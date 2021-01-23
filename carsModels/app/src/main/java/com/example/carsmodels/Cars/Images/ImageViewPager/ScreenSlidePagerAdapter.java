package com.example.carsmodels.Cars.Images.ImageViewPager;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.carsmodels.Cars.Images.CustomeImage;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    /**
     * Declare Instance Variables
     */
    private ViewPager mPager;
    private Activity parentActivit;
    /**
     * Declare Class Variables
     */
    public static ArrayList<CustomeImage> images = new ArrayList<>();

    public ScreenSlidePagerAdapter(FragmentManager fm, ViewPager mPager, FullView parent) {
        super(fm);
        this.mPager = mPager;
        this.parentActivit = parent;
    }

    /**
     * Remove Image From Delete icon in PageViewer
     */
    public void removeView() {
        int delIdxVar = mPager.getCurrentItem();
        //   Remove From images Container
        ((FlexboxLayout) images.get(delIdxVar).getParent()).removeView(images.get(delIdxVar));
        //   Remove From Slider List
        images.remove(delIdxVar);
        updateIndicesOfImageViewList();
        if (images.size() >= 1) {
            mPager.setAdapter(this);
            if (delIdxVar == 0) {
                mPager.setCurrentItem(0, true);
            } else {
                mPager.setCurrentItem(delIdxVar - 1, true);
            }
            notifyDataSetChanged();
        } else {
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

    /**
     * To Recorrect Indecies of images View in list after deleting some inner indecies
     */
    public static void updateIndicesOfImageViewList() {
            int index = 0;
            for (CustomeImage imageView : images) {
                imageView.setIndex(index++);
            }
    }
}
