package com.example.carsmodels.BrandCars;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.speceficeations.SpecificationCuDialogFragment;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddNewCar extends AppCompatActivity {

    private final int mainLayoutId=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainLayOut=new LinearLayout(this);
        mainLayOut.setId(mainLayoutId);
        mainLayOut.setGravity(Gravity.CENTER);
        mainLayOut.setOrientation(LinearLayout.VERTICAL);

        getSupportFragmentManager().beginTransaction()
                .add(mainLayOut.getId(), new CarsCuDialogFragment()).commit();

        setContentView(mainLayOut);

    }

}
