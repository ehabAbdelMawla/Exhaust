package com.example.carsmodels.DataModel;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.util.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.carsmodels.Main.MainActivity.db;

public class CarImage {
    /**
     * Instance Attributes
     */
    private int id;
    private int relationId;
    private String imgPath;

    /**
     * Constructors
     */
    public CarImage(int id, int relationId, String imgPath) {
        this.id = id;
        this.relationId = relationId;
        this.imgPath = imgPath;
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

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }


    public String getImgPath() {
        return imgPath;
    }

    public Bitmap getImageBitMap() {
        try {
            return BitmapFactory.decodeStream(new FileInputStream(new File(this.getImgPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DataBase Section
     *
     * @return result of DB Action
     */
    public long remove() {
        try {
            new File(this.getImgPath()).delete();
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("carImages", "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("CarImage", "remove", e);
        }
        return 0;
    }

   synchronized public static long addCarImageRelation(int relationId, String img, int id) {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", id);
            myValues.put("relationId", relationId);
            myValues.put("img", img);
            return databaseWriteable.insert("carImages", null, myValues);
        } catch (Exception e) {
            Log.i(util.class.getName(), "addCarImageRelation", e);
        }
        return 0;
    }


    public static ArrayList<CarImage> getCarImages(String sql) {
        ArrayList<CarImage> data = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery(sql, null);
            while (res.moveToNext()) {
                data.add(new CarImage(
                        res.getInt(res.getColumnIndex("id")),
                        res.getInt(res.getColumnIndex("relationId")),
                        res.getString(res.getColumnIndex("img")
                        )));
            }

        } catch (Exception e) {
            Log.i(util.class.getName(), "getCarColors", e);
        }
        return data;
    }
}

