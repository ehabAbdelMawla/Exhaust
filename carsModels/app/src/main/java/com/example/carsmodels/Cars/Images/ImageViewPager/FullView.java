package com.example.carsmodels.Cars.Images.ImageViewPager;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.carsmodels.R;
import com.example.carsmodels.util.AnimatedActivity;

public class FullView extends AnimatedActivity {
    /**
     * Declare Instance Variables
     */
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = View.inflate(this, R.layout.view_pager, null);
        setContentView(v);
        mPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mPager, this);
        mPager.setAdapter(pagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mPager.setCurrentItem(b.getInt("start"), true);
        }
    }



    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }
}


