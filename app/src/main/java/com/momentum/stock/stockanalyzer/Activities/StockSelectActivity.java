package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;
import com.opencsv.CSVReader;
import com.momentum.stock.stockanalyzer.Adapters.StockSelectAdapter;
import com.momentum.stock.stockanalyzer.Models.Company;
import com.momentum.stock.stockanalyzer.R;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by kevintong on 16-11-28.
 */
public class StockSelectActivity extends Activity {

    RecyclerView mRecyclerView;
    StockSelectAdapter mAdapter;
    ArrayList<Company> allCompanyList;
    ArrayList<Company> searchResultList;

    ImageButton wishListButton;
    EditText searchText;
    ImageButton cancelButton;
    ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stock);

        initViews();

        setMenuButton();

        loadCompanyList("stock_list_all.csv");

        setSearchText();

    }

    private void initViews(){
        wishListButton = (ImageButton) findViewById(R.id.wish_list_button);
        wishListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockSelectActivity.this, WishListActivity.class));
            }
        });
        searchText = (EditText) findViewById(R.id.search_text);
        cancelButton = (ImageButton) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                cancelButton.setVisibility(View.GONE);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        allCompanyList = new ArrayList<>();
        searchResultList = new ArrayList<>();
        mAdapter = new StockSelectAdapter(this, searchResultList);
        mRecyclerView.setAdapter(mAdapter);

        menuButton = (ImageButton) findViewById(R.id.menu_button);
    }

    private void setMenuButton(){
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = getSharedPreferences(Constants.utilSharePreference, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();
                final int searchCategory = sharedPref.getInt(Constants.searchCategoryKey, -1);

                MaterialDialog.Builder builder = new MaterialDialog.Builder(StockSelectActivity.this)
                        .title("Search Category")
                        .titleColorRes(R.color.black)
                        .items("Toronto Stock Exchange (TSE)", "Nasdaq Stock Market (NASDAQ)", "New York Stock Exchange (NYSE)")
                        .itemsColorRes(R.color.black)
                        .widgetColorRes(R.color.blue)
                        .positiveText("SELECT")
                        .itemsCallbackSingleChoice(searchCategory, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                editor.putInt(Constants.searchCategoryKey, which);
                                editor.apply();

                                switch (which){
                                    case 0:
                                        loadCompanyList("stock_list_tsx.csv");
                                        break;
                                    case 1:
                                        loadCompanyList("stock_list_nasdaq.csv");
                                        break;
                                    case 2:
                                        loadCompanyList("stock_list_nyse.csv");
                                        break;
                                }

                                return true;
                            }
                        })
                        .positiveColorRes(R.color.blue);

                builder.build().show();

            }
        });
    }

    private void loadCompanyList(String listName) {
        InputStream csvStream = null;
        InputStreamReader csvStreamReader = null;
        CSVReader csvReader = null;
        AssetManager assetManager = getAssets();
        try {
            csvStream = assetManager.open(listName);
            csvStreamReader = new InputStreamReader(csvStream);
            csvReader = new CSVReader(csvStreamReader, ',');

            String[] line;
            // throw away the header
//            csvReader.readNext();

            allCompanyList.clear();
            searchResultList.clear();

            while ((line = csvReader.readNext()) != null) {
                Company company = new Company(line[0], line[1]);
                allCompanyList.add(company);
                searchResultList.add(company);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(csvReader);
            closeStream(csvStreamReader);
            closeStream(csvStream);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void closeStream(Closeable closeStream) {
        if (closeStream != null)
            try {
                closeStream.close();
                closeStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void setSearchText(){
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                String key = s.toString().trim();
                cancelButton.setVisibility(key.isEmpty() ? View.GONE : View.VISIBLE);
                searchCompany(key);
            }
        });
    }

    private void searchCompany(String keyWord){

        searchResultList.clear();
        mAdapter.notifyDataSetChanged();

        for (Company company : allCompanyList) {
            if (company.getSymbol().toUpperCase().contains(keyWord.toUpperCase()) ||
                    company.getName().toUpperCase().contains(keyWord.toUpperCase() )) {
                searchResultList.add(company);
            }
        }

        if (searchResultList.size() > 0){
            mAdapter.notifyDataSetChanged();
        }

    }

}
