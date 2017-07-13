package com.example.dounn.menutendina.Animations;


import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.dounn.menutendina.OtherView.CircleView;

public class CircleAnimation extends Animation {

    private boolean direction;
    private CircleView circle;

    public CircleAnimation(CircleView circle, boolean direction) {
        this.circle = circle;
        this.direction = direction;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float plus = (interpolatedTime);
        if(direction) {
            circle.setOffset((int) (360 * plus));
        } else {
            circle.setOffset((int) (360 * (1.0 - plus)));
        }

        circle.invalidate();
    }
}