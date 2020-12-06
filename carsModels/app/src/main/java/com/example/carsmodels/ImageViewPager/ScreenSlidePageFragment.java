package com.example.carsmodels.ImageViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.CarImage;

public class ScreenSlidePageFragment extends Fragment {
    private int position;

    public ScreenSlidePageFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.image_page, container, false);
        ImageView imgView =rootView.findViewById(R.id.imageView);
        final CarImage carImage = ScreenSlidePagerAdapter.images.get(position);
        imgView.setImageBitmap(BitmapFactory.decodeByteArray(carImage.getImg(),0, carImage.getImg().length));
        rootView.findViewById(R.id.deletImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Image?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
//                                long result = temp.remove();
//                                if (result == 1) {
//                                    Toast.makeText(getApplicationContext(), "Brand Deleted Successfully", Toast.LENGTH_SHORT).show();
//                                    loadModels();
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
//                                }

//                                ScreenSlidePagerAdapter.images.remove(carImage);
                                System.out.println("aaaaaa " +position);

                                ((ScreenSlidePagerAdapter)(((FullView)getActivity()).getPagerAdapter())).removeView();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


        return rootView;
    }
}
