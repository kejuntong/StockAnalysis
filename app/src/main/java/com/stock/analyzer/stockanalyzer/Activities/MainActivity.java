package com.stock.analyzer.stockanalyzer.Activities;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.stock.analyzer.stockanalyzer.Adapters.StockHistoryAdapter;
import com.stock.analyzer.stockanalyzer.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class MainActivity extends Activity {

    RecyclerView mRecyclerView;
    StockHistoryAdapter mAdapter;
    ArrayList<HistoricalQuote> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        historyList = new ArrayList<>();
        mAdapter = new StockHistoryAdapter(this, historyList);

        mRecyclerView.setAdapter(mAdapter);



        final Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.add(Calendar.MONTH, -1);
        calendarFrom.set(Calendar.DATE, 1);

        final Calendar calendarTo = Calendar.getInstance();



        Thread thread = new Thread(){
            @Override
            public void run() {
                Stock tesla = null;
                try {
                    tesla = YahooFinance.get("TSLA", true);
//                    System.out.println(tesla.getHistory(calendarFrom, calendarTo, Interval.DAILY));
                    List<HistoricalQuote> list = tesla.getHistory(calendarFrom, calendarTo, Interval.DAILY);
                    for (HistoricalQuote item : list){
                        historyList.add(item);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Handler(MainActivity.this.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        };

        thread.start();

    }
}
