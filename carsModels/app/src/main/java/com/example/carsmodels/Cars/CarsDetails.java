package com.example.carsmodels.Cars;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.Cars.CarCategory.customeCategoryView;
import com.example.carsmodels.Cars.Colors.CustomeColorView;
import com.example.carsmodels.Cars.CarCategory.AddNewCategory;
import com.example.carsmodels.Cars.Colors.AddNewColorOrSelectPrevOneDialogFragment;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Car;
import com.example.carsmodels.DataModel.CarCategoty;
import com.example.carsmodels.DataModel.CarColor;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;

import static com.example.carsmodels.Main.MainActivity.db;

public class CarsDetails extends AnimatedActivity {
    /**
     * Class Variables Declarations
     */
    private static Car currentCar;
    public static Map<Integer, String> specificationImages;

    /**
     * Instance Variables Declarations
     */
    private FlexboxLayout carCategoriesContainer;
    private final int ADD_NEW_CATEGORY = 100;
    protected TextView emptyTextViewForCategories;

    /**
     * Activity LifeCycle Events
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_details);
        /**
         * load specification in static Map
         */
        specificationImages = util.getInstance().getAllSystemSpecificationImages();
        /**
         * Map Components
         */
        carCategoriesContainer = findViewById(R.id.carsCategories);

        /**
         * Load Data
         */
        loadInitData();

        /**
         * Add Buttons Actions
         */
        setButtonActions();
        /**
         * Remove Colors that Have No Referances (Relations)
         */
        util.getInstance().removeNonUsedColors();
    }

    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.ZoomInUp)
                .duration(500).playOn(findViewById(R.id.listButton));
        YoYo.with(Techniques.ZoomInUp)
                .duration(500).playOn(findViewById(R.id.headerParent));

        hideOptionsButtons();

    }

    /**
     * Activity Events
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_CATEGORY && resultCode == Activity.RESULT_OK && data != null) {
            CarCategoty newObj = (CarCategoty) data.getSerializableExtra("newObj");
            addViewWithAnimate(carCategoriesContainer, new customeCategoryView(this, newObj), Techniques.ZoomInUp, 350);
            carCategoriesContainer.setAlignItems(AlignItems.STRETCH);
            checkIfEmpty(carCategoriesContainer.getChildCount() == 0, carCategoriesContainer, R.string.cars_categories_empty_msg);
        }
    }


    /**
     * Basic Buttons Actions
     */
    public void setButtonActions() {
        final FloatingActionButton addCarCategoryButton = findViewById(R.id.addCategoryButton);
        final FloatingActionButton listButton = findViewById(R.id.listButton);
        final FloatingActionButton ColorActionButton = findViewById(R.id.ColorAction);
        addCarCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddNewBrandIntent = new Intent(CarsDetails.this, AddNewCategory.class);
                startActivityForResult(AddNewBrandIntent, ADD_NEW_CATEGORY);
            }
        });

        ColorActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddNewColorOrSelectPrevOneDialogFragment(currentCar.getId()).show(getSupportFragmentManager(), "addOrSelectPrevColor");
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (addCarCategoryButton.getVisibility() == View.INVISIBLE) {
                    YoYo.with(Techniques.SlideInUp).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            addCarCategoryButton.setVisibility(View.VISIBLE);
                        }
                    }).duration(350).repeat(0).playOn(addCarCategoryButton);

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
                            addCarCategoryButton.setVisibility(View.INVISIBLE);
                        }
                    }).duration(350).repeat(0).playOn(addCarCategoryButton);

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

    private void hideOptionsButtons() {
        findViewById(R.id.addCategoryButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.ColorAction).setVisibility(View.INVISIBLE);
    }

    /**
     * Help Methods
     */
    private void loadInitData() {
        //        Set Car Basic info components
        util.getInstance().setTextViewValue((TextView) findViewById(R.id.carCountryOrigin), currentCar.getCountry());
        util.getInstance().setTextViewValue((TextView) findViewById(R.id.hoursePower), currentCar.getHoursePower() + " HP");
        util.getInstance().setTextViewValue((TextView) findViewById(R.id.motorCapacity), currentCar.getMotorCapacity() + " CC");
        util.getInstance().setTextViewValue((TextView) findViewById(R.id.bagSpace), currentCar.getBagSpace() + " L");
        this.setTitle(currentCar.getCarName());
        loadCarsCategories();
        loadCarColors();
    }

    /**
     * Colors Section Methods
     */
    public void loadCarColors() {
        final FlexboxLayout colorsContainer = findViewById(R.id.carColors);
        colorsContainer.removeAllViews();
        ArrayList<CarColor> carColors = currentCar.getCarColors();
        for (final CarColor color : carColors) {
            addViewWithAnimate(colorsContainer, new CustomeColorView(CarsDetails.this, color, currentCar.getId(), currentCar.getCarName()),
                    Techniques.ZoomInUp, 350);
        }
        checkIfEmpty(colorsContainer.getChildCount() == 0, colorsContainer, R.string.cars_colors_empty_msg);
    }

    public void addColor(CarColor carColorObj, int index) {
        final FlexboxLayout colorsContainer = findViewById(R.id.carColors);
        addViewWithAnimate(colorsContainer, new CustomeColorView(CarsDetails.this, carColorObj, currentCar.getId(), currentCar.getCarName()), index,
                Techniques.ZoomInUp, 350);
        checkIfEmpty(colorsContainer.getChildCount() == 0, colorsContainer, R.string.cars_colors_empty_msg);
    }


    /**
     * Car Categories Section Methods
     */
    public void loadCarsCategories() {
        /**
         * clear parent View
         */
        carCategoriesContainer.removeAllViews();
        ArrayList<CarCategoty> categories = getcategories();
        for (final CarCategoty category : categories) {
            addViewWithAnimate(carCategoriesContainer, new customeCategoryView(this, category), Techniques.ZoomInUp, 350);
        }
        if (carCategoriesContainer.getChildCount() > 0) {
            carCategoriesContainer.setAlignItems(AlignItems.STRETCH);
        }
        checkIfEmpty(carCategoriesContainer.getChildCount() == 0, carCategoriesContainer, R.string.cars_categories_empty_msg);
    }

    private ArrayList<CarCategoty> getcategories() {
        ArrayList<CarCategoty> data = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT carsCategory.id,carsCategory.categoryName,carsCategory.carId FROM carsCategory WHERE carsCategory.carId=" + currentCar.getId(), null);
            while (res.moveToNext()) {
                data.add(new CarCategoty(
                        res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("categoryName")),
                        res.getInt(res.getColumnIndex("carId"))));
            }

        } catch (Exception e) {
            Log.i(CarsDetails.class.getName(), "getcategories", e);
        }
        return data;
    }

    /**
     * Class Methods
     *
     * @param carObj current Car To Work with
     */
    public static void setCar(Car carObj) {
        currentCar = carObj;
    }

    public static Car getCurrentCar() {
        return currentCar;
    }


}
