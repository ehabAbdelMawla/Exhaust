package com.example.carsmodels.DataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.util.util;

import java.io.Serializable;

import static com.example.carsmodels.Main.MainActivity.db;

public class CarCategoty implements Serializable {
    /**
     * Instance Attributes
     */
    private int id;
    private String categName;
    private int carId;

    /**
     * Constructors
     */
    public CarCategoty(int id, String categName, int carId) {
        this.id = id;
        this.categName = categName;
        this.carId = carId;
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

    /**
     * DataBase Section
     *
     * @return result of DB Action
     */
    public long insert() {
        try {
            SQLiteDatabase databaseWriteable = db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
            myValues.put("categoryName", this.categName);
            myValues.put("carId", this.carId);
            return databaseWriteable.insert("carsCategory", null, myValues);
        } catch (Exception e) {
            Log.i("CarCategoty", "insert", e);
        }
        return 0;
    }


    public long update() {
        try {
            SQLiteDatabase databaseWriteable = db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
//            myValues.put("id", this.id);
            myValues.put("categoryName", this.categName);
            myValues.put("carId", this.carId);
            return databaseWriteable.update("carsCategory", myValues, "id=?", new String[]{String.valueOf(this.id)});
        } catch (Exception e) {
            Log.i("CarCategoty", "insert", e);
        }
        return 0;
    }

    public long remove() {
        try {
            return MainActivity.db.getWritableDatabase().delete("carsCategory", "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i(util.class.getName(), "remove", e);
        }
        return 0;
    }

    public static void addCategoryandSpecificationRelation(int id, int specId) {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", util.getInstance().getMaximum("id", "car_specifications"));
            myValues.put("categoryId", id);
            myValues.put("specificationId", specId);
            databaseWriteable.insert("car_specifications", null, myValues);
        } catch (Exception e) {
            Log.i(util.class.getName(), "addCategoryandSpecificationRelation", e);
        }
    }


    public static void removeSpecificationAndCategoryRelation(int id, int specId) {
        try {
            MainActivity.db.getWritableDatabase().delete("car_specifications", "categoryId=? AND specificationId=?", new String[]{String.valueOf(id), String.valueOf(specId)});
        } catch (Exception e) {
            Log.i(util.class.getName(), "removeSpecificationAndCategoryRelation", e);
        }
    }

}
