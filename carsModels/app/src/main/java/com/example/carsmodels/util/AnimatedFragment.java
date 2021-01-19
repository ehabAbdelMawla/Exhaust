package com.example.carsmodels.util;

import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.R;

public class AnimatedFragment extends DialogFragment {
    private int animationStyle = R.style.animation_fade;

    public void setAnimationStyle(int newStyle) {
        animationStyle = newStyle;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && animationStyle != -500) {
            getDialog().getWindow().setWindowAnimations(
                    animationStyle);
        }

    }
}
