package com.example.carsmodels.BrandCars;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.CarCategory.AddNewCategory;
import com.example.carsmodels.Colors.addNewColorOrSelectPrevOneDialogFragment;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.dataModel.CarCategoty;
import com.example.carsmodels.dataModel.CarColor;
import com.example.carsmodels.dataModel.Color;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.carsmodels.MainActivity.db;

public class CarsCategories extends AppCompatActivity {
    private static Car currentCar;
    public static CarsCategories CarsCategoriesPointer;
    public static boolean updateData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_categories);
        CarsCategoriesPointer = this;
        setInitData();

        final FloatingActionButton addCarButton = findViewById(R.id.addCategoryButton);
        FloatingActionButton listButton = findViewById(R.id.listButton);
        final FloatingActionButton ColorActionButton = findViewById(R.id.ColorAction);
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewCarModel = new Intent(CarsCategoriesPointer, AddNewCategory.class);
                startActivity(addNewCarModel);
            }
        });
        ColorActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addNewColorOrSelectPrevOneDialogFragment(currentCar.getId()).show(getSupportFragmentManager(), "addOrSelectPrevColor");
            }
        });


        listButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (addCarButton.getVisibility() == View.INVISIBLE) {
                    YoYo.with(Techniques.SlideInUp).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            addCarButton.setVisibility(View.VISIBLE);
                        }
                    }).duration(350).repeat(0).playOn(addCarButton);

                    YoYo.with(Techniques.SlideInUp).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            ColorActionButton.setVisibility(View.VISIBLE);
                        }
                    }).duration(450).repeat(0).playOn(ColorActionButton);

                } else {
                    YoYo.with(Techniques.SlideOutDown).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            addCarButton.setVisibility(View.INVISIBLE);
                        }
                    }).duration(350).repeat(0).playOn(addCarButton);

                    YoYo.with(Techniques.SlideOutDown).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            ColorActionButton.setVisibility(View.INVISIBLE);
                        }
                    }).duration(450).repeat(0).playOn(ColorActionButton);


                }
            }
        });


    }

    protected void onStart() {
        super.onStart();
        if (updateData) {
            setInitData();
            updateData = false;
        }
        findViewById(R.id.addCategoryButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.ColorAction).setVisibility(View.INVISIBLE);

    }

    private void setInitData() {
        //        Set Car Basic info components
        ((TextView) findViewById(R.id.carName)).setText(currentCar.getCarName());
        ((TextView) findViewById(R.id.carCountryOrigin)).setText(currentCar.getCountry());
        ((TextView) findViewById(R.id.hoursePower)).setText(currentCar.getHoursePower() + " H");
        ((TextView) findViewById(R.id.motorCapacity)).setText(currentCar.getMotorCapacity() + " CC");
        ((TextView) findViewById(R.id.bagSpace)).setText(currentCar.getBagSpace() + " L");
        this.setTitle(currentCar.getCarName());
        loadCarsCategories();
        loadCarColors();
    }

    public void loadCarColors() {
        FlexboxLayout colorsContainer = findViewById(R.id.carColors);
        colorsContainer.removeAllViews();
        ArrayList<CarColor> carColors = util.getInstance().getCarColors(currentCar.getId());
        for (final CarColor color : carColors) {
            final View colorLayOut = View.inflate(this, R.layout.color_item, null);
            colorLayOut.findViewById(R.id.view).setBackgroundColor(android.graphics.Color.parseColor("#" + color.getColorHexCode()));
            colorLayOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent colorImages = new Intent(CarsCategoriesPointer, CarColorImages.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("relationId", color.getRealtionId());
                    bundle.putString("CarName", currentCar.getCarName());
                    colorImages.putExtras(bundle);
                    startActivity(colorImages);
                }
            });
            colorsContainer.addView(colorLayOut);
        }

    }

    private void loadCarsCategories() {
        FlexboxLayout brandCarContainer = findViewById(R.id.carsCategories);
        brandCarContainer.removeAllViews();
        ArrayList<CarCategoty> categories = getcategories();
        for (int i = 0; i < categories.size(); i++) {
            final TextView text = new TextView(CarsCategoriesPointer);
            final CarCategoty currentObj = categories.get(i);
            text.setText(currentObj.getCategName() + "\n");
            brandCarContainer.addView(text);
        }
    }

    private ArrayList<CarCategoty> getcategories() {
        ArrayList<CarCategoty> data = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM carsCategory WHERE carId=" + currentCar.getId(), null);
            while (res.moveToNext()) {
                data.add(new CarCategoty(
                        res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("categoryName")),
                        res.getInt(res.getColumnIndex("carId"))));
            }

        } catch (Exception e) {
            Log.i(CarsCategories.class.getName(), "getcategories", e);
        }
        return data;
    }


    public static void setCar(Car carObj) {
        currentCar = carObj;
    }

    public static Car getCurrentCar() {
        return currentCar;
    }
}
