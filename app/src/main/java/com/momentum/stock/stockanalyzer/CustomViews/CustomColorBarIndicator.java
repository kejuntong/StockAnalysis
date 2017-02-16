package com.momentum.stock.stockanalyzer.CustomViews;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.math.BigDecimal;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.momentum.stock.stockanalyzer.R;
import com.momentum.stock.stockanalyzer.UtilClasses.Global;

/**
 * Created by kevintong on 2017-02-14.
 */

public class CustomColorBarIndicator extends View{

    private Paint paint;
    ArgbEvaluator argbEvaluator;

    double startPoint; // correspond to strong sell point
    double endPoint; // correspond to strong buy point
    double subsection;

    double strongSellValue;
    double strongBuyValue;
    double setValue;
    float linePosition = 0;

    public CustomColorBarIndicator(Context context) {
        super(context);
        init();
    }

    public CustomColorBarIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomColorBarIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        argbEvaluator = new ArgbEvaluator();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        startPoint = getWidth()/10.0*2;
        endPoint = getWidth()/10.0*8;
        subsection = (endPoint-startPoint)/100.0;
        paint.setColor(Color.YELLOW);

        float barTop = (float)(getHeight()/6.0*3);
        float barBottom = (float)(getHeight()/6.0*4);

        // paint the start and end sections
        paint.setColor(ContextCompat.getColor(getContext(), R.color.red));
        canvas.drawRect(0, barTop, (float)startPoint, barBottom, paint);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
        canvas.drawRect((float)endPoint, barTop, getWidth(), barBottom, paint);

        for (int i=0; i<100; i++){
            int color = (Integer) argbEvaluator.evaluate((float)(i/100.0),
                    ContextCompat.getColor(getContext(), R.color.red),
                    ContextCompat.getColor(getContext(), R.color.green));
            paint.setColor(color);
            canvas.drawRect((float)(startPoint+subsection*i), barTop, (float)(startPoint+subsection*(i+1)), barBottom, paint);
        }

        paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        paint.setStrokeWidth(Global.dpToPx(3));
        calculateLinePosition();
        canvas.drawLine(linePosition, 0, linePosition, getHeight(), paint);

    }

    public void setLinePosition(double strongSellValue, double strongBuyValue, double setValue){

        this.strongSellValue = strongSellValue;
        this.strongBuyValue = strongBuyValue;
        this.setValue = setValue;

        invalidate();
    }

    private void calculateLinePosition(){

        // if setValue is between the start and end point
        if ((setValue >= strongSellValue && setValue <= strongBuyValue) ||
                (setValue <= strongSellValue && setValue >= strongBuyValue)){
            double percentage = (setValue - strongSellValue) / (strongBuyValue - strongSellValue);
            linePosition = (float) (startPoint + percentage * (getWidth() * 6.0 / 10));
        }

        // if setValue is more than strong sell
        else if ((strongBuyValue > strongSellValue && setValue < strongSellValue) ||
                (strongBuyValue < strongSellValue && setValue > strongSellValue)){
            double percentage = (setValue - strongSellValue) / ((strongSellValue - strongBuyValue) * 10 / 6);
            linePosition  = (float) (startPoint - percentage * getWidth());
        }

        // if setValue is more than strong buy
        else if ((strongBuyValue < strongSellValue && setValue < strongBuyValue) ||
                (strongBuyValue > strongSellValue && setValue > strongBuyValue)){
            double percentage = (setValue - strongBuyValue) / ((strongBuyValue - strongSellValue) * 10 / 6);
            linePosition  = (float) (endPoint + percentage * getWidth());
        }
    }

}
