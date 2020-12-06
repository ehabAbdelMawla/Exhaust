package com.example.carsmodels.ImageViewPager;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.carsmodels.dataModel.CarImage;

import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private ViewPager mPager;
    private Activity parentActivit;
    public static ArrayList<CarImage> images;

    public ScreenSlidePagerAdapter(FragmentManager fm,ViewPager mPager,FullView parent) {
        super(fm);
        this.mPager=mPager;
        this.parentActivit=parent;
    }

    public void removeView() {
        int delIdxVar = mPager.getCurrentItem();
        images.remove(delIdxVar);
        if(images.size() >= 1)
        {
            mPager.setAdapter(this);
            if(delIdxVar==0){
                mPager.setCurrentItem(1,true);}
            else{
            mPager.setCurrentItem(delIdxVar-1,true);}
            notifyDataSetChanged();
        }else{
            parentActivit.finish();
        }

    }



    @Override
    public Fragment getItem(int position) {
        return new ScreenSlidePageFragment(position);
    }

    @Override
    public int getCount() {
        return images.size();
    }
}
