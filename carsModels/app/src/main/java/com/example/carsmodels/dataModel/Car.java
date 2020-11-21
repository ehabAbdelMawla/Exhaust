package com.example.carsmodels.dataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.util.util;

public class Car {
    private int id;
    private String carName;
    private String country;
    private double motorCapacity;
    private double hoursePower;
    private double bagSpace;
    private byte[] img;
    private int brandId;

    //    Creation Constructor Constructor
    public Car( String carName, String country, double motorCapacity, double hoursePower, double bagSpace, byte[] img, int brandId) {
        this.carName = carName;
        this.country = country;
        this.motorCapacity = motorCapacity;
        this.hoursePower = hoursePower;
        this.bagSpace = bagSpace;
        this.img = img;
        this.brandId = brandId;
    }

//    Retreival Constructor
    public Car(int id, String carName, String country, double motorCapacity, double hoursePower, double bagSpace, byte[] img, int brandId) {
        this.id = id;
        this.carName = carName;
        this.country = country;
        this.motorCapacity = motorCapacity;
        this.hoursePower = hoursePower;
        this.bagSpace = bagSpace;
        this.img = img;
        this.brandId = brandId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getMotorCapacity() {
        return motorCapacity;
    }

    public void setMotorCapacity(double motorCapacity) {
        this.motorCapacity = motorCapacity;
    }

    public double getHoursePower() {
        return hoursePower;
    }

    public void setHoursePower(double hoursePower) {
        this.hoursePower = hoursePower;
    }

    public double getBagSpace() {
        return bagSpace;
    }

    public void setBagSpace(double bagSpace) {
        this.bagSpace = bagSpace;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    //    Data Base Handle
    public long insert() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", util.getInstance().getMaximum("id","cars"));
            myValues.put("carName", this.carName);
            myValues.put("country", this.country);
            myValues.put("motorCapacity", this.motorCapacity);
            myValues.put("hoursePower", this.hoursePower);
            myValues.put("bagSpace", this.bagSpace);
            myValues.put("carImage", this.img);
            myValues.put("brandId", this.brandId);
            return databaseWriteable.insert("cars", null, myValues);
        } catch (Exception e) {
            Log.i("Cars", "insert", e);
        }
        return 0;
    }
}
