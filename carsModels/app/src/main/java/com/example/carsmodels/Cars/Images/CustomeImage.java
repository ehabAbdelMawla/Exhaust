package com.example.carsmodels.Cars.Images;


import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.carsmodels.DataModel.CarImage;
import com.example.carsmodels.R;


public class CustomeImage extends ConstraintLayout  {
    /**
     * Variables Declarations
     */
    private int index;
    private CarImage carImageObj;
    private RadioButton radioButton;
    private ImageView image;



    /**
     * @param context     : context that hold Image
     * @param index       : refer to index of image in slider
     * @param carImageObj : hold Obj of All Data
     */
    public CustomeImage(Context context, int index, CarImage carImageObj) {
        super(context);
        inflate(context, R.layout.car_image_item, this);
        this.setId(R.id.parentId);
        this.radioButton = this.findViewById(R.id.selectedRadio);
        this.image = this.findViewById(R.id.ImageView);
        this.index = index;
        this.carImageObj = carImageObj;


    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public CarImage getCarImageObj() {
        return carImageObj;
    }

    public void setCarImageObj(CarImage carImageObj) {
        this.carImageObj = carImageObj;
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }



}



