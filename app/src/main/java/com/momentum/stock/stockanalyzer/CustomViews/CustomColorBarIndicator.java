package com.momentum.stock.stockanalyzer.CustomViews;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.momentum.stock.stockanalyzer.R;

/**
 * Created by kevintong on 2017-02-14.
 */

public class CustomColorBarIndicator extends View{

    private Paint paint;
    ArgbEvaluator argbEvaluator;

    public CustomColorBarIndicator(Context context) {
        super(context);
        paint = new Paint();
        argbEvaluator = new ArgbEvaluator();
    }

    public CustomColorBarIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        argbEvaluator = new ArgbEvaluator();
    }

    public CustomColorBarIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        argbEvaluator = new ArgbEvaluator();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setColor(Color.YELLOW);

        for (int i=0; i<100; i++){
            int color = (Integer) argbEvaluator.evaluate((float)(i/100.0),
                    ContextCompat.getColor(getContext(), R.color.red),
                    ContextCompat.getColor(getContext(), R.color.green));
            paint.setColor(color);
            canvas.drawRect((float)(i*getWidth()/100.0), 0, (float)((i+1)*getWidth()/100.0), getHeight(), paint);
        }

//        canvas.drawColor(Color.BLUE);

    }
}
