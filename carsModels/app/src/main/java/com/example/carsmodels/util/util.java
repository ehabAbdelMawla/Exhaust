package com.example.carsmodels.util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;

import com.example.carsmodels.MainActivity;

import java.io.ByteArrayOutputStream;

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

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

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
