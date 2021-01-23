package com.example.carsmodels.Cars;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.carsmodels.Brands.BrandDetails;
import com.example.carsmodels.DataModel.Car;
import com.example.carsmodels.R;
import com.example.carsmodels.util.AnimatedFragment;
import com.example.carsmodels.util.CloseLoaderThread;
import com.example.carsmodels.util.Loader.Loader;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CarsAddAndUpdateFragment extends AnimatedFragment {

    /**
     * Instance Attributes
     */
    private final int GET_FROM_GALLERY = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    private EditText carName;
    private EditText carCountryOrigin;
    private EditText hoursePower;
    private EditText motorCapacity;
    private EditText bagSpace;
    private Car car;
    private View carView;

    /**
     * Default Constructor Used When Adding a new Car
     */
    public CarsAddAndUpdateFragment() {
    }

    /**
     * Constructor Used When Update a existing Car
     */
    public CarsAddAndUpdateFragment(Car car, View carView) {
        this.car = car;
        this.carView = carView;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_new_car, container, false);
//        Map Components
        Button addButton = rootView.findViewById(R.id.addButton);
        FloatingActionButton uploadImage = rootView.findViewById(R.id.addImageButton);
        carName = rootView.findViewById(R.id.carName);
        carCountryOrigin = rootView.findViewById(R.id.carCountryOrigin);
        hoursePower = rootView.findViewById(R.id.hoursePower);
        motorCapacity = rootView.findViewById(R.id.motorCapacity);
        bagSpace = rootView.findViewById(R.id.bagSpace);
        imageView = rootView.findViewById(R.id.imageView);

        final util utilMethods = util.getInstance();
        final boolean updateMode = car != null;

        if (updateMode) {
            /**
             * Set Init Data When Update
             */
            if (car.getImg() != null && !car.getImg().trim().equals("")) {
                util.getInstance().setGlideImage(this, car.getImg(), imageView);
            }
            utilMethods.setTextViewValue(carName, car.getCarName());
            utilMethods.setTextViewValue(carCountryOrigin, car.getCountry());
            utilMethods.setTextViewValue(motorCapacity, String.valueOf(car.getMotorCapacity()));
            utilMethods.setTextViewValue(hoursePower, String.valueOf(car.getHoursePower()));
            utilMethods.setTextViewValue(bagSpace, String.valueOf(car.getBagSpace()));
            carName.requestFocus();
            addButton.setText(R.string.update);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean chooseImage = bitmap != null;
                if (utilMethods.getVal(carName).equals("")) {
                    Toast.makeText(getActivity(), R.string.car_name_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(carCountryOrigin).equals("")) {
                    Toast.makeText(getActivity(), R.string.car_country_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(hoursePower).equals("")) {
                    Toast.makeText(getActivity(), R.string.car_hourse_power_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(motorCapacity).equals("")) {
                    Toast.makeText(getActivity(), R.string.car_motor_capacity_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                } else if (utilMethods.getVal(bagSpace).equals("")) {
                    Toast.makeText(getActivity(), R.string.car_bag_space_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                final double hp, mc, bs;
                try {
                    hp = Double.parseDouble(utilMethods.getVal(hoursePower));
                    mc = Double.parseDouble(utilMethods.getVal(motorCapacity));
                    bs = Double.parseDouble(utilMethods.getVal(bagSpace));
                    if (hp <= 0 || mc <= 0 || bs <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), R.string.car_numerical_constraint, Toast.LENGTH_SHORT).show();
                    return;
                }

                loaderDialog.displayLoader();
                Thread addOrUpdateCar = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Car newCar = new Car(utilMethods.getVal(carName), utilMethods.getVal(carCountryOrigin), mc, hp, bs, !chooseImage ? "" : util.getInstance().saveToInternalStorage(CarsAddAndUpdateFragment.this.getContext(), bitmap, "brandImages", new Date().getTime() + ".png"), BrandDetails.getBrand().getId());
                        if (updateMode) {
                            newCar.setId(car.getId());
                            if (chooseImage) {
                                //user change Image
                                util.getInstance().removeFile(car.getImg());
                            } else {
                                newCar.setImg(car.getImg());
                            }
                        }
                        long operationResult = updateMode ? newCar.update() : newCar.insert();
                        if (operationResult > 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), updateMode ? R.string.update_car_success_msg : R.string.add_car_success_msg, Toast.LENGTH_SHORT).show();
                                }
                            });

                            if (getDialog() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getDialog().dismiss();
                                        ((FlexboxLayout) carView.getParent()).removeView(carView);
                                        ((BrandDetails) getActivity()).addCar(newCar);
                                    }
                                });
                            } else {

                                Intent newCarIntent = new Intent();
                                newCarIntent.putExtra("newCar", newCar);
                                getActivity().setResult(RESULT_OK, newCarIntent);
                                getActivity().finish();
                            }
                        } else if ((!updateMode && operationResult == -1) || (updateMode && operationResult == 0)) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.duplicate_car_error_msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.uncatched_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                new CloseLoaderThread(addOrUpdateCar, loaderDialog).start();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = util.getInstance().rotateImage(getContext(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage), selectedImage);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                bitmap = null;
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), R.string.image_file_does_notExists, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                bitmap = null;
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), R.string.image_file_does_notExists, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
