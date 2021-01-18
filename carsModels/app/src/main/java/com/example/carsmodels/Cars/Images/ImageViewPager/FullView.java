package com.example.carsmodels.Cars.Images.ImageViewPager;

import android.os.Bundle;
import android.transition.Fade;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.carsmodels.R;

public class FullView extends AppCompatActivity {
    /**
     * Declare Instance Variables
     */
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setExitTransition(new Fade());
        setContentView(R.layout.view_pager);
        mPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mPager, this);
        mPager.setAdapter(pagerAdapter);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mPager.setCurrentItem(b.getInt("start"), true);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }
}


