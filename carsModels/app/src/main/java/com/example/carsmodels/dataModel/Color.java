package com.example.carsmodels.dataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.MainActivity;

public class Color {
    private int id;
    private String colorHexCode;

    //    Creation Constructor
    public Color(String colorHexCode) {
        this.colorHexCode = colorHexCode;
    }

    //    Retreive Constructor
    public Color(int id, String colorHexCode) {
        this.id = id;
        this.colorHexCode = colorHexCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColorHexCode() {
        return colorHexCode;
    }

    public void setColorHexCode(String colorHexCode) {
        this.colorHexCode = colorHexCode;
    }

    // Data base Method
    public long insert() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("color", this.colorHexCode);
            return databaseWriteable.insert("colors", null, myValues);
        } catch (Exception e) {
            Log.i("Color", "insert", e);
        }
        return 0;
    }

    public long update() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
            myValues.put("color", this.colorHexCode);
            return databaseWriteable.update("colors",myValues,"id=?",new String[]{String.valueOf(id)} );
        } catch (Exception e) {
            Log.i("Color", "update", e);
        }
        return 0;
    }

    public long remove() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("colors", "id=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("Color", "remove", e);
        }
        return 0;
    }
}
