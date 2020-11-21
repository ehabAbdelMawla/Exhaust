package com.example.carsmodels.dataModel;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.util.util;

public class Specification {
    private int id;
    private String name;
    private byte[] img;

//    Creation
    public Specification(String name, byte[] img) {
        this.name = name;
        this.img = img;
    }
// Selection
    public Specification(int id,String name, byte[] img) {
        this.id=id;
        this.name = name;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }


//     DB Operations
public long insert() {
    try {
        SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
        ContentValues myValues = new ContentValues();
        myValues.put("name", this.name);
        myValues.put("img", this.img);
        return databaseWriteable.insert("specifications", null, myValues);
    } catch (Exception e) {
        Log.i("specifications", "insert", e);
    }
    return 0;
}

    public long remove() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("specifications", "id=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("specifications", "remove", e);
        }
        return 0;
    }

    public long update() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
            myValues.put("name", this.name);
            myValues.put("img", this.img);
            return databaseWriteable.update("specifications",myValues,"id=?",new String[]{String.valueOf(id)} );
        } catch (Exception e) {
            Log.i("specifications", "update", e);
        }
        return 0;
    }
}
