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

public class Specification implements Serializable {
    /**
     * Instance Attributes
     */
    private int id;
    private String name;
    private String img;

    /**
     * Constructors
     */
    public Specification(int id, String name, String img) {
        this.id = id;
        this.name = name;
        this.img = img;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    /**
     * DataBase Section
     *
     * @return result of DB Action
     */
    public long insert() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id", this.id);
            myValues.put("name", this.name);
            myValues.put("img", this.img);
            return databaseWriteable.insert("specifications", null, myValues);
        } catch (Exception e) {
            Log.i("specifications", "insert", e);
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
            return databaseWriteable.update("specifications", myValues, "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("specifications", "update", e);
        }
        return 0;
    }

    public long remove() {
        try {
            util.getInstance().removeFile(img);
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("specifications", "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.i("specifications", "remove", e);
        }
        return 0;
    }

    public static ArrayList<Specification> getAllspecifications() {
        ArrayList<Specification> specifications = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM specifications", null);
            while (res.moveToNext()) {
                specifications.add(new Specification(res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("name")),
                        res.getString(res.getColumnIndex("img"))));
            }

        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "getAll specifications", e);
        }
        return specifications;
    }

}
