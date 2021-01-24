package com.example.carsmodels.util;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.R;
import com.example.carsmodels.util.Loader.Loader;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * All Activity Extends This Class To Apply on all Activities
 */
public class AnimatedActivity extends AppCompatActivity {

    private int animationStyle = R.style.animation_fade;
    protected Loader loaderDialog;
    protected Map<Integer, TextView> emptyTextViews;

    public void setAnimationStyle(int newStyle) {
        animationStyle = newStyle;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderDialog = new Loader(this);
        emptyTextViews = new HashMap();
    }

    public void checkIfEmpty(boolean condition, FlexboxLayout container, int stringId) {
        if (condition) {
            TextView targetView = emptyTextViews.get(stringId);
            if (targetView == null) {
                emptyTextViews.put(stringId, getEmptyTextViewForm(stringId));
                targetView = emptyTextViews.get(stringId);
            }
            container.addView(targetView);
            YoYo.with(Techniques.SlideInUp).duration(350).playOn(targetView);
        } else if (emptyTextViews.get(stringId) != null) {
            container.removeView(emptyTextViews.get(stringId));
            emptyTextViews.remove(stringId);
        }
    }

    private TextView getEmptyTextViewForm(int stringId) {
        TextView temp = new TextView(this);
        temp.setTextSize(20);
        temp.setText(stringId);
        return temp;
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
    protected void addViewWithAnimate(FlexboxLayout parent, View child,int index, Techniques animateType, int duration) {
        parent.addView(child,index);
        YoYo.with(animateType).duration(duration).playOn(child);
    }


    public void removeViewWithAnimate(final FlexboxLayout parent, final View child, Techniques animateType, int duration) {
        YoYo.with(animateType).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                parent.removeView(child);
            }
        }).duration(duration).playOn(child);
    }

    public void removeViewWithAnimate(final FlexboxLayout parent, final View child, Techniques animateType, int duration, final int stringId) {
        YoYo.with(animateType).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                parent.removeView(child);
                checkIfEmpty(parent.getChildCount() == 0, parent, stringId);
            }
        }).duration(duration).playOn(child);
    }
}
