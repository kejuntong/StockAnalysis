package com.momentum.stock.stockanalyzer.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.ads.MobileAds;
import com.momentum.stock.stockanalyzer.R;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

/**
 * Created by kevintong on 16-12-01.
 */
public class SplashScreenActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3809096372269559~8812375822");

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.black); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.app_icon_5); //or any other drawable
        configSplash.setAnimLogoSplashDuration(2000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.BounceIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Title
        configSplash.setTitleSplash("StockMomentum");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(40f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
        configSplash.setTitleFont("fonts/diti_sweet.ttf"); //provide string to your font located in assets/fonts/
    }

    @Override
    public void animationsFinished() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(SplashScreenActivity.this)
                .content(getString(R.string.disclaimer))
                .contentColorRes(R.color.black)
                .backgroundColorRes(R.color.white)
                .positiveText("AGREE")
                .positiveColorRes(R.color.blue)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(SplashScreenActivity.this, MyPortfolioActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).negativeText("DISAGREE")
                .negativeColorRes(R.color.red)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialog.show();

    }
}
