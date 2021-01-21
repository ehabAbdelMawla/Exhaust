package com.example.carsmodels.Main;

import androidx.annotation.Nullable;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.DB.DB;
import com.example.carsmodels.R;
import com.example.carsmodels.Brands.BrandAddAndUpdateFragment;
import com.example.carsmodels.Brands.AddNewBrandActivity;
import com.example.carsmodels.DataModel.Brand;
import com.example.carsmodels.Speceficeations.specificationSettings;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.CloseLoaderThread;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Dialogs.EditOrDeleteDialog;
import com.example.carsmodels.util.Loader.Loader;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AnimatedActivity {

    /**
     * Class Attributes
     */
    public static DB db;
    /**
     * Instance Attributes
     */
    private FlexboxLayout modelsContainer;
    private int GET_NEW_BRAND_OBJECT = 500;

    /**
     * Activity LifeCycle Events
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB(this);
        modelsContainer = findViewById(R.id.modelsContainer);
        loaderDialog = new Loader(this);
        loadBrands();
        SetButtonsAction();
    }


    @Override
    protected void onStart() {
        super.onStart();
        hideButtonsList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_NEW_BRAND_OBJECT && resultCode == RESULT_OK && data != null) {
            addBrand((Brand) data.getSerializableExtra("newBrand"));
        }
    }

    /**
     * Help Methods
     */
    private void hideButtonsList() {
        findViewById(R.id.addBrandButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.specButton).setVisibility(View.INVISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(500).playOn(findViewById(R.id.listButton));
    }

    public void SetButtonsAction() {
//        add Brand Button
        final FloatingActionButton addBrandButton = findViewById(R.id.addBrandButton);
        addBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddNewBrandActivity.class), GET_NEW_BRAND_OBJECT);
            }
        });


        final FloatingActionButton specButton = findViewById(R.id.specButton);
        specButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, specificationSettings.class));
            }
        });

//    List Animation init
        FloatingActionButton listButton = findViewById(R.id.listButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (addBrandButton.getVisibility() == View.INVISIBLE) {
                    YoYo.with(Techniques.SlideInUp).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            addBrandButton.setVisibility(View.VISIBLE);
                        }
                    }).duration(350).repeat(0).playOn(addBrandButton);

                    YoYo.with(Techniques.SlideInUp).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            findViewById(R.id.specButton).setVisibility(View.VISIBLE);
                        }
                    }).duration(450).repeat(0).playOn(specButton);

                } else {
                    YoYo.with(Techniques.SlideOutDown).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            addBrandButton.setVisibility(View.INVISIBLE);
                        }
                    }).duration(350).repeat(0).playOn(addBrandButton);

                    YoYo.with(Techniques.SlideOutDown).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            findViewById(R.id.specButton).setVisibility(View.INVISIBLE);
                        }
                    }).duration(450).repeat(0).playOn(specButton);


                }
            }
        });
    }

    /**
     * Brands Section
     */
    public void loadBrands() {
        modelsContainer.removeAllViews();
        final ArrayList<Brand> brands = getAllBrands();
        loaderDialog.displayLoader();
        Thread addBrands = new Thread(new Runnable() {
            @Override
            public void run() {
                for (final Brand brand : brands) {
                    addBrand(brand);
                }
            }
        });
        new CloseLoaderThread(addBrands,loaderDialog).start();
    }

    public void addBrand(final Brand brand) {
        final View brandView = View.inflate(MainActivity.this, R.layout.model_box, null);
        ((TextView) brandView.findViewById(R.id.modelName)).setText(brand.getBrandName());


        brandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BrandDetails = new Intent(MainActivity.this, com.example.carsmodels.Brands.BrandDetails.class);
                com.example.carsmodels.Brands.BrandDetails.setBrand(brand);
                startActivity(BrandDetails);

            }
        });

        brandView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new EditOrDeleteDialog(MainActivity.this) {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.editIcon:
                                this.cancel();
                                new BrandAddAndUpdateFragment(brand, brandView).show(getSupportFragmentManager(), "edit_Brand");
                                break;
                            case R.id.deleteIcon:
                                this.cancel();
                                new ConfirmDialog(MainActivity.this, R.string.delete_brand_dialog_title, R.string.delete_brand_dialog_msg, android.R.drawable.ic_delete) {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        util.getInstance().removeResourseFiles("SELECT brands.id,carImages.img FROM carImages " +
                                                " JOIN brands JOIN cars JOIN Car_Colors " +
                                                " ON  cars.brandId= brands.id " +
                                                " AND carImages.relationId=Car_Colors.id " +
                                                " AND cars.id=Car_Colors.carId " +
                                                " AND brands.id=" + brand.getId());

                                        long result = brand.remove();
                                        if (result == 1) {
                                            Toast.makeText(getApplicationContext(), R.string.delete_brand_success_msg, Toast.LENGTH_SHORT).show();
                                            ((FlexboxLayout) brandView.getParent()).removeView(brandView);
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
                if (brand.getImg() != null && !brand.getImg().trim().equals("")) {
                    util.getInstance().setGlideImage(MainActivity.this, brand.getImg(), (ImageView) brandView.findViewById(R.id.modelImage));
                }
                modelsContainer.addView(brandView);
            }
        });

    }

    public ArrayList<Brand> getAllBrands() {
        ArrayList<Brand> brands = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM brands", null);
            while (res.moveToNext()) {
                brands.add(new Brand(res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("brandName")),
                        res.getString(res.getColumnIndex("brandAgent")),
                        res.getString(res.getColumnIndex("brandImage"))));
            }

        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "getAllBrands", e);
        }
        return brands;
    }


}