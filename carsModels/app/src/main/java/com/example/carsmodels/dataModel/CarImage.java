package com.example.carsmodels.dataModel;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.carsmodels.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CarImage   {

    private int id;
    private int relationId;
    private String  imgPath;


    public CarImage(){

    }

    public CarImage(int id, int relationId,String imgPath ) {
        this.id = id;
        this.relationId = relationId;
        this.imgPath = imgPath;
    }

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

    public Bitmap getImageBitMap(){
        try {
            return BitmapFactory.decodeStream(new FileInputStream(new File(this.getImgPath())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Image Not Found");
        }
        return null;
    }



    public long remove() {
        try {
            new File(this.getImgPath()).deleteOnExit();
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("carImages", "id=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("CarImage", "remove", e);
        }
        return 0;
    }
}
