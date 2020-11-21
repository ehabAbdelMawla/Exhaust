package com.example.carsmodels.dataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.MainActivity;

public class Brand {
    private int id;
    private String brandName;
    private String brandAgent;
    private byte[] img;

    //    Retrive Constructor
    public Brand(int id,String brandName, String brandAgent, byte[] img) {
        this.id=id;
        this.brandName = brandName;
        this.brandAgent = brandAgent;
        this.img = img;
    }

    //    Creation Constructor
    public Brand(String brandName, String brandAgent, byte[] img) {
        this.brandName = brandName;
        this.brandAgent = brandAgent;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandAgent() {
        return brandAgent;
    }

    public void setBrandAgent(String brandAgent) {
        this.brandAgent = brandAgent;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    //    Data Base Handle
    public long insert() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("brandName", this.brandName);
            myValues.put("brandAgent", this.brandAgent);
            myValues.put("brandImage", this.img);
            return databaseWriteable.insert("brands", null, myValues);
        } catch (Exception e) {
            Log.i("Brand", "insert", e);
        }
        return 0;
    }

    public long remove() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("brands", "id=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("Brand", "remove", e);
        }
        return 0;
    }

    public long update() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
            myValues.put("brandName", this.brandName);
            myValues.put("brandAgent", this.brandAgent);
            myValues.put("brandImage", this.img);
            return databaseWriteable.update("brands",myValues,"id=?",new String[]{String.valueOf(id)} );
        } catch (Exception e) {
            Log.i("brands", "update", e);
        }
        return 0;
    }
}
