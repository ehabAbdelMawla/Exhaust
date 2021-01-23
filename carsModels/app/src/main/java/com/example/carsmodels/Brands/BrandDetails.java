package com.example.carsmodels.Brands;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.Cars.AddNewCarActivity;
import com.example.carsmodels.Cars.CarsDetails;
import com.example.carsmodels.Cars.CarsAddAndUpdateFragment;
import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Brand;
import com.example.carsmodels.DataModel.Car;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.CloseLoaderThread;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Dialogs.EditOrDeleteDialog;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import static com.example.carsmodels.Main.MainActivity.db;


public class BrandDetails extends AnimatedActivity {
    /**
     * Class Variables Declarations
     */
    private static Brand currentbBrand;

    /**
     * Instance Variables Declarations
     */
    private FlexboxLayout brandCarContainer;
    private final int GET_NEW_CAR_OBJECT = 10;

    /**
     * Activity LifeCycle Events
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_cars);
        brandCarContainer = findViewById(R.id.brandCarContainer);
        setButtonActions();
        loadInitData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.ZoomIn)
                .duration(500).playOn(findViewById(R.id.addCarButton));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_NEW_CAR_OBJECT && resultCode == RESULT_OK && data != null) {
            addCar((Car) data.getSerializableExtra("newCar"));
            checkIfEmpty(brandCarContainer.getChildCount()==0,brandCarContainer,R.string.car_empty_msg);
        }
    }

    /**
     * Set Basic Buttons Actions
     */

    public void setButtonActions() {
        findViewById(R.id.addCarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewCarModel = new Intent(BrandDetails.this, AddNewCarActivity.class);
                startActivityForResult(addNewCarModel, GET_NEW_CAR_OBJECT);
            }
        });
    }

    private void loadInitData() {
//        Set Brand Basic info Data
        util.getInstance().setTextViewValue((TextView) findViewById(R.id.brandAgent), currentbBrand.getBrandAgent());
        this.setTitle(currentbBrand.getBrandName());
//        TODO Delete Image AND Brand Name Block After UI Finished
//        util.getInstance().setTextViewValue((TextView) findViewById(R.id.brandName), currentbBrand.getBrandName());
//        if (currentbBrand.getImg() != null && !currentbBrand.getImg().trim().equals("")) {
//            util.getInstance().setGlideImage(this, currentbBrand.getImg(), (ImageView) findViewById(R.id.brandImage));
//        }
//        ============================================================================
        /**
         * Load All Cars This Methods Should Call only One Time
         */
        LoadCarsData();
    }

    /**
     * Cars Section Methods
     */
    public void LoadCarsData() {
        /**
         * Clear Parent View
         */
        brandCarContainer.removeAllViews();
        loaderDialog.displayLoader();
        new CloseLoaderThread(new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Car> cars = getAllCarsOfBrand();
                for (final Car currentObj : cars) {
                    addCar(currentObj);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkIfEmpty(brandCarContainer.getChildCount()==0,brandCarContainer,R.string.car_empty_msg);
                    }
                });
            }
        }), loaderDialog).start();
    }

    public void addCar(final Car currentObj) {
                final View modelLayOut = View.inflate(BrandDetails.this, R.layout.model_box, null);
                ((TextView) modelLayOut.findViewById(R.id.modelName)).setText(currentObj.getCarName());

                modelLayOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent carDetails = new Intent(BrandDetails.this, CarsDetails.class);
                        CarsDetails.setCar(currentObj);
                        startActivity(carDetails);
                    }
                });

                modelLayOut.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new EditOrDeleteDialog(BrandDetails.this) {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.editIcon:
                                        this.cancel();
                                        new CarsAddAndUpdateFragment(currentObj, modelLayOut).show(getSupportFragmentManager(), "edit_car");
                                        break;
                                    case R.id.deleteIcon:
                                        this.cancel();
                                        new ConfirmDialog(BrandDetails.this, R.string.delete_car_dialog_title, R.string.delete_car_dialog_msg, android.R.drawable.ic_delete) {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                util.getInstance().removeResourseFiles("SELECT carImages.img FROM carImages JOIN Car_Colors ON Car_Colors.carId=" + currentObj.getId() + " AND carImages.relationId=Car_Colors.id");
                                                long result = currentObj.remove();
                                                if (result == 1) {
                                                    Toast.makeText(getApplicationContext(), R.string.delete_car_success_msg, Toast.LENGTH_SHORT).show();
                                                    removeViewWithAnimate(brandCarContainer,modelLayOut, Techniques.ZoomOutDown,350,R.string.car_empty_msg);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.uncatched_error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }.show();
                                        break;
                                }
                            }
                        }.show();
                        return true;
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentObj.getImg() != null && !currentObj.getImg().trim().equals("")) {
                            util.getInstance().setGlideImage(BrandDetails.this, currentObj.getImg(), (ImageView) modelLayOut.findViewById(R.id.modelImage));
                        }
                        addViewWithAnimate(brandCarContainer,modelLayOut,Techniques.ZoomInUp,350);
                    }
                });
    }

    private ArrayList<Car> getAllCarsOfBrand() {
        ArrayList<Car> cars = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM cars WHERE brandId=" + currentbBrand.getId(), null);
            while (res.moveToNext()) {
                cars.add(new Car(
                        res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("carName")),
                        res.getString(res.getColumnIndex("country")),
                        res.getDouble(res.getColumnIndex("motorCapacity")),
                        res.getDouble(res.getColumnIndex("hoursePower")),
                        res.getDouble(res.getColumnIndex("bagSpace")),
                        res.getString(res.getColumnIndex("carImage")),
                        res.getInt(res.getColumnIndex("brandId"))));
            }

        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "getAllBrands", e);
        }
        return cars;
    }

    /**
     * Class Attributes Getters & Setters
     */

    public static void setBrand(Brand brand) {
        currentbBrand = brand;
    }

    public static Brand getBrand() {
        return currentbBrand;
    }

}