package com.example.carsmodels.BrandCars;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Brand;
import com.example.carsmodels.dataModel.Car;
import com.example.carsmodels.speceficeations.SpecificationCuDialogFragment;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.carsmodels.MainActivity.db;


public class BrandCars extends AppCompatActivity {

    private static Brand currentbBrand;
    public static BrandCars BrandCarsPointer;
    public static boolean updateData=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_cars);
        BrandCarsPointer=this;
        setInitData();
        FloatingActionButton addCarButton = findViewById(R.id.addCarButton);
        final BrandCars superThis = this;
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewCarModel = new Intent(superThis, AddNewCar.class);
                startActivity(addNewCarModel);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(updateData){
            setInitData();
            updateData=false;
        }
    }

    private void setInitData() {
//        Map Brand Basic info components
        TextView brandName = findViewById(R.id.brandName);
        ImageView imgView = findViewById(R.id.brandImage);
        TextView brandAgent = findViewById(R.id.brandAgent);
//        Set Brand Basic info Data
        brandName.setText(currentbBrand.getBrandName());
        brandAgent.setText(currentbBrand.getBrandAgent());
        this.setTitle(currentbBrand.getBrandName());
        if (currentbBrand.getImg() != null) {
            imgView.setImageBitmap(BitmapFactory.decodeByteArray(currentbBrand.getImg(), 0, currentbBrand.getImg().length));
        }
        LoadCarsData();
    }

    public void LoadCarsData() {
        FlexboxLayout brandCarContainer=findViewById(R.id.brandCarContainer);
        brandCarContainer.removeAllViews();
        ArrayList<Car> cars=getAllCarsOfBrand();
        final BrandCars globalThis=this;
        for(final Car currentObj:cars){
            final View modelLayOut = View.inflate(globalThis, R.layout.model_box, null);
            ((TextView) modelLayOut.findViewById(R.id.modelName)).setText(currentObj.getCarName());
            if(currentObj.getImg()!=null){
                ((ImageView)modelLayOut.findViewById(R.id.modelImage)).setImageBitmap(BitmapFactory.decodeByteArray(currentObj.getImg(), 0,   currentObj.getImg().length));
            }

            modelLayOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent carDetails = new Intent(BrandCarsPointer, CarsCategories.class );
                    CarsCategories.setCar(currentObj);
                    startActivity(carDetails);
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
                            //                dialog fragment
                            new CarsCuDialogFragment(currentObj).show(getSupportFragmentManager(), "edit_car");
                        }
                    });

                    popUpView.findViewById(R.id.deleteIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            new AlertDialog.Builder(globalThis)
                                    .setTitle("Delete Car?")
                                    .setMessage("All Car Category,specification and images will lost .")
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            long result = currentObj.remove();
                                            if (result == 1) {
                                                Toast.makeText(getApplicationContext(), "Car Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                LoadCarsData();
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
            brandCarContainer.addView(modelLayOut);
        }
    }

    private ArrayList<Car> getAllCarsOfBrand() {
        ArrayList<Car> cars = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM cars WHERE brandId=" + currentbBrand.getId(), null);
            while (res.moveToNext()) {
                cars.add(new Car(
                        res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("carName")),
                        res.getString(res.getColumnIndex("country")),
                        res.getDouble(res.getColumnIndex("motorCapacity")),
                        res.getDouble(res.getColumnIndex("hoursePower")),
                        res.getDouble(res.getColumnIndex("bagSpace")),
                        res.getBlob(res.getColumnIndex("carImage")),
                        res.getInt(res.getColumnIndex("brandId"))));
            }

        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "getAllBrands", e);
        }
        return cars;
    }


    public static void setBrand(Brand brand) {
        currentbBrand = brand;
    }

    public static Brand getBrand() {
        return currentbBrand;
    }

}