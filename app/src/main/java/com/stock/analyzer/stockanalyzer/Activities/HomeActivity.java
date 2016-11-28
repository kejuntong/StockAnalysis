package com.stock.analyzer.stockanalyzer.Activities;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stock.analyzer.stockanalyzer.Adapters.StockHistoryAdapter;
import com.stock.analyzer.stockanalyzer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class HomeActivity extends Activity {

    RecyclerView mRecyclerView;
    StockHistoryAdapter mAdapter;
    ArrayList<HistoricalQuote> historyList;

    Button okButton;
    EditText enterStockText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // init recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        historyList = new ArrayList<>();
        mAdapter = new StockHistoryAdapter(this, historyList);
        mRecyclerView.setAdapter(mAdapter);

        // init button and edit text
        enterStockText = (EditText) findViewById(R.id.enter_stock_text);
        okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockId = enterStockText.getText().toString().trim();
                if (!stockId.isEmpty()) {
                    pullData(stockId);
                }
            }
        });


    }

    private void pullData(final String stockId){
        final Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.add(Calendar.MONTH, -1);
        calendarFrom.set(Calendar.DATE, 1);

        final Calendar calendarTo = Calendar.getInstance();

        Thread thread = new Thread(){
            @Override
            public void run() {
                Stock stock = null;
                try {
//                    tesla = YahooFinance.get("TSLA", true);
                    stock = YahooFinance.get(stockId, true);
//                    System.out.println(tesla.getHistory(calendarFrom, calendarTo, Interval.DAILY));
                    List<HistoricalQuote> list = stock.getHistory(calendarFrom, calendarTo, Interval.DAILY);
                    historyList.clear();
                    for (HistoricalQuote item : list){
                        historyList.add(item);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Handler(HomeActivity.this.getMainLooper()).post(new Runnable() {
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
