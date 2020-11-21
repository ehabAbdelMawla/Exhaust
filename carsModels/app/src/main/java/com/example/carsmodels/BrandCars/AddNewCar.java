package com.example.carsmodels.BrandCars;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddNewCar extends AppCompatActivity {


    final int GET_FROM_GALLERY = 1;
    byte[] imageBytes;

    //   Load Image when retreve result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                Compress Image
                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                imageBytes = util.getInstance().getBitmapAsByteArray(bitmap);

                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_car);
//      Map Controllers

        Button addButton = findViewById(R.id.addButton);
        FloatingActionButton uploadImage=findViewById(R.id.addImageButton);
        final EditText carName = findViewById(R.id.carName);
        final EditText carCountryOrigin = findViewById(R.id.carCountryOrigin);
        final EditText hoursePower = findViewById(R.id.hoursePower);
        final EditText motorCapacity = findViewById(R.id.motorCapacity);
        final EditText bagSpace = findViewById(R.id.bagSpace);
        final util utilMethods = util.getInstance();

        final AddNewCar superThis = this;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilMethods.getVal(carName).equals("")) {
                    Toast.makeText(getApplicationContext(), "Car Name is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(carCountryOrigin).equals("")) {
                    Toast.makeText(getApplicationContext(), "Country of Origin is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(hoursePower).equals("")) {
                    Toast.makeText(getApplicationContext(), "hourse Power is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(motorCapacity).equals("")) {
                    Toast.makeText(getApplicationContext(), "Motor Capacity is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(bagSpace).equals("")) {
                    Toast.makeText(getApplicationContext(), "Bag Space  is Required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double hp, mc, bs;

                try {
                    hp = Double.parseDouble(utilMethods.getVal(hoursePower));
                    mc = Double.parseDouble(utilMethods.getVal(motorCapacity));
                    bs = Double.parseDouble(utilMethods.getVal(bagSpace));
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "hourse Power,Motor Capacity and Bag Space allowed Numbers Only!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Car newCar = new Car(utilMethods.getVal(carName), utilMethods.getVal(carCountryOrigin), mc, hp, bs, imageBytes, BrandCars.getBrand().getId());
                long operationResult = newCar.insert();
                if (operationResult > 0) {
                    Toast.makeText(getApplicationContext(), "Car Added Successfully", Toast.LENGTH_SHORT).show();
                    BrandCars.updateData=true;
                    superThis.finish();
                } else if (operationResult == -1) {
                    Toast.makeText(getApplicationContext(), "Car Already Exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }

}
