package com.example.carsmodels.util;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.R;
import com.example.carsmodels.util.Loader.Loader;
/**
 * All Activity Extends This Class To Apply on all Activities
 */
public class AnimatedActivity extends AppCompatActivity {

    private int animationStyle = R.style.animation_fade;
    protected Loader loaderDialog;

    public void setAnimationStyle(int newStyle) {
        animationStyle = newStyle;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderDialog = new Loader(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setWindowAnimations(animationStyle);
    }
}
