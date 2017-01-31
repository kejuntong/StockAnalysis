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

        buttonImageFirst = (ImageView) findViewById(R.id.first_image);
        buttonImageSecond = (ImageView) findViewById(R.id.second_image);
        buttonImageThird = (ImageView) findViewById(R.id.third_image);

        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonImageFirst.setAlpha(1.0f);
                if (dayDataFragment == null) {
                    dayDataFragment = new DayDataFragment();
                }
                switchFragment(dayDataFragment);

            }
        });

        buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonImageSecond.setAlpha(1.0f);
                if (historicalDataFragment == null) {
                    historicalDataFragment = new HistoricalDataFragment();
                }
                switchFragment(historicalDataFragment);
            }
        });

        buttonThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonImageThird.setAlpha(1.0f);
                if (indicatorsFragment == null){
                    indicatorsFragment = new IndicatorsFragment();
                }
                switchFragment(indicatorsFragment);
            }
        });

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

    public String getSelectedStockSymbol(){
        return this.selectedStockSymbol;
    }
    public String getSelectedStockName(){
        return this.selectedStockName;
    }
}
