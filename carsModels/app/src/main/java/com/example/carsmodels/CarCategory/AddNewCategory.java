package com.example.carsmodels.CarCategory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.BrandCars.CarsCategories;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.CarCategoty;
import com.example.carsmodels.util.util;

public class AddNewCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);
        final AddNewCategory superThis=this;
        ((Button)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText CategoryName=findViewById(R.id.categName);
                if(util.getInstance().getVal(CategoryName).equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Category Name is Required.",Toast.LENGTH_SHORT).show();
                    return;
                }
                CarCategoty newCateg=new CarCategoty(util.getInstance().getVal(CategoryName),CarsCategories.getCurrentCar().getId());
                long operationResult=newCateg.insert();

                if(operationResult>0){
                    Toast.makeText(getApplicationContext(),"Category Added Successfully",Toast.LENGTH_SHORT).show();
                    CarsCategories.updateData=true;
                    superThis.finish();
                }else if(operationResult==-1){
                    Toast.makeText(getApplicationContext(),"Category Already Exist!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Uncatched Error ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

