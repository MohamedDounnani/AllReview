package com.example.dounn.menutendina.Animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.dounn.menutendina.OtherView.CircleImageViewSpecial;

/**
 * Created by lucadiliello on 26/06/2017.
 */

public class ImageProfileAnimation extends Animation{

    CircleImageViewSpecial image;

    public ImageProfileAnimation(CircleImageViewSpecial image) {
        this.image = image;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float plus = (interpolatedTime);
        image.setOffset((int) (360 * plus));
        image.invalidate();
    }
}
