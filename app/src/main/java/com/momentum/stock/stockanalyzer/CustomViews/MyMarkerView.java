
package com.momentum.stock.stockanalyzer.CustomViews;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.momentum.stock.stockanalyzer.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    ArrayList<HistoricalQuote> historyList;

    public MyMarkerView(Context context, int layoutResource, ArrayList<HistoricalQuote> historyList) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
        this.historyList = historyList;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText("" + ce.getHigh());
        } else {

            HistoricalQuote historicalQuote = historyList.get(historyList.size() - 1 -(int)e.getX());
            Date date = historicalQuote.getDate().getTime();
            DateFormat dateFormat = new SimpleDateFormat("MMM.dd", Locale.US);
            String dateString = dateFormat.format(date);

            tvContent.setText(dateString + "\n" + e.getY());
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
