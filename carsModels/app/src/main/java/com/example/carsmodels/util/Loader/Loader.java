package com.example.carsmodels.util.Loader;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.R;

public class Loader {

    private AlertDialog dialog;

    public Loader(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View loader = View.inflate(context,R.layout.loader, null);
        builder.setView(loader);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }


    public void displayLoader() {
        dialog.show();
    }

    public void dismissLoader() {
        dialog.dismiss();
    }


}
