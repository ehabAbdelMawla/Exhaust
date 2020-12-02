package com.example.carsmodels.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context) {
        super(context, "data.db", null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

//       Brands Table
        db.execSQL("CREATE TABLE IF NOT EXISTS brands (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "brandName VARCHAR(225) UNIQUE NOT NULL," +
                "brandAgent VARCHAR(225) NOT NULL," +
                "brandImage BLOB )");
//       Cars Table
        db.execSQL("CREATE TABLE IF NOT EXISTS cars (" +
                "id INTEGER UNIQUE NOT NULL," +
                "carName VARCHAR(225)  NOT NULL," +
                "country VARCHAR(225) NOT NULL," +
                "motorCapacity DOUBLE NOT NULL," +
                "hoursePower DOUBLE NOT NULL," +
                "bagSpace DOUBLE NOT NULL," +
                "carImage BLOB ," +
                "brandId INTEGER NOT NULL," +
                "  PRIMARY KEY(carName,brandId)," +
                " FOREIGN KEY(brandId) REFERENCES brands(id) ON DELETE CASCADE ON UPDATE CASCADE)");
//  Cars Categories
        db.execSQL("CREATE TABLE IF NOT EXISTS carsCategory (" +
                "id INTEGER UNIQUE NOT NULL," +
                "categoryName VARCHAR(225)  NOT NULL," +
                "carId INTEGER NOT NULL," +
                "  PRIMARY KEY(categoryName,carId)," +
                " FOREIGN KEY(carId) REFERENCES cars(id) ON DELETE CASCADE ON UPDATE CASCADE)");


        //  System Specification
        db.execSQL("CREATE TABLE IF NOT EXISTS specifications (" +
                "id INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(225) UNIQUE NOT NULL,img BLOB UNIQUE NOT NULL)");

        //  All Colors
        db.execSQL("CREATE TABLE IF NOT EXISTS colors (" +
                "id INTEGER  PRIMARY KEY NOT NULL," +
                "color VARCHAR(225) UNIQUE NOT NULL)");

        //  Car_Colors
        db.execSQL("CREATE TABLE IF NOT EXISTS Car_Colors (" +
                "id INTEGER  UNIQUE NOT NULL," +
                "carId INTEGER  NOT NULL," +
                "colorId INTEGER  NOT NULL," +
                "PRIMARY KEY(carId,colorId)," +
                "FOREIGN KEY(carId) REFERENCES cars(id) ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY(colorId) REFERENCES colors(id) ON DELETE CASCADE ON UPDATE CASCADE)");
//      Cars Color Images
        db.execSQL("CREATE TABLE IF NOT EXISTS carImages (" +
                "id INTEGER UNIQUE NOT NULL," +
                "relationId INTEGER NOT NULL," +
                "img BLOB NOT NULL," +
                "PRIMARY KEY(relationId,img)," +
                "FOREIGN KEY(relationId) REFERENCES Car_Colors(id) ON DELETE CASCADE ON UPDATE CASCADE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS brands");
        db.execSQL("DROP TABLE IF EXISTS cars");
        db.execSQL("DROP TABLE IF EXISTS carsCategory");
        db.execSQL("DROP TABLE IF EXISTS specifications");
        db.execSQL("DROP TABLE IF EXISTS colors");
        db.execSQL("DROP TABLE IF EXISTS Car_Colors");
        db.execSQL("DROP TABLE IF EXISTS CarImages");
        onCreate(db);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //     Open FOREIGN Support
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}
