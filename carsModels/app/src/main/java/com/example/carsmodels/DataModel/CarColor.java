package com.example.carsmodels.DataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.util.util;

public class CarColor extends Color {
    /**
     * Instance Attributes
     */
    private int realtionId;
    private int carId;

    /**
     * Constructors
     */
    public CarColor(int realtionId, int carId, int id, String colorHexCode) {
        super(id, colorHexCode);
        this.realtionId = realtionId;
        this.carId = carId;
    }

    /**
     * Getters & Setters
     */
    public int getRealtionId() {
        return realtionId;
    }

    public void setRealtionId(int realtionId) {
        this.realtionId = realtionId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public static long addColorRelation(int carId, int colorId, int relationId) {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", relationId);
            myValues.put("carId", carId);
            myValues.put("colorId", colorId);
            return databaseWriteable.insert("Car_Colors", null, myValues);
        } catch (Exception e) {
            Log.i(util.class.getName(), "addColorRelation", e);
        }
        return 0;
    }

    public static long updateColorRelation(int relationID, int newColorId) {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("colorId", newColorId);
            return databaseWriteable.update("Car_Colors", myValues, "id=?", new String[]{String.valueOf(relationID)});
        } catch (Exception e) {
            Log.i(util.class.getName(), "updateColorRelation", e);
        }
        return 0;
    }

    public static long removeRelation(int id) {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            util.getInstance().removeResourseFiles("SELECT carImages.img FROM carImages WHERE carImages.relationId=" + id);
            return databaseWriteable.delete("Car_Colors", "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i(util.class.getName(), "removeRelation", e);
        }
        return 0;
    }
}
