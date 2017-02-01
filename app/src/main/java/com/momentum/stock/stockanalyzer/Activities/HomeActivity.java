package com.momentum.stock.stockanalyzer.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.momentum.stock.stockanalyzer.Fragments.BaseFragment;
import com.momentum.stock.stockanalyzer.Fragments.DayDataFragment;
import com.momentum.stock.stockanalyzer.Fragments.HistoricalDataFragment;
import com.momentum.stock.stockanalyzer.Fragments.IndicatorsFragment;
import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Constants;

/**
 * Created by kevintong on 2017-01-31.
 */

public class HomeActivity extends Activity {

    RelativeLayout buttonFirst;
    RelativeLayout buttonSecond;
    RelativeLayout buttonThird;
    RelativeLayout indicatorFirst;
    RelativeLayout indicatorSecond;
    RelativeLayout indicatorThird;

    ImageView buttonImageFirst;
    ImageView buttonImageSecond;
    ImageView buttonImageThird;

    DayDataFragment dayDataFragment;
    HistoricalDataFragment historicalDataFragment;
    IndicatorsFragment indicatorsFragment;

    String selectedStockSymbol;
    String selectedStockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        selectedStockSymbol = getIntent().getExtras().getString("symbol");
        selectedStockName = getIntent().getExtras().getString("name");

        buttonFirst = (RelativeLayout) findViewById(R.id.first_button);
        buttonSecond = (RelativeLayout) findViewById(R.id.second_button);
        buttonThird = (RelativeLayout) findViewById(R.id.third_button);
        indicatorFirst = (RelativeLayout) findViewById(R.id.indicator_first);
        indicatorSecond = (RelativeLayout) findViewById(R.id.indicator_second);
        indicatorThird = (RelativeLayout) findViewById(R.id.indicator_third);

        buttonImageFirst = (ImageView) findViewById(R.id.first_image);
        buttonImageSecond = (ImageView) findViewById(R.id.second_image);
        buttonImageThird = (ImageView) findViewById(R.id.third_image);

        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buttonImageFirst.setAlpha(1.0f);
                setIndicator(Constants.DAY_DATA_FRAGMENT);
                if (dayDataFragment == null) {
                    dayDataFragment = new DayDataFragment();
                }
                switchFragment(dayDataFragment);

            }
        });

        buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buttonImageSecond.setAlpha(1.0f);
                setIndicator(Constants.HISTORICAL_DATA_FRAGMENT);
                if (historicalDataFragment == null) {
                    historicalDataFragment = new HistoricalDataFragment();
                }
                switchFragment(historicalDataFragment);
            }
        });

        buttonThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buttonImageThird.setAlpha(1.0f);
                setIndicator(Constants.INDICATORS_FRAGMENT);
                if (indicatorsFragment == null){
                    indicatorsFragment = new IndicatorsFragment();
                }
                switchFragment(indicatorsFragment);
            }
        });


        // load the first fragment by default
        setIndicator(Constants.DAY_DATA_FRAGMENT);
        dayDataFragment = new DayDataFragment();
        switchFragment(dayDataFragment);

    }

    public void switchFragment(BaseFragment fragment) {
        hideAllFragments(fragment.getFragmentName());
        FragmentTransaction txn = getFragmentManager().beginTransaction();
        Fragment existingFragment = getFragmentManager().findFragmentByTag(fragment.getFragmentName());
        if (existingFragment != null) {
            txn.show(existingFragment).commit();
        } else {
            txn.add(R.id.fragment_container, fragment, fragment.getFragmentName()).commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    private void hideAllFragments(String excludeFragmentName) {
        Fragment dayDataFragment = getFragmentManager().findFragmentByTag(Constants.DAY_DATA_FRAGMENT);
        Fragment historicalDataFragment = getFragmentManager().findFragmentByTag(Constants.HISTORICAL_DATA_FRAGMENT);
        Fragment indicatorsFragment = getFragmentManager().findFragmentByTag(Constants.INDICATORS_FRAGMENT);

        FragmentTransaction txn = getFragmentManager().beginTransaction();

        if (dayDataFragment != null
                && excludeFragmentName != null && !excludeFragmentName.equals(Constants.DAY_DATA_FRAGMENT)) {
            txn.hide(dayDataFragment);
        }

        if (historicalDataFragment != null
                && excludeFragmentName != null && !excludeFragmentName.equals(Constants.HISTORICAL_DATA_FRAGMENT)){
            txn.hide(historicalDataFragment);
        }

        if (indicatorsFragment != null
                && excludeFragmentName != null && !excludeFragmentName.equals(Constants.INDICATORS_FRAGMENT)){
            txn.hide(indicatorsFragment);
        }

        txn.commit();
        getFragmentManager().executePendingTransactions();
    }

    private void setIndicator(String name){
        switch (name){
            case Constants.DAY_DATA_FRAGMENT:
                indicatorFirst.setVisibility(View.VISIBLE);
                indicatorSecond.setVisibility(View.INVISIBLE);
                indicatorThird.setVisibility(View.INVISIBLE);
                buttonImageFirst.setAlpha(1.0f);
                buttonImageSecond.setAlpha(0.5f);
                buttonImageThird.setAlpha(0.5f);
                break;
            case Constants.HISTORICAL_DATA_FRAGMENT:
                indicatorFirst.setVisibility(View.INVISIBLE);
                indicatorSecond.setVisibility(View.VISIBLE);
                indicatorThird.setVisibility(View.INVISIBLE);
                buttonImageFirst.setAlpha(0.5f);
                buttonImageSecond.setAlpha(1.0f);
                buttonImageThird.setAlpha(0.5f);
                break;
            case Constants.INDICATORS_FRAGMENT:
                indicatorFirst.setVisibility(View.INVISIBLE);
                indicatorSecond.setVisibility(View.INVISIBLE);
                indicatorThird.setVisibility(View.VISIBLE);
                buttonImageFirst.setAlpha(0.5f);
                buttonImageSecond.setAlpha(0.5f);
                buttonImageThird.setAlpha(1.0f);
                break;
        }
    }

    public String getSelectedStockSymbol(){
        return this.selectedStockSymbol;
    }
    public String getSelectedStockName(){
        return this.selectedStockName;
    }
}
