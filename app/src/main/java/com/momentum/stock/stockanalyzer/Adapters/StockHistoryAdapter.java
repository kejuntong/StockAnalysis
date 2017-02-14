package com.momentum.stock.stockanalyzer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.momentum.stock.stockanalyzer.Activities.StockActivity;
import com.momentum.stock.stockanalyzer.Activities.StockDetailActivity;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;
import com.momentum.stock.stockanalyzer.UtilClasses.Global;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by kejuntong on 16-11-13.
 */
public class StockHistoryAdapter extends
        RecyclerView.Adapter<StockHistoryAdapter.ViewHolder>{

    Context mContext;
    LayoutInflater mInflater;

    ArrayList<HistoricalQuote> itemList;

    public StockHistoryAdapter(Context context, ArrayList<HistoricalQuote> itemList){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView dateText;
        TextView openPriceText;
        TextView highPriceText;
        TextView lowPriceText;
        TextView closePriceText;
        TextView volumeText;

        ImageView indicatorCircle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_stock_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.dateText = (TextView) view.findViewById(R.id.date);
        viewHolder.openPriceText = (TextView) view.findViewById(R.id.open_price);
        viewHolder.highPriceText = (TextView) view.findViewById(R.id.high_price);
        viewHolder.lowPriceText = (TextView) view.findViewById(R.id.low_price);
        viewHolder.closePriceText = (TextView) view.findViewById(R.id.close_price);
        viewHolder.volumeText = (TextView) view.findViewById(R.id.volume);

        viewHolder.indicatorCircle = (ImageView) view.findViewById(R.id.circle_indicator);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        HistoricalQuote item = itemList.get(position);

        // date
        Calendar calender = item.getDate();
        Date date = calender.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MMM.dd\nyyyy", Locale.US);
        String strDate = dateFormat.format(date);
        holder.dateText.setText(strDate);

        // open price
        BigDecimal openPrice = item.getOpen();
//                .setScale(6, BigDecimal.ROUND_CEILING);
        holder.openPriceText.setText(String.valueOf(openPrice));

        // high price
        holder.highPriceText.setText(String.valueOf(item.getHigh()));
//                .setScale(6, BigDecimal.ROUND_CEILING)));

        // low price
        holder.lowPriceText.setText(String.valueOf(item.getLow()));
//                .setScale(6, BigDecimal.ROUND_CEILING)));

        // close price
        BigDecimal closePrice = item.getClose();
//                .setScale(6, BigDecimal.ROUND_CEILING);
        holder.closePriceText.setText(String.valueOf(closePrice));

        // volume
        holder.volumeText.setText(NumberFormat.getNumberInstance(Locale.US).format(item.getVolume()));

        // setting indicator
        if (closePrice.subtract(openPrice).compareTo(BigDecimal.ZERO) < 0){
            holder.indicatorCircle.setImageResource(R.drawable.circle_red);
        } else if (closePrice.subtract(openPrice).compareTo(BigDecimal.ZERO) > 0) {
            holder.indicatorCircle.setImageResource(R.drawable.circle_green);
        } else {
            holder.indicatorCircle.setImageResource(R.drawable.circle_white);
        }

//        // go to detail
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Global.getInstance().currentList = itemList.subList(position, itemList.size());
//                Intent intent = new Intent(mContext, StockDetailActivity.class);
//                intent.putExtra("symbol", ( (StockActivity) mContext).getStockSymbol());
//                intent.putExtra("name", ( (StockActivity) mContext).getStockName());
//                mContext.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}

