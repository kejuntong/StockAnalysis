package com.stock.analyzer.stockanalyzer.Activities;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.opencsv.CSVReader;
import com.stock.analyzer.stockanalyzer.Adapters.StockSelectAdapter;
import com.stock.analyzer.stockanalyzer.Models.Company;
import com.stock.analyzer.stockanalyzer.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stock);

        initViews();

        loadCompanyList();

    }

    private void initViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        allCompanyList = new ArrayList<>();
        mAdapter = new StockSelectAdapter(this, allCompanyList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadCompanyList() {
        InputStream csvStream = null;
        InputStreamReader csvStreamReader = null;
        CSVReader csvReader = null;
        AssetManager assetManager = getAssets();
        try {
            csvStream = assetManager.open("company_list.csv");
            csvStreamReader = new InputStreamReader(csvStream);
            csvReader = new CSVReader(csvStreamReader, ',');

            String[] line;
            // throw away the header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                Company school = new Company(line[0], line[1]);
                allCompanyList.add(school);
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
}
