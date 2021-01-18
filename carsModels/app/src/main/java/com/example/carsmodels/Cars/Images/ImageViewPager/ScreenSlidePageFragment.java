package com.example.carsmodels.Cars.Images.ImageViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.CarImage;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.util;

public class ScreenSlidePageFragment extends Fragment {
    /**
     * Declare Instance Variables
     */
    private int position;


    public ScreenSlidePageFragment(int position) {
        System.out.println("position: " + position);
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // .... inflate layout ....
        final View rootView = inflater.inflate(R.layout.image_page, container, false);
        // .... Map Components ....
        final ImageView imgView = rootView.findViewById(R.id.imageView);
        // .... get Object  ....
        final CarImage carImage = ScreenSlidePagerAdapter.images.get(position).getCarImageObj();
        // .... Load Image  ....
        util.getInstance().setGlideImage(container, carImage.getImgPath(), imgView);
        // .... Set Delete Icon Action Image  ....
        rootView.findViewById(R.id.deletImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmDialog(rootView.getContext(), "Delete Image?", android.R.drawable.ic_delete) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long result = carImage.remove();
                        if (result == 1) {
                            Toast.makeText(getContext(), "Image Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                        }
                        ((ScreenSlidePagerAdapter) (((FullView) getActivity()).getPagerAdapter())).removeView();
                    }
                }.show();

            }
        });

        return rootView;
    }


}


