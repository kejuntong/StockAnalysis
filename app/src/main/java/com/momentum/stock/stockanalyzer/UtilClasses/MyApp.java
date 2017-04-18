package com.momentum.stock.stockanalyzer.UtilClasses;

import android.app.Application;

/**
 * Created by kevintong on 2017-04-18.
 */

public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        System.setProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");
    }
}
