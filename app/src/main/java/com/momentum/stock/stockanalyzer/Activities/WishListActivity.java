package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.momentum.stock.stockanalyzer.Adapters.StockSelectAdapter;
import com.momentum.stock.stockanalyzer.Models.Company;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.CallbackInterface;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;
import com.momentum.stock.stockanalyzer.UtilClasses.OnItemClickedCallbackInterface;

import java.util.ArrayList;

/**
 * Created by kejuntong on 2016-12-04.
 */
public class WishListActivity extends Activity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String wishListString;

    RecyclerView mRecyclerView;
    StockSelectAdapter mAdapter;
    ArrayList<Company> wishArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        wishArrayList = new ArrayList<>();
        mAdapter = new StockSelectAdapter(this, wishArrayList);
        mRecyclerView.setAdapter(mAdapter);

        sharedPref = getSharedPreferences(Constants.wishListSharePreference, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        wishListString = sharedPref.getString(Constants.wishListKey, "");
        if (!wishListString.isEmpty()) {
            String[] array = wishListString.split(";");
            for (int i=0; i<array.length; i++){
                if (!array[i].isEmpty()){
                    String symbol = array[i].split(",")[0];
                    String name = array[i].split(",")[1];
                    wishArrayList.add(new Company(symbol, name));
                }
            }
            mAdapter.notifyDataSetChanged();
        }


        mAdapter.setOnItemLongClickListener(new OnItemClickedCallbackInterface() {
            @Override
            public void onCallback(View view, final int position, Object object) {

                final Company company = (Company) object;

                MaterialDialog.Builder builder = new MaterialDialog.Builder(WishListActivity.this)
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
                            }
                        }).negativeText("Cancel")
                        .negativeColorRes(R.color.blue);

                builder.build().show();

            }
        });
    }
}
