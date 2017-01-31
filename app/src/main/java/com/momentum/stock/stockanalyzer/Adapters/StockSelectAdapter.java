package com.momentum.stock.stockanalyzer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.momentum.stock.stockanalyzer.Activities.HomeActivity;
import com.momentum.stock.stockanalyzer.Activities.StockActivity;
import com.momentum.stock.stockanalyzer.Models.Company;
import com.momentum.stock.stockanalyzer.R;

import java.util.ArrayList;

/**
 * Created by kevintong on 16-11-28.
 */
public class StockSelectAdapter extends
        RecyclerView.Adapter<StockSelectAdapter.ViewHolder>{

    Context mContext;
    LayoutInflater mInflater;

    ArrayList<Company> itemList;

    public StockSelectAdapter(Context context, ArrayList<Company> itemList){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        TextView symbolAndNameText;
//        TextView openPriceText;
//        TextView highPriceText;
//        TextView lowPriceText;
//        TextView closePriceText;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.item_company, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.symbolAndNameText = (TextView) view.findViewById(R.id.symbol_and_name);
//        viewHolder.openPriceText = (TextView) view.findViewById(R.id.open_price);
//        viewHolder.highPriceText = (TextView) view.findViewById(R.id.high_price);
//        viewHolder.lowPriceText = (TextView) view.findViewById(R.id.low_price);
//        viewHolder.closePriceText = (TextView) view.findViewById(R.id.close_price);

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
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}


