package com.example.carsmodels.addNewBrand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Brand;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddNewBrand extends AppCompatActivity {


    final int GET_FROM_GALLERY = 1;
    byte[] imageBytes;

    //   Load Image when retreve result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                Compress Image
//                bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);
                imageBytes = util.getInstance().getBitmapAsByteArray(bitmap);

                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_brand);


    }

    @Override
    protected void onStart() {
        super.onStart();
        final util utilMethods = util.getInstance();
//        Map Components
        final EditText brandNameText = findViewById(R.id.brandName);
        final EditText brandAgentText = findViewById(R.id.brandAgent);
        Button addButton = findViewById(R.id.addButton);
        FloatingActionButton uploadImage = findViewById(R.id.addImageButton);
//        Add Button Actions
        final AddNewBrand superThis=this;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilMethods.getVal(brandNameText).equals("")) {
                    Toast.makeText(getApplicationContext(), "Brand Name is Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (utilMethods.getVal(brandAgentText).equals("")) {
                    Toast.makeText(getApplicationContext(), "Brand Agent is Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                Brand newBrand= new Brand(utilMethods.getVal(brandNameText),utilMethods.getVal(brandAgentText),imageBytes);
                long operationResult=newBrand.insert();
                if(operationResult>0){
                    Toast.makeText(getApplicationContext(),"Brand Added Successfully",Toast.LENGTH_SHORT).show();
                    MainActivity.updateData=true;
                    superThis.finish();
                }else if(operationResult==-1){
                    Toast.makeText(getApplicationContext(),"Brand Already Exist!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Uncatched Error ",Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
    }



}
