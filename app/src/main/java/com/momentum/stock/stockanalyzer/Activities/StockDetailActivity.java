package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Global;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by kejuntong on 16-11-27.
 */
public class StockDetailActivity extends Activity {

    TextView openPriceText;
    TextView highPriceText;
    TextView lowPriceText;
    TextView closePriceText;
    TextView priceGainText;
    TextView priceLossText;
    TextView avgGainText;
    TextView avgLossText;
    TextView rsText;
    TextView rsiText;
    TextView sokText;
    TextView sodText;
    TextView williamsRText;
    TextView mfiText;
    TextView fourteenDaysMifText;

    Button favoriteButton;

    List<HistoricalQuote> currentList;
    ArrayList<BigDecimal> gainList;
    ArrayList<BigDecimal> lossList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setFavoriteButton();

        currentList = Global.getInstance().currentList;

        openPriceText = (TextView) findViewById(R.id.open_price);
        highPriceText = (TextView) findViewById(R.id.high_price);
        lowPriceText = (TextView) findViewById(R.id.low_price);
        closePriceText = (TextView) findViewById(R.id.close_price);
        priceGainText = (TextView) findViewById(R.id.price_gain);
        priceLossText = (TextView) findViewById(R.id.price_loss);
        avgGainText = (TextView) findViewById(R.id.avg_gain);
        avgLossText = (TextView) findViewById(R.id.avg_loss);
        rsText = (TextView) findViewById(R.id.rs);
        rsiText = (TextView) findViewById(R.id.rsi);
        sokText = (TextView) findViewById(R.id.sok);
        sodText = (TextView) findViewById(R.id.sod);
        williamsRText = (TextView) findViewById(R.id.william_r);
        mfiText = (TextView) findViewById(R.id.mfi);
        fourteenDaysMifText = (TextView) findViewById(R.id.fourteen_days_mfi);

        // open, high, low, and close prices
        openPriceText.setText(String.valueOf(currentList.get(0).getOpen()));
        highPriceText.setText(String.valueOf(currentList.get(0).getHigh()));
        lowPriceText.setText(String.valueOf(currentList.get(0).getLow()));
        closePriceText.setText(String.valueOf(currentList.get(0).getClose()));

        // calculate gain and loss
        gainList = new ArrayList<>();
        lossList = new ArrayList<>();
        for (int i=0; i<currentList.size()-1; i++){
            BigDecimal thisClosePrice = currentList.get(i).getClose();
            BigDecimal lastClosePrice = currentList.get(i+1).getClose();
            BigDecimal gain;
            BigDecimal loss;

            BigDecimal difference = thisClosePrice.subtract(lastClosePrice);
            if (difference.compareTo(BigDecimal.ZERO) >= 0) {
                gain = difference;
                loss = BigDecimal.ZERO;
            } else {
                loss = difference;
                gain = BigDecimal.ZERO;
            }
            gainList.add(gain);
            lossList.add(loss);
        }

        // gain and loss of this stock
        if (gainList.size() > 0) {
            priceGainText.setText(String.valueOf(gainList.get(0)));
            priceLossText.setText(String.valueOf(lossList.get(0)));
        } else {
            priceGainText.setText("N/A");
            priceLossText.setText("N/A");
        }

        // avg gain and loss
        BigDecimal avgGain = null;
        BigDecimal avgLoss = null;
        if (gainList.size() >= 14){
            BigDecimal totalGain = BigDecimal.ZERO;
            BigDecimal totalLoss = BigDecimal.ZERO;
            for (int i=0; i<14; i++){
                totalGain = totalGain.add(gainList.get(i));
                totalLoss = totalLoss.add(lossList.get(i));
            }
            avgGain = totalGain.divide(BigDecimal.valueOf(14), BigDecimal.ROUND_HALF_UP);
            avgLoss = totalLoss.divide(BigDecimal.valueOf(14), BigDecimal.ROUND_HALF_UP);
            avgGainText.setText(String.valueOf(avgGain));
            avgLossText.setText(String.valueOf(avgLoss));
        }
        else {
            avgGainText.setText("N/A");
            avgLossText.setText("N/A");
        }

        // rs and rsi
        BigDecimal rs = null;
        BigDecimal rsi = null;
        if (avgGain != null && avgLoss.compareTo(BigDecimal.ZERO) != 0){
            rs = avgGain.divide(avgLoss.abs(), BigDecimal.ROUND_HALF_UP);
            if (avgLoss.compareTo(BigDecimal.ZERO) == 0){
                rsi = BigDecimal.valueOf(100);
            } else {
                rsi = BigDecimal.valueOf(100).
                        subtract(BigDecimal.valueOf(100).divide(rs.add(BigDecimal.ONE), BigDecimal.ROUND_HALF_UP));
            }
            rsText.setText(String.valueOf(rs));
            rsiText.setText(String.valueOf(rsi));
        } else {
            rsText.setText("N/A");
            rsiText.setText("N/A");
        }

        // get 3-day sok list
        ArrayList<BigDecimal> sokList = new ArrayList<>();
        if (currentList.size() >= 14){
            BigDecimal closeDay1 = currentList.get(0).getClose();
            BigDecimal sok1 = closeDay1.subtract(getLowestInPastFourteenDays(0)).
                    divide(getHighestInPastFourteenDays(0).subtract(getLowestInPastFourteenDays(0)),
                            BigDecimal.ROUND_HALF_UP);
            sokList.add(sok1);
        }
        if (currentList.size() >= 15){
            BigDecimal closeDay2 = currentList.get(1).getClose();
            BigDecimal sok2 = closeDay2.subtract(getLowestInPastFourteenDays(1)).
                    divide(getHighestInPastFourteenDays(1).subtract(getLowestInPastFourteenDays(1)),
                            BigDecimal.ROUND_HALF_UP);
            sokList.add(sok2);
        }
        if (currentList.size() >= 16){
            BigDecimal closeDay3 = currentList.get(2).getClose();
            BigDecimal sok3 = closeDay3.subtract(getLowestInPastFourteenDays(2)).
                    divide(getHighestInPastFourteenDays(2).subtract(getLowestInPastFourteenDays(2)),
                            BigDecimal.ROUND_HALF_UP);
            sokList.add(sok3);
        }

        // set sok text
        BigDecimal sokValue = null;
        if (sokList.size() > 0){
            sokValue = sokList.get(0).multiply(BigDecimal.valueOf(100));
            sokText.setText(String.valueOf(sokValue) + "%");
        } else {
            sokText.setText("N/A");
        }

        // set sod text
        BigDecimal sodValue = null;
        if (sokList.size() >= 3 ){
            BigDecimal totalSok = BigDecimal.ZERO;
            for (int i=0; i<3; i++){
                totalSok = totalSok.add(sokList.get(i));
            }
            sodValue = totalSok.divide(BigDecimal.valueOf(3), BigDecimal.ROUND_HALF_UP);
            sodText.setText(String.valueOf(sodValue.multiply(BigDecimal.valueOf(100))) + "%");
        }

        // williams
        BigDecimal williams = null;
        if (currentList.size() >= 14){
            williams = currentList.get(0).getClose().subtract(getHighestInPastFourteenDays(0)).
                    divide(getHighestInPastFourteenDays(0).subtract(getLowestInPastFourteenDays(0)), BigDecimal.ROUND_HALF_UP);
            williamsRText.setText(String.valueOf(williams.multiply(BigDecimal.valueOf(100))) + "%");
        } else {
            williamsRText.setText("N/A");
        }

        // calculate all mfi values
        ArrayList<BigDecimal> mfiList = new ArrayList<>();
        for (int i=0; i<currentList.size()-1; i++){
            BigDecimal mfi;
            // high + low + close / 3
            BigDecimal hlcThis = currentList.get(i).getHigh().add(currentList.get(i).getLow()).add(currentList.get(i).getClose()).divide(BigDecimal.valueOf(3), BigDecimal.ROUND_HALF_UP);
            BigDecimal hlcPrevious = currentList.get(i+1).getHigh().add(currentList.get(i+1).getLow()).add(currentList.get(i+1).getClose()).divide(BigDecimal.valueOf(3), BigDecimal.ROUND_HALF_UP);
            if (hlcThis.compareTo(hlcPrevious) > 0 ){
                mfi = hlcThis.multiply(BigDecimal.valueOf(currentList.get(i).getVolume()));
            }
            else if (hlcThis.compareTo(hlcPrevious) < 0){
                mfi = hlcThis.multiply(BigDecimal.valueOf(currentList.get(i).getVolume())).negate();
            }
            else {
                mfi = BigDecimal.ZERO;
            }
            mfiList.add(mfi);
        }
        if (mfiList.size() > 0){
            mfiText.setText(String.valueOf(mfiList.get(0)));
        } else {
            mfiText.setText("N/A");
        }

        // 14 days mfi
        BigDecimal posTotal = BigDecimal.ZERO;
        BigDecimal negTotal = BigDecimal.ZERO;
        if (mfiList.size() >= 14){
            for (int i=0; i<14; i++){
                if (mfiList.get(i).compareTo(BigDecimal.ZERO) > 0){
                    posTotal = posTotal.add(mfiList.get(i));
                } else {
                    negTotal = negTotal.add(mfiList.get(i).abs());
                }
            }
            BigDecimal fourteenDaysMfi = BigDecimal.valueOf(100).subtract(
                    BigDecimal.valueOf(100).divide( BigDecimal.ONE.add(posTotal.divide(negTotal, BigDecimal.ROUND_HALF_UP)), BigDecimal.ROUND_HALF_UP ) );
            fourteenDaysMifText.setText(String.valueOf(fourteenDaysMfi));
        }
        else {
            fourteenDaysMifText.setText("N/A");
        }

    }

    private BigDecimal getLowestInPastFourteenDays(int from){
        BigDecimal lowest = currentList.get(0).getLow();
        for (int i=from; i<from+14; i++){
            BigDecimal value = currentList.get(i).getLow();
            if (lowest.compareTo(value) > 0){
                lowest = value;
            }
        }
        return lowest;
    }

    private BigDecimal getHighestInPastFourteenDays(int from){
        BigDecimal highest = currentList.get(0).getHigh();
        for (int i=from; i<from+14; i++){
            BigDecimal value = currentList.get(i).getHigh();
            if (highest.compareTo(value) < 0){
                highest = value;
            }
        }
        return highest;
    }

    private void setFavoriteButton(){
        favoriteButton = (Button) findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(StockDetailActivity.this)
                        .content("test test add to watch list")
                        .contentColorRes(R.color.white)
                        .backgroundColorRes(R.color.white)
                        .positiveText("ok")
                        .positiveColorRes(R.color.blue);
//                        .onPositive(posListener)
//                        .negativeText(negButtonText)
//                        .negativeColorRes(negButtonColor)
//                        .onNegative(negListener);

                builder.build().show();

            }
        });
    }
}
