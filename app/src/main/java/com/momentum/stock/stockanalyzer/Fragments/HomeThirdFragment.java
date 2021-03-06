package com.momentum.stock.stockanalyzer.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.momentum.stock.stockanalyzer.Activities.HomeActivity;
import com.momentum.stock.stockanalyzer.CustomViews.CustomColorBarIndicator;
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
import java.util.List;
import java.util.Locale;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Created by kevintong on 2017-01-31.
 */

public class HomeThirdFragment extends BaseFragment {

    View fragmentView;

//    TextView openPriceText;
//    TextView highPriceText;
//    TextView lowPriceText;
//    TextView closePriceText;
//    TextView volumeText;
    TextView priceGainText;
    TextView priceLossText;
    TextView avgGainText;
    TextView avgLossText;
    TextView rsText;
    TextView rsiText;
    TextView rsiSignalText;
    TextView sokText;
    TextView sokSignalText;
    TextView sodText;
    TextView sodSignalText;
    TextView williamsRText;
    TextView williamsRSignalText;
    TextView mfiText;
    TextView mfiSignalText;
    TextView signalText;

    TextView titleTextView;
    TextView sectionOneTitle;
    Button favoriteButton;
    CustomColorBarIndicator rsiIndicator;
    CustomColorBarIndicator sokIndicator;
    CustomColorBarIndicator sodIndicator;
    CustomColorBarIndicator williamIndicator;
    CustomColorBarIndicator mfiIndicator;

    List<HistoricalQuote> currentList;
    ArrayList<BigDecimal> gainList;
    ArrayList<BigDecimal> lossList;

    String symbol;
    String name;

    public HomeThirdFragment(){
        setFragmentName(Constants.INDICATORS_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_home_third, container, false);

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

        setFavoriteButton();

        currentList = new ArrayList<>();
        for (HistoricalQuote item : ((HomeActivity) getActivity()).getInitialDataList()) {
            currentList.add(item);
        }

        titleTextView = (TextView) fragmentView.findViewById(R.id.title_text);
        titleTextView.setText(symbol + ", " + name);

        sectionOneTitle = (TextView) fragmentView.findViewById(R.id.first_section_title);
        Calendar calender = currentList.get(0).getDate();
        Date date = calender.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MMM.dd, yyyy", Locale.US);
        String strDate = dateFormat.format(date);
        sectionOneTitle.setText("Market Info - " + strDate);

//        openPriceText = (TextView) fragmentView.findViewById(R.id.open_price);
//        highPriceText = (TextView) fragmentView.findViewById(R.id.high_price);
//        lowPriceText = (TextView) fragmentView.findViewById(R.id.low_price);
//        closePriceText = (TextView) fragmentView.findViewById(R.id.close_price);
//        volumeText = (TextView) fragmentView.findViewById(R.id.volume);
        priceGainText = (TextView) fragmentView.findViewById(R.id.price_gain);
        priceLossText = (TextView) fragmentView.findViewById(R.id.price_loss);
        avgGainText = (TextView) fragmentView.findViewById(R.id.avg_gain);
        avgLossText = (TextView) fragmentView.findViewById(R.id.avg_loss);
        rsText = (TextView) fragmentView.findViewById(R.id.rs);
        
        rsiText = (TextView) fragmentView.findViewById(R.id.rsi);
        rsiSignalText = (TextView) fragmentView.findViewById(R.id.rsi_signal_text);
        rsiIndicator = (CustomColorBarIndicator) fragmentView.findViewById(R.id.indicator_rsi);
        
        sokText = (TextView) fragmentView.findViewById(R.id.sok);
        sokSignalText = (TextView) fragmentView.findViewById(R.id.sok_signal_text);
        sokIndicator = (CustomColorBarIndicator) fragmentView.findViewById(R.id.indicator_sok); 
        
        sodText = (TextView) fragmentView.findViewById(R.id.sod);
        sodSignalText = (TextView) fragmentView.findViewById(R.id.sod_signal_text);
        sodIndicator = (CustomColorBarIndicator) fragmentView.findViewById(R.id.indicator_sod); 
        
        williamsRText = (TextView) fragmentView.findViewById(R.id.william_r);
        williamsRSignalText = (TextView) fragmentView.findViewById(R.id.william_signal_text);
        williamIndicator = (CustomColorBarIndicator) fragmentView.findViewById(R.id.indicator_william); 
        
        mfiText = (TextView) fragmentView.findViewById(R.id.fourteen_days_mfi);
        mfiSignalText = (TextView) fragmentView.findViewById(R.id.mfi_signal_text);
        mfiIndicator = (CustomColorBarIndicator) fragmentView.findViewById(R.id.indicator_mfi);

        signalText = (TextView) fragmentView.findViewById(R.id.signal);

//        // open, high, low, close prices, and volume
//        openPriceText.setText(String.valueOf(currentList.get(0).getOpen().setScale(2, BigDecimal.ROUND_HALF_UP)));
//        highPriceText.setText(String.valueOf(currentList.get(0).getHigh().setScale(2, BigDecimal.ROUND_HALF_UP)));
//        lowPriceText.setText(String.valueOf(currentList.get(0).getLow().setScale(2, BigDecimal.ROUND_HALF_UP)));
//        closePriceText.setText(String.valueOf(currentList.get(0).getClose().setScale(2, BigDecimal.ROUND_HALF_UP)));
//        volumeText.setText(NumberFormat.getNumberInstance(Locale.US).format(currentList.get(0).getVolume()));

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
            priceGainText.setText(String.valueOf(gainList.get(0).setScale(2, BigDecimal.ROUND_HALF_UP)));
            priceLossText.setText(String.valueOf(lossList.get(0).setScale(2, BigDecimal.ROUND_HALF_UP)));

            if (gainList.get(0).compareTo(BigDecimal.ZERO) == 0) {
                fragmentView.findViewById(R.id.gain_line).setVisibility(View.GONE);
            }
            if (lossList.get(0).compareTo(BigDecimal.ZERO) == 0) {
                fragmentView.findViewById(R.id.loss_line).setVisibility(View.GONE);
            }

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
            avgGain = totalGain.divide(BigDecimal.valueOf(14), 6, BigDecimal.ROUND_HALF_UP);
            avgLoss = totalLoss.divide(BigDecimal.valueOf(14), 6, BigDecimal.ROUND_HALF_UP);
            avgGainText.setText(String.valueOf(avgGain.setScale(2, BigDecimal.ROUND_HALF_UP)));
            avgLossText.setText(String.valueOf(avgLoss.setScale(2, BigDecimal.ROUND_HALF_UP)));
        }
        else {
            avgGainText.setText("N/A");
            avgLossText.setText("N/A");
        }

        // rs and rsi
        BigDecimal rs = null;
        BigDecimal rsi = null;
        if (avgGain != null && avgLoss.compareTo(BigDecimal.ZERO) != 0){
            rs = avgGain.divide(avgLoss.abs(), 6, BigDecimal.ROUND_HALF_UP);
            if (avgLoss.compareTo(BigDecimal.ZERO) == 0){
                rsi = BigDecimal.valueOf(100);
            } else {
                rsi = BigDecimal.valueOf(100).
                        subtract(BigDecimal.valueOf(100).divide(rs.add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP));
            }
            rsText.setText(String.valueOf(rs.setScale(2, BigDecimal.ROUND_HALF_UP)));
            rsiText.setText(String.valueOf(rsi.setScale(2, BigDecimal.ROUND_HALF_UP)));

            if (rsi.compareTo(BigDecimal.valueOf(70)) < 0 && rsi.compareTo(BigDecimal.valueOf(30)) > 0){
                rsiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                rsiSignalText.setText("hold");
            } else if (rsi.compareTo(BigDecimal.valueOf(80)) < 0 && rsi.compareTo(BigDecimal.valueOf(70)) >= 0){
                rsiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                rsiSignalText.setText("sell");
            } else if (rsi.compareTo(BigDecimal.valueOf(80)) >= 0){
                rsiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                rsiSignalText.setText("strong sell");
            } else if (rsi.compareTo(BigDecimal.valueOf(30)) <= 0 && rsi.compareTo(BigDecimal.valueOf(20)) > 0){
                rsiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                rsiSignalText.setText("buy");
            } else if (rsi.compareTo(BigDecimal.valueOf(20)) <= 0){
                rsiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                rsiSignalText.setText("strong buy");
            }

            rsiIndicator.setLinePosition(80, 20, rsi.doubleValue());

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
                            6, BigDecimal.ROUND_HALF_UP);
            sokList.add(sok1);
        }
        if (currentList.size() >= 15){
            BigDecimal closeDay2 = currentList.get(1).getClose();
            BigDecimal sok2 = closeDay2.subtract(getLowestInPastFourteenDays(1)).
                    divide(getHighestInPastFourteenDays(1).subtract(getLowestInPastFourteenDays(1)),
                            6, BigDecimal.ROUND_HALF_UP);
            sokList.add(sok2);
        }
        if (currentList.size() >= 16){
            BigDecimal closeDay3 = currentList.get(2).getClose();
            BigDecimal sok3 = closeDay3.subtract(getLowestInPastFourteenDays(2)).
                    divide(getHighestInPastFourteenDays(2).subtract(getLowestInPastFourteenDays(2)),
                            6, BigDecimal.ROUND_HALF_UP);
            sokList.add(sok3);
        }

        // set sok text
        BigDecimal sokValue = null;
        if (sokList.size() > 0){
            sokValue = sokList.get(0);
            sokText.setText(String.valueOf(sokValue.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP)) + "%");

            if (sokValue.compareTo(BigDecimal.valueOf(0.7)) < 0 && sokValue.compareTo(BigDecimal.valueOf(0.3)) > 0){
                sokSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                sokSignalText.setText("hold");
            } else if (sokValue.compareTo(BigDecimal.valueOf(0.8)) < 0 && sokValue.compareTo(BigDecimal.valueOf(0.7)) >= 0){
                sokSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                sokSignalText.setText("sell");
            } else if (sokValue.compareTo(BigDecimal.valueOf(0.8)) >= 0){
                sokSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                sokSignalText.setText("strong sell");
            } else if (sokValue.compareTo(BigDecimal.valueOf(0.3)) <= 0 && sokValue.compareTo(BigDecimal.valueOf(0.2)) > 0){
                sokSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                sokSignalText.setText("buy");
            } else if (sokValue.compareTo(BigDecimal.valueOf(0.2)) <= 0){
                sokSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                sokSignalText.setText("strong buy");
            }
            
            sokIndicator.setLinePosition(0.8, 0.2, sokValue.doubleValue());

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
            sodValue = totalSok.divide(BigDecimal.valueOf(3), 6, BigDecimal.ROUND_HALF_UP);
            sodText.setText(String.valueOf(sodValue.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP)) + "%");

            if (sodValue.compareTo(BigDecimal.valueOf(0.7)) < 0 && sodValue.compareTo(BigDecimal.valueOf(0.3)) > 0){
                sodSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                sodSignalText.setText("hold");
            } else if (sodValue.compareTo(BigDecimal.valueOf(0.8)) < 0 && sodValue.compareTo(BigDecimal.valueOf(0.7)) >= 0){
                sodSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                sodSignalText.setText("sell");
            } else if (sodValue.compareTo(BigDecimal.valueOf(0.8)) >= 0){
                sodSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                sodSignalText.setText("strong sell");
            } else if (sodValue.compareTo(BigDecimal.valueOf(0.3)) <= 0 && sodValue.compareTo(BigDecimal.valueOf(0.2)) > 0){
                sodSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                sodSignalText.setText("buy");
            } else if (sodValue.compareTo(BigDecimal.valueOf(0.2)) <= 0){
                sodSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                sodSignalText.setText("strong buy");
            }

            sodIndicator.setLinePosition(0.8, 0.2, sodValue.doubleValue());
       
        }

        // williams
        BigDecimal williams = null;
        if (currentList.size() >= 14){
            williams = currentList.get(0).getClose().subtract(getHighestInPastFourteenDays(0)).
                    divide(getHighestInPastFourteenDays(0).subtract(getLowestInPastFourteenDays(0)), 6, BigDecimal.ROUND_HALF_UP);
            williamsRText.setText(String.valueOf(williams.multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP)) + "%");
            
            if (williams.abs().compareTo(BigDecimal.valueOf(0.7)) < 0 && williams.abs().compareTo(BigDecimal.valueOf(0.3)) > 0){
                williamsRSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                williamsRSignalText.setText("hold");
            } else if (williams.abs().compareTo(BigDecimal.valueOf(0.3)) < 0 && williams.abs().compareTo(BigDecimal.valueOf(0.2)) >= 0){
                williamsRSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                williamsRSignalText.setText("sell");
            } else if (williams.abs().compareTo(BigDecimal.valueOf(0.2)) <= 0){
                williamsRSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                williamsRSignalText.setText("strong sell");
            } else if (williams.abs().compareTo(BigDecimal.valueOf(0.8)) <= 0 && williams.abs().compareTo(BigDecimal.valueOf(0.7)) > 0){
                williamsRSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                williamsRSignalText.setText("buy");
            } else if (williams.abs().compareTo(BigDecimal.valueOf(0.8)) >= 0){
                williamsRSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                williamsRSignalText.setText("strong buy");
            }

            williamIndicator.setLinePosition(0.2, 0.8, williams.abs().doubleValue());
        
        } else {
            williamsRText.setText("N/A");
        }

        // calculate all mfi values
        ArrayList<BigDecimal> mfiList = new ArrayList<>();
        for (int i=0; i<currentList.size()-1; i++){
            BigDecimal mfi;
            // high + low + close / 3
            BigDecimal hlcThis = currentList.get(i).getHigh().add(currentList.get(i).getLow()).add(currentList.get(i).getClose()).divide(BigDecimal.valueOf(3), 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal hlcPrevious = currentList.get(i+1).getHigh().add(currentList.get(i+1).getLow()).add(currentList.get(i+1).getClose()).divide(BigDecimal.valueOf(3), 6, BigDecimal.ROUND_HALF_UP);
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

        // 14 days mfi
        BigDecimal posTotal = BigDecimal.ZERO;
        BigDecimal negTotal = BigDecimal.ZERO;
        BigDecimal fourteenDaysMfi = null;
        if (mfiList.size() >= 14){
            for (int i=0; i<14; i++){
                if (mfiList.get(i).compareTo(BigDecimal.ZERO) > 0){
                    posTotal = posTotal.add(mfiList.get(i));
                } else {
                    negTotal = negTotal.add(mfiList.get(i).abs());
                }
            }
            fourteenDaysMfi = BigDecimal.valueOf(100).subtract(
                    BigDecimal.valueOf(100).divide( BigDecimal.ONE.add(posTotal.divide(
                            negTotal, 6, BigDecimal.ROUND_HALF_UP)), 6, BigDecimal.ROUND_HALF_UP ) );
            mfiText.setText(String.valueOf(fourteenDaysMfi.setScale(2, BigDecimal.ROUND_HALF_UP)));

            if (fourteenDaysMfi.compareTo(BigDecimal.valueOf(80)) < 0 && fourteenDaysMfi.compareTo(BigDecimal.valueOf(20)) > 0){
                mfiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_blue_bright));
                mfiSignalText.setText("hold");
            } else if (fourteenDaysMfi.compareTo(BigDecimal.valueOf(90)) < 0 && fourteenDaysMfi.compareTo(BigDecimal.valueOf(80)) >= 0){
                mfiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                mfiSignalText.setText("sell");
            } else if (fourteenDaysMfi.compareTo(BigDecimal.valueOf(90)) >= 0){
                mfiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                mfiSignalText.setText("strong sell");
            } else if (fourteenDaysMfi.compareTo(BigDecimal.valueOf(20)) <= 0 && fourteenDaysMfi.compareTo(BigDecimal.valueOf(10)) > 0){
                mfiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                mfiSignalText.setText("buy");
            } else if (fourteenDaysMfi.compareTo(BigDecimal.valueOf(10)) <= 0){
                mfiSignalText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                mfiSignalText.setText("strong buy");
            }

            mfiIndicator.setLinePosition(90, 10, fourteenDaysMfi.doubleValue());
        }
        else {
            mfiText.setText("N/A");
        }

        // buy/sell signal
        if (rsi != null && sokValue!= null && sodValue != null && williams != null && fourteenDaysMfi != null){
            if (rsi.compareTo(BigDecimal.valueOf(80)) > 0 &&
                    sokValue.compareTo(BigDecimal.valueOf(0.8)) > 0 &&
                    sodValue.compareTo(BigDecimal.valueOf(0.8)) > 0 &&
                    williams.abs().compareTo(BigDecimal.valueOf(0.2)) < 0 &&
                    fourteenDaysMfi.compareTo(BigDecimal.valueOf(90)) > 0) {
                signalText.setText("strong sell");
            }
            else if (rsi.compareTo(BigDecimal.valueOf(20)) < 0 &&
                    sokValue.compareTo(BigDecimal.valueOf(0.2)) < 0 &&
                    sodValue.compareTo(BigDecimal.valueOf(0.2)) < 0 &&
                    williams.abs().compareTo(BigDecimal.valueOf(0.2)) > 0 &&
                    fourteenDaysMfi.compareTo(BigDecimal.valueOf(10)) < 0){
                signalText.setText("strong buy");
            }
            else if (rsi.compareTo(BigDecimal.valueOf(70)) > 0 &&
                    sokValue.compareTo(BigDecimal.valueOf(0.7)) > 0 &&
                    sodValue.compareTo(BigDecimal.valueOf(0.7)) > 0 &&
                    williams.abs().compareTo(BigDecimal.valueOf(0.3)) < 0 &&
                    fourteenDaysMfi.compareTo(BigDecimal.valueOf(80)) > 0) {
                signalText.setText("sell");
            }
            else if (rsi.compareTo(BigDecimal.valueOf(30)) < 0 &&
                    sokValue.compareTo(BigDecimal.valueOf(0.3)) < 0 &&
                    sodValue.compareTo(BigDecimal.valueOf(0.3)) < 0 &&
                    williams.abs().compareTo(BigDecimal.valueOf(0.7)) > 0 &&
                    fourteenDaysMfi.compareTo(BigDecimal.valueOf(20)) < 0){
                signalText.setText("buy");
            }
            else {
                signalText.setText("hold");
            }
        }
        else {
            signalText.setText("N/A");
        }

        setAdvertisement();
    }

    private BigDecimal getLowestInPastFourteenDays(int from){
        BigDecimal lowest = currentList.get(from).getLow();
        for (int i=from; i<from+14; i++){
            BigDecimal value = currentList.get(i).getLow();
            if (lowest.compareTo(value) > 0){
                lowest = value;
            }
        }
        return lowest;
    }

    private BigDecimal getHighestInPastFourteenDays(int from){
        BigDecimal highest = currentList.get(from).getHigh();
        for (int i=from; i<from+14; i++){
            BigDecimal value = currentList.get(i).getHigh();
            if (highest.compareTo(value) < 0){
                highest = value;
            }
        }
        return highest;
    }

    public void setSymbol(String symbol){
        this.symbol = symbol;
    }
    public void setName(String name){
        this.name = name;
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
                                String newItem = symbol + ", " + name;
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

    private void setAdvertisement(){
        AdView mAdView = (AdView) fragmentView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
