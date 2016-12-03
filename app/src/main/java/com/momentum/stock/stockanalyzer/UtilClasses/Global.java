package com.momentum.stock.stockanalyzer.UtilClasses;

import java.util.ArrayList;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by kejuntong on 16-11-27.
 */
public class Global {

    private static Global instance;
    public static Global getInstance() {
        if (instance == null) {
            instance = new Global();
        }
        return instance;
    }

    public List<HistoricalQuote> currentList = new ArrayList<>();

}
