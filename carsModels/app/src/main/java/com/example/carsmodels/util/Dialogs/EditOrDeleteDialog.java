package com.example.carsmodels.util.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.R;

public abstract class EditOrDeleteDialog extends AlertDialog implements View.OnClickListener {

    public EditOrDeleteDialog(Context context) {
        super(context);
        View popUpView = View.inflate(context, R.layout.edit_delete_popup, null);
        setView(popUpView);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));

        YoYo.with(Techniques.ZoomIn)
                .duration(250).playOn(popUpView.findViewById(R.id.editIcon));
        YoYo.with(Techniques.ZoomIn)
                .duration(250).playOn(popUpView.findViewById(R.id.deleteIcon));

        popUpView.findViewById(R.id.editIcon).setOnClickListener(this);
        popUpView.findViewById(R.id.deleteIcon).setOnClickListener(this);
    }


    @Override
    abstract public void onClick(View v);
}
