package com.example.dounn.menutendina.OtherView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.example.dounn.menutendina.Animations.ImageProfileAnimation;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lucadiliello on 26/06/2017.
 */

public class CircleImageViewSpecial extends CircleImageView {

    int offset = 0;
    Shader gradient;
    RectF rect;
    Paint p;
    ImageProfileAnimation animation;
    int lastCenterW, lastCenterH;

    public CircleImageViewSpecial(Context context, AttributeSet a) {
        super(context, a);
        init();
    }

    public CircleImageViewSpecial(Context context) {
        super(context);
        init();
    }

    public CircleImageViewSpecial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public void init() {
        animation = new ImageProfileAnimation(this);
        animation.setDuration(8000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());

        this.setBorderColor(Color.TRANSPARENT);
        this.setBorderWidth(0);
        p = new Paint();
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // only want to do this once
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        startAnimation(animation);
                    }
                });
        rect = new RectF(0, 0, 0, 0);
        gradient = new SweepGradient(0, 0, new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED}, null);

    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // calcolo il centro della view
        int centerW = getWidth() / 2, centerH = getHeight() / 2;
        int radius = Math.min(centerW, centerH) - 10;

        // se le dimensioni sono cambiate dall'ultimo rendering,
        // ricalcolo rect e gradiend con le nuove dimensioni
        if(lastCenterW != centerW || lastCenterH != centerH) {
            lastCenterH = centerH;
            lastCenterW = centerW;
            rect = new RectF(centerW - radius, centerH - radius, centerW + radius, centerH + radius);
            gradient = new SweepGradient(centerW, centerH, new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED}, null);
        }

        // setto il pennello con linea grossa e anti-aliasing
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(20);
        p.setShader(gradient);

        // per simulare una rotazione del cerchio intorno all'immagine,
        // lo disegno sulla canvas ruotata di un offset che sarà modificato a sua volta da un'animazione
        canvas.rotate(offset, centerW, centerH);
        canvas.drawArc(rect, 0, 360, false, p);
    }
}
