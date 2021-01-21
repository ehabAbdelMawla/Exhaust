package com.example.carsmodels.util;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.R;
import com.example.carsmodels.util.Loader.Loader;
import com.google.android.flexbox.FlexboxLayout;

/**
 * All Activity Extends This Class To Apply on all Activities
 */
public class AnimatedActivity extends AppCompatActivity {

    private int animationStyle = R.style.animation_fade;
    protected Loader loaderDialog;
    protected TextView emptyTextView;

    public void setAnimationStyle(int newStyle) {
        animationStyle = newStyle;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderDialog = new Loader(this);
    }

    protected void checkIfEmpty(boolean condition, FlexboxLayout container, int stringId) {
        if (condition) {
            emptyTextView = new TextView(this);
            emptyTextView.setTextSize(20);
            emptyTextView.setText(stringId);
            container.addView(emptyTextView);
            YoYo.with(Techniques.SlideInUp).duration(350).playOn(emptyTextView);
        } else if (emptyTextView != null) {
            container.removeView(emptyTextView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setWindowAnimations(animationStyle);
    }


    protected void addViewWithAnimate(FlexboxLayout parent, View child, Techniques animateType, int duration) {
        parent.addView(child);
        YoYo.with(animateType).duration(duration).playOn(child);
    }

    protected void removeViewWithAnimate(final FlexboxLayout parent, final View child, Techniques animateType, int duration) {
        YoYo.with(animateType).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                parent.removeView(child);
            }
        }).duration(duration).playOn(child);
    }
    protected void removeViewWithAnimate(final FlexboxLayout parent, final View child, Techniques animateType, int duration,  final int stringId) {
        YoYo.with(animateType).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                parent.removeView(child);
                checkIfEmpty(parent.getChildCount()==0,parent,stringId);
            }
        }).duration(duration).playOn(child);
    }
}
