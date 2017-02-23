package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.momentum.stock.stockanalyzer.Adapters.MyPortfolioAdapter;
import com.momentum.stock.stockanalyzer.Models.Company;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;
import com.momentum.stock.stockanalyzer.UtilClasses.OnItemClickedCallbackInterface;

import java.util.ArrayList;

/**
 * Created by kejuntong on 2016-12-04.
 */
public class MyPortfolioActivity extends Activity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String wishListString;

    Button addButton;
    ImageButton refreshButton;
    RelativeLayout emptyScreen;

    RecyclerView mRecyclerView;
    MyPortfolioAdapter mAdapter;
    ArrayList<Company> wishArrayList;

    boolean isOnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_portfolio);

        isOnCreate = true;

        setEmptyScreen();

        addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPortfolioActivity.this, StockSelectActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        setRecyclerView();

        refreshButton = (ImageButton) findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.notifyDataSetChanged();
            }
        });

        setAdvertisement();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnCreate){
            isOnCreate = false;
        } else {
            loadListAndPullData();
        }
    }

    private void setEmptyScreen(){
        emptyScreen = (RelativeLayout) findViewById(R.id.empty_screen);
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPortfolioActivity.this, StockSelectActivity.class));
            }
        });
    }

    private void setRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        wishArrayList = new ArrayList<>();
        mAdapter = new MyPortfolioAdapter(this, wishArrayList);
        mRecyclerView.setAdapter(mAdapter);

        sharedPref = getSharedPreferences(Constants.wishListSharePreference, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        loadListAndPullData();

        mAdapter.setOnItemLongClickListener(new OnItemClickedCallbackInterface() {
            @Override
            public void onCallback(View view, final int position, Object object) {

                final Company company = (Company) object;

                MaterialDialog.Builder builder = new MaterialDialog.Builder(MyPortfolioActivity.this)
                        .content("Remove " +  company.getName() + " from Portfolio")
                        .contentColorRes(R.color.black)
                        .backgroundColorRes(R.color.white)
                        .positiveText("Remove")
                        .positiveColorRes(R.color.red)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                wishArrayList.remove(company);
                                mAdapter.notifyItemRemoved(position);
                                mAdapter.notifyItemRangeChanged(position, wishArrayList.size());

                                String stringToRemove = company.getSymbol() + "," + company.getName() + ";";
                                wishListString = wishListString.replace(stringToRemove, "");
                                editor.putString(Constants.wishListKey, wishListString);
                                editor.apply();

                                if (wishArrayList.size() == 0){
                                    emptyScreen.setVisibility(View.VISIBLE);
                                } else {
                                    emptyScreen.setVisibility(View.GONE);
                                }

                            }
                        }).negativeText("Cancel")
                        .negativeColorRes(R.color.blue);

                builder.build().show();

            }
        });
    }

    private void loadListAndPullData(){
        sharedPref = getSharedPreferences(Constants.wishListSharePreference, Context.MODE_PRIVATE);
        wishListString = sharedPref.getString(Constants.wishListKey, "");
        if (!wishListString.isEmpty()) {
            String[] array = wishListString.split(";");
            emptyScreen.setVisibility(View.GONE);
            wishArrayList.clear();
            for (int i=0; i<array.length; i++){
                if (!array[i].isEmpty()){
                    String symbol = array[i].split(",")[0];
                    String name = array[i].split(",")[1];
                    wishArrayList.add(new Company(symbol, name));
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            emptyScreen.setVisibility(View.VISIBLE);
        }
    }

    private void setAdvertisement(){
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
