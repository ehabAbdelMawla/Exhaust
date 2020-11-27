package com.example.carsmodels.BrandCars;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CarsCuDialogFragment extends DialogFragment {


    private final int GET_FROM_GALLERY = 1;
    private byte[] imageBytes;
    private Button addButton;
    private ImageView imageView;
    private FloatingActionButton uploadImage;
    private EditText carName;
    private EditText carCountryOrigin;
    private EditText hoursePower;
    private EditText motorCapacity;
    private EditText bagSpace;
    private Car car;


    //    default Constructor
    public CarsCuDialogFragment() {
    }

    //    Edit  Constructor
    public CarsCuDialogFragment(Car car) {
        this.car = car;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_new_car, container, false);

//        Map Controllers
        addButton = rootView.findViewById(R.id.addButton);
        uploadImage = rootView.findViewById(R.id.addImageButton);
        carName = rootView.findViewById(R.id.carName);
        carCountryOrigin = rootView.findViewById(R.id.carCountryOrigin);
        hoursePower = rootView.findViewById(R.id.hoursePower);
        motorCapacity = rootView.findViewById(R.id.motorCapacity);
        bagSpace = rootView.findViewById(R.id.bagSpace);
        imageView = rootView.findViewById(R.id.imageView);

        if (car != null) {
            imageBytes = car.getImg();
            if (imageBytes != null) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
            carName.setText(car.getCarName());
            carCountryOrigin.setText(car.getCountry());
            motorCapacity.setText(String.valueOf(car.getMotorCapacity()));
            hoursePower.setText(String.valueOf(car.getHoursePower()));
            bagSpace.setText(String.valueOf(car.getBagSpace()));
            addButton.setText("Update");
        }

        final util utilMethods = util.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilMethods.getVal(carName).equals("")) {
                    Toast.makeText(getActivity(), "Car Name is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(carCountryOrigin).equals("")) {
                    Toast.makeText(getActivity(), "Country of Origin is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(hoursePower).equals("")) {
                    Toast.makeText(getActivity(), "hourse Power is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(motorCapacity).equals("")) {
                    Toast.makeText(getActivity(), "Motor Capacity is Required!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(bagSpace).equals("")) {
                    Toast.makeText(getActivity(), "Bag Space  is Required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                double hp, mc, bs;
                try {
                    hp = Double.parseDouble(utilMethods.getVal(hoursePower));
                    mc = Double.parseDouble(utilMethods.getVal(motorCapacity));
                    bs = Double.parseDouble(utilMethods.getVal(bagSpace));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "hourse Power,Motor Capacity and Bag Space allowed Numbers Only!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Car newCar = new Car(utilMethods.getVal(carName), utilMethods.getVal(carCountryOrigin), mc, hp, bs, imageBytes, BrandCars.getBrand().getId());
                if(car != null){
                    newCar.setId(car.getId());
                }
                long operationResult = car == null ? newCar.insert() : newCar.update();
                if (operationResult > 0) {
                    Toast.makeText(getActivity(), car == null?"Car Added Successfully":"Car Updated Successfully", Toast.LENGTH_SHORT).show();
//                    if fragment usage finish parent and set parent update Notation
                    if (getDialog() == null) {
                        getActivity().finish();
                        BrandCars.updateData = true;
                    } else {
//                        if dialog usage close dialog and update Data
                        getDialog().dismiss();
                        ((BrandCars) getActivity()).LoadCarsData();
                    }
                } else if ((car == null && operationResult == -1) || (car != null && operationResult == 0)) {
                    Toast.makeText(getActivity(), "Car Already Exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        return rootView;
    }

    //   Load Image when retreve result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            System.out.println(selectedImage);
            System.out.println(selectedImage);
            System.out.println(selectedImage);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//               === Compress Image ===
//                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                imageBytes = util.getInstance().getBitmapAsByteArray(bitmap);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
