package com.example.carsmodels.dataModel;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.carsmodels.Colors.ColorsSettings;
import com.example.carsmodels.MainActivity;
import com.example.carsmodels.util.util;

import java.util.ArrayList;

public class Color {
    protected int colorId;
    protected String colorHexCode;

    //    Creation Constructor
    public Color(String colorHexCode) {
        this.colorHexCode = colorHexCode;
    }

    //    Retreive Constructor
    public Color(int colorId, String colorHexCode) {
        this.colorId = colorId;
        this.colorHexCode = colorHexCode;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
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
            setColorId(util.getInstance().getMaximum("id","colors"));
            myValues.put("id", this.colorId);
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
            myValues.put("id", this.colorId);
            myValues.put("color", this.colorHexCode);
            return databaseWriteable.update("colors",myValues,"id=?",new String[]{String.valueOf(colorId)} );
        } catch (Exception e) {
            Log.i("Color", "update", e);
        }
        return 0;
    }

    public long remove() {
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            return databaseWriteable.delete("colors", "id=?",new String[]{String.valueOf(colorId)});
        } catch (Exception e) {
            Log.i("Color", "remove", e);
        }
        return 0;
    }

//    get All Colors
public static ArrayList<Color> getAllColors() {
    ArrayList<Color> colors = new ArrayList<>();
    try {
        Cursor res = MainActivity.db.getReadableDatabase().rawQuery("SELECT * FROM colors", null);
        while (res.moveToNext()) {
            colors.add(new Color(res.getInt(res.getColumnIndex("id")),
                    res.getString(res.getColumnIndex("color"))));
        }
    } catch (Exception e) {
        Log.i(ColorsSettings.class.getName(), "getAllColors", e);
    }
    return colors;
}


}
