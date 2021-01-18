package com.example.carsmodels.DataModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.util.util;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.carsmodels.Main.MainActivity.db;

public class Car implements Serializable {
    /**
     * Instance Attributes
     */
    private int id;
    private String carName;
    private String country;
    private double motorCapacity;
    private double hoursePower;
    private double bagSpace;
    private String img;
    private int brandId;

    /**
     * Constructors
     */
    public Car(String carName, String country, double motorCapacity, double hoursePower, double bagSpace, String img, int brandId) {
        this.carName = carName;
        this.country = country;
        this.motorCapacity = motorCapacity;
        this.hoursePower = hoursePower;
        this.bagSpace = bagSpace;
        this.img = img;
        this.brandId = brandId;
    }

    public Car(int id, String carName, String country, double motorCapacity, double hoursePower, double bagSpace, String img, int brandId) {
        this.id = id;
        this.carName = carName;
        this.country = country;
        this.motorCapacity = motorCapacity;
        this.hoursePower = hoursePower;
        this.bagSpace = bagSpace;
        this.img = img;
        this.brandId = brandId;
    }

    /**
     * Getters & Setters
     */
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    /**
     * DataBase Section
     *
     * @return result of DB Action
     */
    public long insert() {
        try {
            setId(util.getInstance().getMaximum("id", "cars"));
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
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

    public long update() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
            myValues.put("carName", this.carName);
            myValues.put("country", this.country);
            myValues.put("motorCapacity", this.motorCapacity);
            myValues.put("hoursePower", this.hoursePower);
            myValues.put("bagSpace", this.bagSpace);
            myValues.put("carImage", this.img);
            myValues.put("brandId", this.brandId);
            return databaseWriteable.update("cars", myValues, "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("cars", "update", e);
        }
        return 0;
    }

    public long remove() {
        try {
            util.getInstance().removeFile(img);
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("cars", "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("cars", "remove", e);
        }
        return 0;
    }

    public ArrayList<CarColor> getCarColors() {
        ArrayList<CarColor> data = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT Car_Colors.id,Car_Colors.carId,Car_Colors.colorId,colors.color FROM Car_Colors JOIN colors ON Car_Colors.carId=" + this.id + " AND Car_Colors.colorId=colors.id", null);
            while (res.moveToNext()) {
                data.add(new CarColor(
                        res.getInt(res.getColumnIndex("id")),
                        res.getInt(res.getColumnIndex("carId")),
                        res.getInt(res.getColumnIndex("colorId")),
                        res.getString(res.getColumnIndex("color"))));
            }

        } catch (Exception e) {
            Log.i(util.class.getName(), "getCarColors", e);
        }
        return data;
    }


}
