package com.momentum.stock.stockanalyzer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.momentum.stock.stockanalyzer.Activities.HomeActivity;
import com.momentum.stock.stockanalyzer.Models.Company;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.OnItemClickedCallbackInterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by kevintong on 16-11-28.
 */
public class MyPortfolioAdapter extends
        RecyclerView.Adapter<MyPortfolioAdapter.ViewHolder>{

    Context mContext;
    LayoutInflater mInflater;

    ArrayList<Company> itemList;

    OnItemClickedCallbackInterface onItemLongClickListener;
    Handler mainThreadHandler;

    public MyPortfolioAdapter(Context context, ArrayList<Company> itemList){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;

        this.mainThreadHandler = new Handler(mContext.getMainLooper());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView symbolAndNameText;
        TextView priceText;
        ProgressBar spinner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_my_portfolio, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.symbolAndNameText = (TextView) view.findViewById(R.id.symbol_and_name);
        viewHolder.priceText = (TextView) view.findViewById(R.id.price_text);
        viewHolder.spinner = (ProgressBar) view.findViewById(R.id.spinner);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Company item = itemList.get(position);
        holder.symbolAndNameText.setText(item.getSymbol() + ", " + item.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("symbol", itemList.get(position).getSymbol());
                intent.putExtra("name", itemList.get(position).getName());
                mContext.startActivity(intent);
            }
        });

        holder.spinner.setVisibility(View.VISIBLE);
        holder.priceText.setVisibility(View.INVISIBLE);
        new Thread(){
            @Override
            public void run() {
                try {
                    Stock stock = YahooFinance.get(itemList.get(position).getSymbol(), true);

                    // current data
                    final StockQuote stockQuote = stock.getQuote();

                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.spinner.setVisibility(View.GONE);
                            holder.priceText.setVisibility(View.VISIBLE);
                            if (stockQuote.getChange().compareTo(BigDecimal.ZERO) < 0) {
                                holder.priceText.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                            } else if (stockQuote.getChange().compareTo(BigDecimal.ZERO) > 0){
                                holder.priceText.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                            } else {
                                holder.priceText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                            }
                            holder.priceText.setText(String.valueOf(stockQuote.getPrice()));
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.priceText.setText("");
                        }
                    });
                }

            }
        }.start();

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (onItemLongClickListener != null){
                    onItemLongClickListener.onCallback(view, position, itemList.get(position));
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemLongClickListener(OnItemClickedCallbackInterface cbi){
        this.onItemLongClickListener = cbi;
    }
}


