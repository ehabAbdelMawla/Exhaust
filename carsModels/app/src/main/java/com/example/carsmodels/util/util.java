package com.example.carsmodels.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;

import com.example.carsmodels.BrandCars.CarsCategories;
import com.example.carsmodels.MainActivity;
import com.example.carsmodels.dataModel.CarCategoty;
import com.example.carsmodels.dataModel.CarColor;
import com.example.carsmodels.dataModel.CarImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.example.carsmodels.MainActivity.db;

public class util {

    private static util util;


    public static util getInstance() {
        if (util == null) {
            util = new util();
        }
        return util;
    }

    //    get Value of editable Text
    public String getVal(EditText textInput) {
        try {
            if (textInput != null) {
                return textInput.getText().toString().trim();
            }
        } catch (Exception e) {
            Log.i("util", "Exception in getVal()", e);
        }
        return "";
    }

    public long addRelation(int carId,int colorId){
        try {
            SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
            ContentValues myValues = new ContentValues();
            myValues.put("id",getMaximum("id","Car_Colors"));
            myValues.put("carId",carId);
            myValues.put("colorId", colorId);
            return databaseWriteable.insert("Car_Colors", null, myValues);
        } catch (Exception e) {
            Log.i(util.class.getName(), "addRelation", e);
        }
      return 0;
    }

    public ArrayList<CarColor> getCarColors(int carId) {
        ArrayList<CarColor> data=new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT Car_Colors.id,Car_Colors.carId,Car_Colors.colorId,colors.color FROM Car_Colors JOIN colors ON Car_Colors.carId=" + carId+" AND Car_Colors.colorId=colors.id", null);
            while (res.moveToNext()) {
                data.add(new CarColor(
                        res.getInt(res.getColumnIndex("id")),
                        res.getInt(res.getColumnIndex("carId")),
                        res.getInt(res.getColumnIndex("colorId")),
                        res.getString(res.getColumnIndex("color"))));
            }

        } catch (Exception e) {
            Log.i(util.class.getName(), "getCarColors", e);
        }
        return data;
    }


//    Add Image & Color Relation
public long addColorImage(int relationId,byte[] img){
    try {
        SQLiteDatabase databaseWriteable = MainActivity.db.getWritableDatabase();
        ContentValues myValues = new ContentValues();
        myValues.put("id",getMaximum("id","carImages"));
        myValues.put("relationId",relationId);
        myValues.put("img",img);
        return databaseWriteable.insert("carImages", null, myValues);
    } catch (Exception e) {
        Log.i(util.class.getName(), "addColorImage", e);
    }
    return 0;
}
//get All Relation Images
    public ArrayList<CarImage> getCarImages(int relationId) {
        ArrayList<CarImage> data=new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM carImages WHERE relationId="+relationId, null);
            while (res.moveToNext()) {
                data.add(new CarImage(
                        res.getInt(res.getColumnIndex("id")),
                        res.getInt(res.getColumnIndex("relationId")),
                        res.getBlob(res.getColumnIndex("img")
                     )));
            }

        } catch (Exception e) {
            Log.i(util.class.getName(), "getCarColors", e);
        }
        return data;
    }



//    convert bitmap to byte array and make images compression and change scale
    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        //                Compress Image
//        bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.25), (int)(bitmap.getHeight()*0.25), false);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        return outputStream.toByteArray();
    }

//    get Maximum of specific number column in specific table in DB
    public int getMaximum(String fName,String tabelName){
        try {
            Cursor res = MainActivity.db.getReadableDatabase().rawQuery(String.format("SELECT MAX(%s) as id FROM %s",fName,tabelName), null);
            while (res.moveToNext()){
                return res.getInt(res.getColumnIndex("id"))+1;
            }
        } catch (Exception e) {
            Log.i("util", "Exception in getMaximum()", e);
        }
        return 1;
    }

}
