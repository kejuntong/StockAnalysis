package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.momentum.stock.stockanalyzer.Adapters.StockSelectAdapter;
import com.momentum.stock.stockanalyzer.Models.Company;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;

import java.util.ArrayList;

/**
 * Created by kejuntong on 2016-12-04.
 */
public class WishListActivity extends Activity {

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

        SharedPreferences sharedPref = getSharedPreferences(Constants.wishListSharePreference, Context.MODE_PRIVATE);
        String wishListString = sharedPref.getString(Constants.wishListKey, "");
        if (!wishListString.isEmpty()) {
            System.out.println("testttt: " + wishListString);
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
    }
}
