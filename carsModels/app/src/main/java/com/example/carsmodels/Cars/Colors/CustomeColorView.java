package com.example.carsmodels.Cars.Colors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.carsmodels.Cars.Images.CarColorImages;
import com.example.carsmodels.DataModel.CarColor;
import com.example.carsmodels.R;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;

public class CustomeColorView extends ConstraintLayout implements View.OnClickListener, View.OnLongClickListener {
    /**
     * Variables Declarations
     */
    private CarColor colorObj;
    private int carId;
    private String carName;
    private AppCompatActivity activity;


    public CustomeColorView(AppCompatActivity activity, CarColor colorObj, int carId, String carName) {
        super(activity);
        try {

            this.activity = activity;
            // .... inflate Layout in my Obj
            inflate(activity, R.layout.color_item, this);
            // .... Set Init Data ....
            this.colorObj = colorObj;
            this.carName = carName;
            this.carId = carId;
            // .... Set Ui Color ....

            setOnClickListener(this);
            setOnLongClickListener(this);
            this.findViewById(R.id.view).setBackgroundColor(android.graphics.Color.parseColor(colorObj.getColorHexCode()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CarColor getColorObj() {
        return colorObj;
    }

    public void setColorObj(CarColor colorObj) {
        this.colorObj = colorObj;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    @Override
    public void onClick(View v) {
        Intent colorImages = new Intent(this.getContext(), CarColorImages.class);
        Bundle bundle = new Bundle();
        bundle.putInt("relationId", colorObj.getRealtionId());
        bundle.putString("CarName", carName);
        colorImages.putExtras(bundle);
        this.getContext().startActivity(colorImages);
    }

    @Override
    public boolean onLongClick(View v) {
        final AlertDialog.Builder alertDialogBuilde = new AlertDialog.Builder(this.getContext());
        final View popUpView = inflate(this.getContext(), R.layout.edit_delete_popup, null);
        alertDialogBuilde.setView(popUpView);
        final AlertDialog alert = alertDialogBuilde.create();
        alert.show();
        popUpView.findViewById(R.id.editIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                new AddNewColorOrSelectPrevOneDialogFragment(carId, colorObj.getRealtionId(), colorObj.getColorId(), CustomeColorView.this).show(CustomeColorView.this.activity.getSupportFragmentManager(), "updateColor");
            }
        });
        popUpView.findViewById(R.id.deleteIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                new ConfirmDialog(CustomeColorView.this.getContext(), "Delete Color?", android.R.drawable.ic_delete) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CarColor.removeRelation(colorObj.getRealtionId());
                        Toast.makeText(CustomeColorView.this.getContext(), "Color Deleted Successfully", Toast.LENGTH_LONG).show();
                        ((FlexboxLayout) CustomeColorView.this.getParent()).removeView(CustomeColorView.this);
                    }
                }.show();
            }
        });
        return true;
    }
}
