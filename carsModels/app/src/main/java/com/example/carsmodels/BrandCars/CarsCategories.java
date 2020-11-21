package com.example.carsmodels.BrandCars;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.CarCategory.AddNewCategory;
import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.dataModel.CarCategoty;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.carsmodels.MainActivity.db;

public class CarsCategories extends AppCompatActivity {
    private static Car currentCar;
    public static CarsCategories CarsCategoriesPointer;
    public static boolean updateData=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_categories);
        CarsCategoriesPointer = this;
        setInitData();


        FloatingActionButton addCarButton = findViewById(R.id.addCategoryButton);
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewCarModel = new Intent(CarsCategoriesPointer, AddNewCategory.class);
                startActivity(addNewCarModel);
            }
        });

    }

    protected void onStart(){
        super.onStart();
        if(updateData){
            setInitData();
            updateData=false;
        }

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
    }

    private void loadCarsCategories() {
        FlexboxLayout brandCarContainer=findViewById(R.id.carsCategories);
        brandCarContainer.removeAllViews();
        ArrayList<CarCategoty> categories=getcategories();
        for(int i=0;i<categories.size();i++){
            final TextView text = new TextView(CarsCategoriesPointer);
            final CarCategoty currentObj=categories.get(i);
            text.setText(currentObj.getCategName()+"\n");
            brandCarContainer.addView(text);
        }
    }

    private ArrayList<CarCategoty> getcategories() {
        ArrayList<CarCategoty> data=new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM carsCategory WHERE carId=" + currentCar.getId(), null);
            while (res.moveToNext()) {
                data.add(new CarCategoty(
                        res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("categoryName")),
                        res.getInt(res.getColumnIndex("carId"))));
            }

        } catch (Exception e) {
            Log.i(CarsCategories.class.getName(), "getAllBrands", e);
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
