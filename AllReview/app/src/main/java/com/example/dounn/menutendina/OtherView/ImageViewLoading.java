package com.example.dounn.menutendina.OtherView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.dounn.menutendina.R;
import com.example.dounn.menutendina.SuperActivity;
import com.example.dounn.menutendina.Utility.Constants;
import com.example.dounn.menutendina.Utility.Images;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lucadiliello on 28/06/2017.
 */

public class ImageViewLoading extends FrameLayout {

    String imageStyle;
    ProgressBar progressBar;
    ImageView imageView;
    String fotoPath;
    Context context;
    FrameLayout.LayoutParams params;

    public ImageViewLoading(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ImageViewLoading(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageViewLoading);
        try {
            imageStyle = ta.getString(R.styleable.ImageViewLoading_image_style);
        } finally {
            ta.recycle();
        }
        init(context);
    }

    public ImageViewLoading(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageViewLoading);
        try {
            imageStyle = ta.getString(R.styleable.ImageViewLoading_image_style);
        } finally {
            ta.recycle();
        }
        init(context);
    }

    public void init(Context context) {

        this.context = context;
        fotoPath = "";
        progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.barra),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        if(imageStyle == null) imageStyle = "normal";

        switch(imageStyle) {
            case "circular":
                imageView = new CircleImageView(context);
                break;
            case "circular_animated":
                imageView = new CircleImageViewSpecial(context);
                break;
            case "normal":
            default:
                imageView = new ImageView(context);
        }

        params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);

        addViewInLayout(imageView, 0, params);
        addViewInLayout(progressBar, 1, params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAlpha(0f);
        progressBar.setAlpha(1f);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }


    public void setFotoPath(final String f) {
        //if(true) return;
        if(f == null) {
            setImageResource(R.mipmap.ic_broken_image_black_24dp);

        } else if(this.fotoPath.equals(f)) {
            return;
        } else {
            this.fotoPath = f;
            ((SuperActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap b;
                    if(Images.containsImage(f)) {
                        b = Images.getImage(f);
                        setImageBitmap(b);
                    } else {

                        JSONObject req = new JSONObject();
                        try {
                            req.put("path", "foto");
                            req.put("fotoname", f);
                            new Request(new RequestCallback() {
                                @Override
                                public void inTheEnd(JSONObject a) {
                                    try {
                                        if(a.getString("status").equals("OK")) {
                                            Bitmap foto = Images.StringToBitMap(a.getJSONObject("result").getString("foto"));
                                            Images.addImage(f, foto);
                                            setImageBitmap(foto);
                                        } else throw new JSONException("");
                                    } catch(JSONException e) {
                                        e.printStackTrace();
                                        setImageResource(R.mipmap.ic_broken_image_black_24dp);
                                    }
                                }

                                @Override
                                public void noInternetConnection() {
                                    inTheEnd(new JSONObject());
                                }
                            }).execute(req);
                        } catch(JSONException e) {
                            e.printStackTrace();
                            setImageResource(R.mipmap.ic_profile);
                        }
                    }
                }
            });

        }

    }

    private void mostra() {
        imageView.animate().alpha(1f).setDuration(Constants.imageViewLoadDuration).start();
        progressBar.animate().alpha(0f).setDuration(Constants.imageViewLoadDuration).start();
    }

    public void setImageBitmap(Bitmap b) {
        if(b != null) {
            imageView.setImageBitmap(b);
            mostra();
        }
    }

    public void setImageResource(int i) {
        imageView.setImageResource(i);
        mostra();
    }

    public String getFotoPath() {
        return fotoPath;
    }
}
