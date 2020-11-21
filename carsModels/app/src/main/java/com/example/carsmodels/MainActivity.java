package com.example.carsmodels;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.carsmodels.addNewBrand.AddNewBrand;
import com.example.carsmodels.dataModel.Brand;
import com.example.carsmodels.speceficeations.specificationSettings;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
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
                            final AlertDialog.Builder editBrand = new AlertDialog.Builder(globalThis);
                            View editView = getLayoutInflater().inflate(R.layout.add_new_brand, null);
                            editBrand.setView(editView);
                            final AlertDialog editAlert = editBrand.create();
                            editAlert.show();

//                            Init Components
                            imageView = editView.findViewById(R.id.imageView);
                            final EditText brandNameText = editView.findViewById(R.id.brandName);
                            final EditText brandAgentText = editView.findViewById(R.id.brandAgent);
                            Button editButton = editView.findViewById(R.id.addButton);
                            FloatingActionButton uploadImageButton = editView.findViewById(R.id.addImageButton);
//                            Set Initial Data
                            brandNameText.setText(temp.getBrandName());
                            brandAgentText.setText(temp.getBrandAgent());
                            editButton.setText("Edit");
                            if (temp.getImg() != null) {
                                imageBytes = temp.getImg();
                                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                            } else {
                                imageBytes = null;
                            }

                            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                                }
                            });

                            editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (util.getInstance().getVal(brandNameText).equalsIgnoreCase("")) {
                                        Toast.makeText(globalThis, "Brand Name can not be empty", Toast.LENGTH_LONG);
                                    } else if (util.getInstance().getVal(brandAgentText).equalsIgnoreCase("")) {
                                        Toast.makeText(globalThis, "Brand Agent can not be empty", Toast.LENGTH_LONG);
                                    } else {
                                        temp.setBrandAgent(util.getInstance().getVal(brandAgentText));
                                        temp.setBrandName(util.getInstance().getVal(brandNameText));
                                        if (imageBytes != null) {
                                            temp.setImg(imageBytes);
                                        }
                                        long result = temp.update();
                                        if (result > 0) {
                                            Toast.makeText(getApplicationContext(), "Brand updated Successfully", Toast.LENGTH_SHORT).show();
                                        } else if (result == 0) {
                                            Toast.makeText(getApplicationContext(), "Brand Already Exist!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                        }
                                        loadModels();
                                        editAlert.cancel();
                                    }
                                }
                            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//               === Compress Image ===
//                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                imageBytes = util.getInstance().getBitmapAsByteArray(bitmap);

                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void SetButtonsAction() {
//        add Brand Button
        final FloatingActionButton addBrandButton = findViewById(R.id.addBrandButton);
        addBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddNewBrandIntent = new Intent(MainActivityPointer, AddNewBrand.class);
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