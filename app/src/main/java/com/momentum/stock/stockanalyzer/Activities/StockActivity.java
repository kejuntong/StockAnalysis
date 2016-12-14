package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.momentum.stock.stockanalyzer.Adapters.StockHistoryAdapter;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class StockActivity extends Activity {

    TextView titleText;
    Button favoriteButton;

    RecyclerView mRecyclerView;
    StockHistoryAdapter mAdapter;
    ArrayList<HistoricalQuote> historyList;

    String stockSymbol;
    String stockName;

    LineChartView lineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        stockSymbol = getIntent().getExtras().getString("symbol");
        stockName = getIntent().getExtras().getString("name");
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(stockSymbol + ", " + stockName);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setFavoriteButton();

        // init recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        historyList = new ArrayList<>();
        mAdapter = new StockHistoryAdapter(this, historyList);
        mRecyclerView.setAdapter(mAdapter);

        pullData(stockSymbol);

    }

    private void pullData(final String stockId){
        final Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.add(Calendar.MONTH, -2);
        calendarFrom.set(Calendar.DATE, 1);

        final Calendar calendarTo = Calendar.getInstance();

        findViewById(R.id.loading_layer).setVisibility(View.VISIBLE);
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

                    new Handler(StockActivity.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            findViewById(R.id.loading_layer).setVisibility(View.GONE);

//                            drawChart();

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    new Handler(StockActivity.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StockActivity.this, "No data found", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        };

        thread.start();
    }

    private void setFavoriteButton(){
        favoriteButton = (Button) findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(StockActivity.this)
                        .content("Add this stock to your Portfolio" )
                        .contentColorRes(R.color.black)
                        .backgroundColorRes(R.color.white)
                        .positiveText("ADD")
                        .positiveColorRes(R.color.blue)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SharedPreferences sharedPref = getSharedPreferences(Constants.wishListSharePreference, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();

                                String wishListString = sharedPref.getString(Constants.wishListKey, "");
                                String newItem = stockSymbol + ", " + stockName;
                                if (!wishListString.contains(newItem)) {
                                    wishListString = wishListString + newItem + ";";
                                    editor.putString(Constants.wishListKey, wishListString);
                                    editor.apply();
                                } else {
                                    Toast.makeText(StockActivity.this, "You already have this stock in your Portfolio", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).negativeText("Cancel")
                        .negativeColorRes(R.color.red);

                builder.build().show();

            }
        });
    }

    public String getStockSymbol(){
        return this.stockSymbol;
    }
    public String getStockName(){
        return this.stockName;
    }

//    public void drawChart(){
//        lineChartView = (LineChartView) findViewById(R.id.chart);
//        List<PointValue> values = new ArrayList<PointValue>();
//        for (int i=0; i<historyList.size(); i++) {
//            values.add(new PointValue(i, historyList.get(i).getClose().floatValue()));
//        }
//
//        //In most cased you can call data model methods in builder-pattern-like manner.
//        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
//        List<Line> lines = new ArrayList<Line>();
//        lines.add(line);
//
//        LineChartData data = new LineChartData();
//        data.setLines(lines);
//
//        lineChartView.setLineChartData(data);
//    }
}
