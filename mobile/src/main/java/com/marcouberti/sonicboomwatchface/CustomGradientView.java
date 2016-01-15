package com.marcouberti.sonicboomwatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.wearable.view.CircledImageView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Marco on 05/09/15.
 */
public class CustomGradientView extends CircledImageView {

    Paint paint = new Paint();
    int color;

    public CustomGradientView(Context context) {
        super(context);
        initPaint(context);
    }

    public CustomGradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(context);
    }

    public CustomGradientView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint(context);
    }

    private void initPaint(Context ctx){
        paint.setAntiAlias(true);
        color = ctx.getResources().getColor(R.color.col_4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(color);

        float R = this.getCircleRadius();
        if(R > this.getMeasuredWidth()/2) R = this.getMeasuredWidth()/2;
        canvas.drawCircle(this.getMeasuredWidth()/2, this.getMeasuredHeight()/2, R, paint);
    }
}
