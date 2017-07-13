package com.example.dounn.menutendina.OtherView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.dounn.menutendina.Utility.Constants;

import static android.R.attr.padding;

/**
 * Created by lucadiliello on 26/06/2017.
 */

public class CircleView extends View {

    RectF rect;
    private final int points = Constants.pointsNumberAnimationCircle;

    private int offset;
    private Paint circlePaint;
    private Paint textPaint;
    private Paint bgPaint;
    private int textSize = 60;
    RadialGradient rad;
    int oldCenterH, oldCenterW;
    int number = 0;

    public int getNumber() {
        return number;
    }

    public void setNumber(int setNumber) {
        this.number = setNumber;
    }

    public CircleView(Context context, AttributeSet a) {
        super(context, a);
        init();
    }

    public CircleView(Context context) {
        super(context);
        init();
    }

    void init() {
        offset = 0;
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(20);
        circlePaint.setStyle(Paint.Style.STROKE);
        bgPaint = new Paint();
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.SERIF);
        textPaint.setColor(Color.BLACK);
    }

    public void setColor(int c) {
        circlePaint.setColor(c);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        int h = getHeight(), w = getWidth();
        int centerW = w / 2;
        int centerH = h / 2;
        int dimension = Math.min(h, w);
        int padding = dimension/4;

        if(oldCenterH != centerH || oldCenterW != centerW || rect == null || rad == null) {
            oldCenterH = centerH;
            oldCenterW = centerW;
            rad = new RadialGradient(centerW, centerH, dimension / 2, Color.rgb(232, 234, 254), Color.rgb(250, 250, 250), Shader.TileMode.CLAMP);
            rect = new RectF(centerW - padding, centerH - padding, centerW + padding, centerH + padding);
        }
        bgPaint.setShader(rad);

        c.drawCircle(centerW, centerH, dimension, bgPaint);

        int lang = (int) ((360 / points) * 0.7);
        int angle = 360 / points;
        for(int i = 0; i < points; i++) {
            int startAngle = offset + angle * i;
            c.drawArc(rect, startAngle, lang, false, circlePaint);
        }
        drawTextCentred(c, textPaint, ""+number, centerW, centerH);
    }

    private final Rect textBounds = new Rect();

    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy) {
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }
}
