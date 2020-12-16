package com.example.carsmodels.dataModel;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.MainActivity;

public class CarImage   {

    private int id;
    private int relationId;
    private byte[] img;

    public CarImage(){

    }

    public CarImage(int id, int relationId, byte[] img) {
        this.id = id;
        this.relationId = relationId;
        this.img = img;
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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public long remove() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("carImages", "id=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("CarImage", "remove", e);
        }
        return 0;
    }
}
