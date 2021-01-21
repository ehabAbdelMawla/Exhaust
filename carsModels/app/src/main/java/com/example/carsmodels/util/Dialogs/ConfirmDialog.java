package com.example.carsmodels.util.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ConfirmDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {

    public ConfirmDialog(Context context, String title, int iconId) {
        super(context);
        setTitle(title);
        setIcon(iconId);
        setPositiveButton(android.R.string.yes, this);
        setNegativeButton(android.R.string.no, null);
    }

    public ConfirmDialog(Context context, int title, int iconId) {
        super(context);
        setTitle(title);
        setIcon(iconId);
        setPositiveButton(android.R.string.yes, this);
        setNegativeButton(android.R.string.no, null);
    }

    public ConfirmDialog(Context context, int title, int msg, int iconId) {
        super(context);
        setTitle(title);
        setMessage(msg);
        setIcon(iconId);
        setPositiveButton(android.R.string.yes, this);
        setNegativeButton(android.R.string.no, null);
    }

    @Override
    abstract public void onClick(DialogInterface dialog, int which);
}
