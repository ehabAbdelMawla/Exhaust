package com.example.carsmodels.dataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.util.util;

public class CarCategoty {
    private int id;
    private String categName;
    private int carId;

    //  retrive Constructor
    public CarCategoty(int id,String categName,int carId){
        this.id=id;
        this.categName=categName;
        this.carId=carId;
    }

//  Creation Constructor
    public CarCategoty(String categName,int carId){
        this.categName=categName;
        this.carId=carId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategName() {
        return categName;
    }

    public void setCategName(String categName) {
        this.categName = categName;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    // Data base Method
public long insert() {
    try {
        SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
        ContentValues myValues = new ContentValues();
        myValues.put("id", util.getInstance().getMaximum("id","carsCategory"));
        myValues.put("categoryName", this.categName);
        myValues.put("carId", this.carId);
        return databaseWriteable.insert("carsCategory", null, myValues);
    } catch (Exception e) {
        Log.i("CarCategoty", "insert", e);
    }
    return 0;
}
}
