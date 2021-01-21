package com.example.carsmodels.util;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.R;
import com.example.carsmodels.util.Loader.Loader;

/**
 * All Fragments Extends This  Class To Apply on all childs
 */
public class AnimatedFragment extends DialogFragment {
    private int animationStyle = R.style.animation_fade;
    protected Loader loaderDialog;

    public void setAnimationStyle(int newStyle) {
        animationStyle = newStyle;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loaderDialog = new Loader(context);
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
