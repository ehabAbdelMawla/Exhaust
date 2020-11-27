package com.example.carsmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.BrandCars.BrandCars;
import com.example.carsmodels.Colors.ColorsSettings;
import com.example.carsmodels.DB.DB;
import com.example.carsmodels.addNewBrand.BrandCuDialogFragment;
import com.example.carsmodels.addNewBrand.addNewBrandActivity;
import com.example.carsmodels.dataModel.Brand;
import com.example.carsmodels.speceficeations.SpecificationCuDialogFragment;
import com.example.carsmodels.speceficeations.specificationSettings;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static DB db;
    public static MainActivity MainActivityPointer;
    public static boolean updateData = false;
    ImageView imageView;      //image of edit Brand Pop Up
    byte[] imageBytes;
    int GET_FROM_GALLERY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB(this);
        loadModels();
        MainActivityPointer = this;
        SetButtonsAction();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (updateData) {
            loadModels();
            updateData = false;
        }
        findViewById(R.id.addBrandButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.colorsButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.specButton).setVisibility(View.INVISIBLE);

        YoYo.with(Techniques.SlideInUp)
                .duration(500).playOn(findViewById(R.id.listButton));
    }


    public void loadModels() {
        FlexboxLayout modelsContainer = findViewById(R.id.modelsContainer);
        modelsContainer.removeAllViews();
        ArrayList<Brand> brands = getAllBrands();
        final MainActivity globalThis = this;
        for (int i = 0; i < brands.size(); i++) {
            final View modelLayOut = View.inflate(globalThis, R.layout.model_box, null);
            ((TextView) modelLayOut.findViewById(R.id.modelName)).setText(brands.get(i).getBrandName());
            System.out.println(brands.get(i).getImg());
            if (brands.get(i).getImg() != null) {
                ((ImageView) modelLayOut.findViewById(R.id.modelImage)).setImageBitmap(BitmapFactory.decodeByteArray(brands.get(i).getImg(), 0, brands.get(i).getImg().length));
            }
            final Brand temp = brands.get(i);

            modelLayOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent BrandDetails = new Intent(MainActivityPointer, BrandCars.class);
                    BrandCars.setBrand(temp);
                    startActivity(BrandDetails);

                }
            });

            modelLayOut.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder alertDialogBuilde = new AlertDialog.Builder(globalThis);
                    final View popUpView = getLayoutInflater().inflate(R.layout.edit_delete_popup, null);
                    alertDialogBuilde.setView(popUpView);
                    final AlertDialog alert = alertDialogBuilde.create();
                    alert.show();
                    popUpView.findViewById(R.id.editIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            new BrandCuDialogFragment(temp).show(getSupportFragmentManager(), "edit_Brand");
                        }
                    });
                    popUpView.findViewById(R.id.deleteIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            new AlertDialog.Builder(globalThis)
                                    .setTitle("Delete Brand?")
                                    .setMessage("All cars and cars Categories will deleted too.")
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            long result = temp.remove();
                                            if (result == 1) {
                                                Toast.makeText(getApplicationContext(), "Brand Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                loadModels();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    });
                    return true;
                }
            });

            modelsContainer.addView(modelLayOut);
        }
    }

    public ArrayList<Brand> getAllBrands() {
        ArrayList<Brand> brands = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM brands", null);
            while (res.moveToNext()) {

                brands.add(new Brand(res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("brandName")),
                        res.getString(res.getColumnIndex("brandAgent")),
                        res.getBlob(res.getColumnIndex("brandImage"))));


            }

        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "getAllBrands", e);
        }
        return brands;
    }


    public void SetButtonsAction() {
//        add Brand Button
        final FloatingActionButton addBrandButton = findViewById(R.id.addBrandButton);
        addBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddNewBrandIntent = new Intent(MainActivityPointer, addNewBrandActivity.class);
                startActivity(AddNewBrandIntent);
            }
        });
        final FloatingActionButton colorsButton = findViewById(R.id.colorsButton);
        colorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddNewBrandIntent = new Intent(MainActivityPointer, ColorsSettings.class);
                startActivity(AddNewBrandIntent);

            }
        });

        final FloatingActionButton specButton = findViewById(R.id.specButton);
        specButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSpecificationObject = new Intent(MainActivityPointer, specificationSettings.class);
                startActivity(addSpecificationObject);
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
                            findViewById(R.id.colorsButton).setVisibility(View.VISIBLE);
                        }
                    }).duration(400).repeat(0).playOn(colorsButton);

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
                            findViewById(R.id.colorsButton).setVisibility(View.INVISIBLE);
                        }
                    }).duration(400).repeat(0).playOn(colorsButton);
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
}