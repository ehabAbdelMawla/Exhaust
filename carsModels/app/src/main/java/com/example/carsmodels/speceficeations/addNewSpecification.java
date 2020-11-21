package com.example.carsmodels.speceficeations;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.BrandCars.BrandCars;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.dataModel.Specification;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class addNewSpecification extends AppCompatActivity {

    final int GET_FROM_GALLERY = 1;
    byte[] imageBytes;
    Button AddnewSpecificationButton;
    FloatingActionButton uploadImageButton;
    ImageView imageView;
    EditText specificationName;







    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_specification);


//        Init Vars
        imageView = findViewById(R.id.imageView);
        AddnewSpecificationButton = findViewById(R.id.addButton);
        uploadImageButton = findViewById(R.id.addImageButton);
        specificationName=findViewById(R.id.specificationEditText);

        final  addNewSpecification globalThis=this;
//        Set Actions


        AddnewSpecificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(util.getInstance().getVal(specificationName).equalsIgnoreCase("")){
                Toast.makeText(globalThis,"Specification Name is Required",Toast.LENGTH_LONG).show();
            }
            else if(imageBytes==null){
                Toast.makeText(globalThis,"Specification icon is Required",Toast.LENGTH_LONG).show();
            }else{
                Specification newSpecification = new Specification(util.getInstance().getVal(specificationName),imageBytes);
                long operationResult = newSpecification.insert();
                if (operationResult > 0) {
                    Toast.makeText(getApplicationContext(), "Specification Added Successfully", Toast.LENGTH_SHORT).show();
                    specificationSettings.updateData=true;
                    globalThis.finish();
                } else if (operationResult == -1) {
                    Toast.makeText(getApplicationContext(), "Specification  Already Exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                }
            }

            }
        });

            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                }
            });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //   Load Image when retreve result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            System.out.println(selectedImage);
            System.out.println(selectedImage);
            System.out.println(selectedImage);

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



}
