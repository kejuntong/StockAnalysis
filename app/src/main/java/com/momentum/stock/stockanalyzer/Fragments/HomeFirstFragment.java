package com.momentum.stock.stockanalyzer.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.momentum.stock.stockanalyzer.Activities.HomeActivity;
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

public class HomeFirstFragment extends BaseFragment {

    View fragmentView;

    TextView titleText;
    Button favoriteButton;

//    RecyclerView mRecyclerView;
//    StockHistoryAdapter mAdapter;
    ArrayList<HistoricalQuote> historyList;

    TextView currentPriceText;
    ImageView upArrow;
    ImageView downArrow;
    TextView lastUpdateText;
    ImageView refreshButton;
    ProgressBar refreshSpinner;
    TextView changePercentageText;
    TextView changeValueText;
    TextView openPriceText;
    TextView volumeText;
    TextView dayHighText;
    TextView dayLowText;

    String stockSymbol;
    String stockName;

    LineChart lineChart;

    Handler mainThreadHandler;

    public HomeFirstFragment(){
        setFragmentName(Constants.DAY_DATA_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_home_first, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        stockSymbol = ((HomeActivity) getActivity()).getSelectedStockSymbol();
        stockName = ((HomeActivity) getActivity()).getSelectedStockName();
        titleText = (TextView) fragmentView.findViewById(R.id.title_text);
        titleText.setText(stockSymbol + ", " + stockName);

        setFavoriteButton();

        initViews();

        pullData(stockSymbol);

        setRefreshButton();

        setAdvertisement();
    }

    private void initViews(){
        historyList = new ArrayList<>();

        currentPriceText = (TextView) fragmentView.findViewById(R.id.latest_price);
        upArrow = (ImageView) fragmentView.findViewById(R.id.arrow_image_up);
        downArrow = (ImageView) fragmentView.findViewById(R.id.arrow_image_down);
        lastUpdateText = (TextView) fragmentView.findViewById(R.id.last_update_text);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMM.dd, HH:mm:ss", Locale.US);
        String strDate = dateFormat.format(date);
        lastUpdateText.setText(strDate);

        changePercentageText = (TextView) fragmentView.findViewById(R.id.latest_change_percentage);
        volumeText = (TextView) fragmentView.findViewById(R.id.latest_volume);
        changeValueText = (TextView) fragmentView.findViewById(R.id.latest_change_value);
        openPriceText = (TextView) fragmentView.findViewById(R.id.latest_open_price);
        dayHighText = (TextView) fragmentView.findViewById(R.id.latest_day_high);
        dayLowText = (TextView) fragmentView.findViewById(R.id.latest_day_low);
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

                    // current data
                    final StockQuote stockQuote = stock.getQuote();
                    updateCurrentInfoSection(stockQuote);

                    // history data
                    historyList.clear();
                    for (HistoricalQuote item : ((HomeActivity) getActivity()).getInitialDataList()) {
                        historyList.add(item);
                    }

                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
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

    public void updateCurrentInfoSection(final StockQuote stockQuote){
        new Handler(getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (stockQuote.getChange().compareTo(BigDecimal.ZERO) > 0){
                    currentPriceText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                    changePercentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                    changeValueText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                    upArrow.setVisibility(View.VISIBLE);
                    downArrow.setVisibility(View.GONE);
                } else if (stockQuote.getChange().compareTo(BigDecimal.ZERO) < 0){
                    currentPriceText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    changePercentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    changeValueText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    downArrow.setVisibility(View.VISIBLE);
                    upArrow.setVisibility(View.GONE);
                } else {
                    currentPriceText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                    changePercentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                    changePercentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                    downArrow.setVisibility(View.GONE);
                    upArrow.setVisibility(View.GONE);
                }
                currentPriceText.setText("" + stockQuote.getPrice());
                changePercentageText.setText("" + stockQuote.getChangeInPercent() + "%");
                volumeText.setText("" + NumberFormat.getNumberInstance(Locale.US).format(stockQuote.getVolume()));
                changeValueText.setText("" + stockQuote.getChange());
                openPriceText.setText("" + stockQuote.getOpen());
                dayHighText.setText("" + stockQuote.getDayHigh());
                dayLowText.setText("" + stockQuote.getDayLow());

                refreshButton.setVisibility(View.VISIBLE);
                refreshSpinner.setVisibility(View.INVISIBLE);

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MMM.dd, HH:mm:ss", Locale.US);
                String strDate = dateFormat.format(date);
                lastUpdateText.setText(strDate);

//                System.out.println("test date 1: " + stockQuote.getLastTradeTime().get(Calendar.DAY_OF_YEAR));
//                System.out.println("test date 2: " + Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
            }
        });
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

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.view_my_marker, historyList);
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

    public void setRefreshButton(){
        refreshButton = (ImageView) fragmentView.findViewById(R.id.button_refresh);
        refreshSpinner = (ProgressBar) fragmentView.findViewById(R.id.refresh_spinner);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButton.setVisibility(View.INVISIBLE);
                refreshSpinner.setVisibility(View.VISIBLE);

                new Thread(){
                    @Override
                    public void run() {
                        Stock stock = null;
                        try {
                            stock = YahooFinance.get(stockSymbol, false);

                            // current data
                            final StockQuote stockQuote = stock.getQuote();
                            updateCurrentInfoSection(stockQuote);

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
                }.start();
            }
        });
    }

    private void setAdvertisement(){
        AdView mAdView = (AdView) fragmentView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
