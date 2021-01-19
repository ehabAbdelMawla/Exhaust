package com.example.carsmodels.util;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.R;

public class AnimatedActivity extends AppCompatActivity {

    private int animationStyle = R.style.animation_fade;

    public void setAnimationStyle(int newStyle) {
        animationStyle = newStyle;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setWindowAnimations(animationStyle);
    }
}
