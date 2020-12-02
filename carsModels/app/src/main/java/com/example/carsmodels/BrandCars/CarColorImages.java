package com.example.carsmodels.BrandCars;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.CarImage;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CarColorImages extends AppCompatActivity {

    FloatingActionButton addButton;
    String cardName;
    int relationId;
    int GET_FROM_GALLERY = 1;
    FlexboxLayout imagesContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specification_settings);
//        get Bundle Paramteres
        Bundle b = getIntent().getExtras();
        if (b != null) {
            cardName = b.getString("CarName");
            relationId = b.getInt("relationId");
        }
        setTitle(cardName);

//        Map Componnet
        addButton = findViewById(R.id.addNewSpec);
        imagesContainer = findViewById(R.id.specificationContainer);


//        Set Button Action
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_FROM_GALLERY);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImages();
    }

    private void loadImages() {
        imagesContainer.removeAllViews();
        ArrayList<CarImage> images = util.getInstance().getCarImages(relationId);

        for (CarImage imgObj : images) {
            View ImageViewParent = View.inflate(this, R.layout.car_image_item, null);
            ((ImageView) ImageViewParent.findViewById(R.id.ImageView)).setImageBitmap(BitmapFactory.decodeByteArray(imgObj.getImg(), 0, imgObj.getImg().length));
            imagesContainer.addView(ImageViewParent);
        }


    }

    //   Load Image when retreve result
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        try {

            if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getData() != null) {
                    try {
                        Uri mImageUri = data.getData();
                        Bitmap bitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                        long result = util.getInstance().addColorImage(relationId, util.getInstance().getBitmapAsByteArray(bitMap));
                        if (result > 0) {
                            Toast.makeText(getApplicationContext(), "Image Added Successfully", Toast.LENGTH_SHORT).show();
                        } else if (result == -1) {
                            Toast.makeText(getApplicationContext(), "Image Already Exist!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                        }

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (data.getClipData() != null) {
                       new Thread(new Runnable() {
                           @Override
                           public void run() {

                               ClipData mClipData = data.getClipData();
                                final int totalImages=mClipData.getItemCount();
                               int repeatedImage=0,failImage=0;
                               long result;
                               for (int i = 0; i < mClipData.getItemCount(); i++) {
                                   try {
                                       ClipData.Item item = mClipData.getItemAt(i);
                                       Uri uri = item.getUri();
                                       Bitmap bitMap = MediaStore.Images.Media.getBitmap(CarColorImages.this.getContentResolver(), uri);
                                       result=util.getInstance().addColorImage(relationId, util.getInstance().getBitmapAsByteArray(bitMap));
                                        if (result == -1) {
                                           repeatedImage+=1;
                                       } else if(result < 0) {
                                           failImage+=1;
                                       }
                                   } catch (Exception ex) {
                                       System.out.println("Exception " + ex);
                                   }
                               }
                               showToast(totalImages,repeatedImage,failImage);
                           }
                       }).start();
                    }
                }


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


    }

    public void showToast(final int totalImages, final int finalRepeatedImage, final int finalFailImage){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalRepeatedImage == 0 && finalFailImage == 0) {
                    Toast.makeText(CarColorImages.this, totalImages + " Images Added Successfully", Toast.LENGTH_LONG).show();
                } else if (finalFailImage == 0) {
                    Toast.makeText(CarColorImages.this, (totalImages - finalFailImage) + " Images Added \n" + finalFailImage + " Faild ", Toast.LENGTH_LONG).show();

                } else if (finalRepeatedImage == 0) {
                    Toast.makeText(CarColorImages.this, (totalImages - finalRepeatedImage) + " Images Added \n" + finalRepeatedImage + " Already Exists", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CarColorImages.this, (totalImages - (finalRepeatedImage + finalFailImage)) + " Images Added \n" + finalRepeatedImage + " Already Exists \n" + finalFailImage + "Faild", Toast.LENGTH_LONG).show();
                }
                loadImages();   // High Cost , must fix in updates
            }
        });


    }
}

