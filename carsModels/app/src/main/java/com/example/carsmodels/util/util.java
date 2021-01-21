package com.example.carsmodels.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.carsmodels.Main.MainActivity.db;

public class util {
    /**
     * Singleton Design Pattern
     */
    private static util util;

    public static util getInstance() {
        if (util == null) {
            util = new util();
        }
        return util;
    }

    /**
     * UI Util
     */
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

    public void setTextViewValue(TextView text, String val) {
        text.setText(val);
    }

    public void GlideProps(RequestManager request, String imagePath, ImageView parent) {
        request.load(imagePath)
                .placeholder(R.drawable.loader_gif)
                .fitCenter()
                .error(R.drawable.image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(parent);
    }

    public void setGlideImage(Context context, String imagePath, ImageView parent) {
        GlideProps(Glide.with(context), imagePath, parent);
    }

    public void setGlideImage(Fragment frag, String imagePath, ImageView parent) {
        GlideProps(Glide.with(frag), imagePath, parent);
    }


    public void setGlideImage(View view, String imagePath, ImageView parent) {
        GlideProps(Glide.with(view), imagePath, parent);
    }


    public ProgressDialog createProgressDialog(Context context, String message, int initProgressValue, int MaxProgressValue) {
        ProgressDialog progressBar = new ProgressDialog(context);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setCancelable(false);
        progressBar.setMessage(message);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(initProgressValue);
        progressBar.setMax(MaxProgressValue);
        return progressBar;
    }




    public void shareIntent(Context context, ArrayList<Uri> imagesUri, boolean whatsAppOnly) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        if (whatsAppOnly) {
            whatsappIntent.setPackage("com.whatsapp"); //to share With Whatsapp only
        }
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imagesUri);
        whatsappIntent.setType("image/*");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(whatsappIntent, "share With..."));
    }

    /**
     * DB
     */
    public int getMaximum(String fName, String tabelName) {
        try {
            Cursor res = MainActivity.db.getReadableDatabase().rawQuery(String.format("SELECT MAX(%s) as id FROM %s", fName, tabelName), null);
            while (res.moveToNext()) {
                return res.getInt(res.getColumnIndex("id")) + 1;
            }
        } catch (Exception e) {
            Log.i("util", "Exception in getMaximum()", e);
        }
        return 1;
    }

    public void deleteImagesWithIds(String ids) {
        try {
            MainActivity.db.getWritableDatabase().execSQL(
                    "DELETE FROM carImages WHERE id IN(" + ids + ")");
        } catch (Exception e) {
            Log.i("CarColorImages", "deleteImagesWithIds", e);
        }
    }


    public void removeNonUsedColors() {
        try {
            MainActivity.db.getWritableDatabase().execSQL("DELETE  FROM colors WHERE colors.id  NOT IN (SELECT colors.id from colors  JOIN Car_Colors ON colors.id=Car_Colors.colorId)");

        } catch (Exception e) {
            Log.i(util.class.getName(), "removeNonUsedColors", e);
        }
    }


    /**
     * Files
     */
    public String saveToInternalStorage(Context context, final Bitmap bitmapImage, String folderName, String imageName) {
        /**
         *         ==>   Internal Dirs
         *         ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
         *         // path to /data/data/yourapp/app_data/imageDir
         *         File directory = cw.getDir(folderName, Context.MODE_PRIVATE);
         *         // Create imageDir
         *         File mypath = new File(directory, imageName);
         */

//        External Dirs
        final File mypath = new File(context.getExternalFilesDir("DIRECTORY_PICTURES"), imageName);
        final FileOutputStream[] fos = new FileOutputStream[1];
        Thread copyImage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fos[0] = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos[0].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        copyImage.start();
        try {
            copyImage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }


    public void removeFiles(final ArrayList<String> imagesPathsToDelete) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String path : imagesPathsToDelete) {
                    try {
                        new File(path).delete();
                    } catch (Exception e) {
                        Log.e("util", "Exception in removeFiles()", e);
                    }
                }
            }
        }).start();
    }

    public void removeFile(final String img) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new File(img).delete();
                } catch (Exception e) {
                    Log.e("util", "Exception in removeFile()", e);
                }
            }
        }).start();
    }

    public void removeImageRelationfiles(final Cursor res) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (res.moveToNext()) {
                    try {
                        new File(res.getString(res.getColumnIndex("img"))).delete();
                    } catch (Exception e) {
                        Log.i(util.class.getName(), "getImagesOfId", e);
                    }
                }
            }
        }).start();
    }

    public void removeResourseFiles(String sqlStatement) {
        removeImageRelationfiles(db.getReadableDatabase().rawQuery(sqlStatement, null));
    }

    /**
     * Help Methods
     */
    public Map<Integer, View> getAllSystemSpecification(Context context) {
        Map<Integer, View> allSystemSpecification = new HashMap<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM specifications", null);
            while (res.moveToNext()) {
                View rootView = View.inflate(context, R.layout.specification_radio_btn_item, null);
                ((Switch) rootView.findViewById(R.id.switchItem)).setText(res.getString(res.getColumnIndex("name")));
                setGlideImage(context, res.getString(res.getColumnIndex("img")), (ImageView) rootView.findViewById(R.id.specificationImage));
                allSystemSpecification.put(res.getInt(res.getColumnIndex("id")), rootView);
            }
        } catch (Exception e) {
            Log.i("CarCategoty", "getAllSystemSpecification", e);
        }
        return allSystemSpecification;
    }

    public Map<Integer, Boolean> getSpecificationsIdsOf(int id) {
        Map<Integer, Boolean> ids = new HashMap<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT specificationId FROM car_specifications WHERE categoryId=" + id, null);
            while (res.moveToNext()) {
                ids.put(res.getInt(res.getColumnIndex("specificationId")), true);
            }
        } catch (Exception e) {
            Log.i("carCategories", "getSpecificationsIdsOf", e);
        }
        return ids;
    }

    public Map<Integer, String> getAllSystemSpecificationImages() {
        Map<Integer, String> allSystemSpecification = new HashMap<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT id,img FROM specifications", null);
            while (res.moveToNext()) {

                allSystemSpecification.put(res.getInt(res.getColumnIndex("id")), res.getString(res.getColumnIndex("img")));
            }
        } catch (Exception e) {
            Log.i("carCategories", "getAllSystemSpecification", e);
        }
        return allSystemSpecification;
    }


}
