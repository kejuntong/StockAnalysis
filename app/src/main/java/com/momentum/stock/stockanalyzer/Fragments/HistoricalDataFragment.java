package com.momentum.stock.stockanalyzer.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.momentum.stock.stockanalyzer.Activities.HomeActivity;
import com.momentum.stock.stockanalyzer.Adapters.StockHistoryAdapter;
import com.momentum.stock.stockanalyzer.CustomViews.MyMarkerView;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by kevintong on 2017-01-31.
 */

public class HistoricalDataFragment extends BaseFragment {

    View fragmentView;

    TextView titleText;
    Button favoriteButton;

    RecyclerView mRecyclerView;
    StockHistoryAdapter mAdapter;
    ArrayList<HistoricalQuote> historyList;

    String stockSymbol;
    String stockName;

    LineChart lineChart;

    Handler mainThreadHandler;

    public HistoricalDataFragment(){
        setFragmentName(Constants.HISTORICAL_DATA_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_historical_data, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stockSymbol = ((HomeActivity) getActivity()).getSelectedStockSymbol();
        stockName = ((HomeActivity) getActivity()).getSelectedStockName();
        titleText = (TextView) fragmentView.findViewById(R.id.title_text);
        titleText.setText(stockSymbol + ", " + stockName);

        fragmentView.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setFavoriteButton();

        initViews();

        pullData(stockSymbol);

    }

    private void initViews(){
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        historyList = new ArrayList<>();
        mAdapter = new StockHistoryAdapter(getActivity(), historyList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void pullData(final String stockId){

        mainThreadHandler = new Handler(getActivity().getMainLooper());

        fragmentView.findViewById(R.id.loading_layer).setVisibility(View.VISIBLE);
        Thread thread = new Thread(){
            @Override
            public void run() {
                Stock stock = null;
                try {
                    stock = YahooFinance.get(stockId, true);

                    // history data
                    Calendar calendarFrom = Calendar.getInstance();
                    calendarFrom.add(Calendar.MONTH, -2);
                    calendarFrom.set(Calendar.DATE, 1);
                    Calendar calendarTo = Calendar.getInstance();
                    List<HistoricalQuote> list = stock.getHistory(calendarFrom, calendarTo, Interval.DAILY);
                    historyList.clear();
                    for (HistoricalQuote item : list){
                        historyList.add(item);
                    }

                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            fragmentView.findViewById(R.id.loading_layer).setVisibility(View.GONE);

                            setLineChart();

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    new Handler(getActivity().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        };

        thread.start();
    }

    public void setLineChart(){
        lineChart = (LineChart) fragmentView.findViewById(R.id.line_chart);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        Description description = new Description();
        description.setText("data retrieved from Yahoo Finance");
        description.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));
        lineChart.setDescription(description);

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.view_my_marker);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv); // Set the marker to the chart

        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
//        xAxis.setAxisMinimum(0f);
//        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                HistoricalQuote historicalQuote = historyList.get(historyList.size() - 1 -(int)value);
                Date date = historicalQuote.getDate().getTime();
                DateFormat dateFormat = new SimpleDateFormat("MMM.dd", Locale.US);
                return dateFormat.format(date);
            }
        });

        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisLeft.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));
        yAxisRight.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));

        Legend legend = lineChart.getLegend();
        legend.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));

        List<Entry> entries = new ArrayList<Entry>();
        for (int i=0; i<historyList.size(); i++) {
            HistoricalQuote historicalQuote = historyList.get(historyList.size()-1-i);
            entries.add(new Entry(i, historicalQuote.getClose().floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Close Price"); // add entries to dataset
        dataSet.enableDashedLine(10f, 5f, 0f);
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.orange));
        dataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
//        dataSet.setValueTextSize(9f);
//        dataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.red));
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh
    }

    private void setFavoriteButton(){
        favoriteButton = (Button) fragmentView.findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                        .content("Add this stock to your Portfolio" )
                        .contentColorRes(R.color.black)
                        .backgroundColorRes(R.color.white)
                        .positiveText("ADD")
                        .positiveColorRes(R.color.blue)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.wishListSharePreference, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();

                                String wishListString = sharedPref.getString(Constants.wishListKey, "");
                                String newItem = stockSymbol + ", " + stockName;
                                if (!wishListString.contains(newItem)) {
                                    wishListString = wishListString + newItem + ";";
                                    editor.putString(Constants.wishListKey, wishListString);
                                    editor.apply();
                                } else {
                                    Toast.makeText(getActivity(), "You already have this stock in your Portfolio", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).negativeText("Cancel")
                        .negativeColorRes(R.color.red);

                builder.build().show();

            }
        });
    }

}
