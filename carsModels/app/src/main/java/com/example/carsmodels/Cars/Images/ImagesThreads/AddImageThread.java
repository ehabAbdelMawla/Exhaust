package com.example.carsmodels.Cars.Images.ImagesThreads;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.carsmodels.DataModel.CarImage;
import com.example.carsmodels.util.util;

import java.util.Date;

/**
 * This Thread is passed To Thread Pool To Add Multiple Images
 * each Thread Work on Added One Image
 */
public class AddImageThread extends Thread {
    /**
     * Declare Instance Variables
     */
    private Uri uri;
    private int relationId;
    private int id;
    /**
     * Declare Class Variables
     */
    public static int[] operationResults = {0, 0, 0};
    private static int progressValue = 0;
    private static Context context = null;
    private static ProgressDialog progressBar = null;

    /**
     * @param uri:uri        for image to store in DB
     * @param relationId:id  of relation between color and image in DB
     * @param id:incremental Id of record
     */
    public AddImageThread(Uri uri, int relationId, int id) {
        this.uri = uri;
        this.relationId = relationId;
        this.id = id;
    }

    /**
     * Implement Thread Class run Method
     */
    public void run() {
        try {
            long result = CarImage.addCarImageRelation(relationId, util.getInstance().saveToInternalStorage(AddImageThread.context, MediaStore.Images.Media.getBitmap(AddImageThread.context.getContentResolver(), uri), "colorImages", relationId + "_" + new Date().getTime() + ".png"), this.id);
            if (result == -1) {
                operationResults[1] += 1;
            } else if (result < 0) {
                operationResults[2] += 1;
            }
            AddImageThread.progressBar.setProgress(++AddImageThread.progressValue);
        } catch (Exception ex) {
            Log.i(getClass().getName(),"Exception "+ex);
        }
    }

    public static void setInit(Context context, ProgressDialog progressBar) {
        AddImageThread.context = context;
        AddImageThread.progressBar = progressBar;
        AddImageThread.progressValue = 0;
        AddImageThread.operationResults = new int[]{0, 0, 0};
    }

    public static void clear() {
        AddImageThread.context = null;
        AddImageThread.progressBar = null;
    }

}