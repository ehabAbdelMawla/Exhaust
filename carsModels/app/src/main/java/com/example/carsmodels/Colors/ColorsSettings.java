package com.example.carsmodels.Colors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Color;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class ColorsSettings extends AppCompatActivity {
    ColorsSettings ColorsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_color_or_select_prev_one);
        ColorsSettings = this;

//        findViewById(R.id.addColorButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ColorPickerDialogBuilder
//                        .with(ColorsSettings)
//                        .setTitle("Choose color")
////                        .initialColor(android.graphics.Color.parseColor("#FFFFFF"))
//                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                        .density(8)
//                        .showColorPreview(true)
//                        .setPositiveButton("ok", new ColorPickerClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                                try {
//                                    long result = new Color(Integer.toHexString(selectedColor)).insert();
//
//                                    if (result > 0) {
//                                        Toast.makeText(getApplicationContext(), "Color Added Successfully", Toast.LENGTH_SHORT).show();
//                                    } else if (result == -1) {
//                                        Toast.makeText(getApplicationContext(), "Color Already Exist!", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
//                                    }
//                                    loadColors();
//                                } catch (Exception e) {
//                                    Log.i("ColorsSettings", "ColorPickerDialogBuilder -> setPositiveButton -> onClick", e);
//                                }
//                            }
//                        })
//                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .build()
//                        .show();
//            }
//        });
        loadColors();

    }

    private void loadColors() {
        FlexboxLayout colorsContainer = findViewById(R.id.brandCarContainer);
        colorsContainer.removeAllViews();
        ArrayList<Color> colors = Color.getAllColors();

        for (int i = 0; i < colors.size(); i++) {
            final View colorLayOut = View.inflate(this, R.layout.color_item, null);
            final Color color = colors.get(i);
            colorLayOut.findViewById(R.id.view).setBackgroundColor(android.graphics.Color.parseColor("#" + color.getColorHexCode()));
            colorLayOut.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alertDialogBuilde = new AlertDialog.Builder(ColorsSettings);
                    View popUpView = getLayoutInflater().inflate(R.layout.edit_delete_popup, null);
                    alertDialogBuilde.setView(popUpView);
                    final AlertDialog alert = alertDialogBuilde.create();
                    alert.show();
                    popUpView.findViewById(R.id.editIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            ColorPickerDialogBuilder
                                    .with(ColorsSettings)
                                    .setTitle("Choose color")
                                    .initialColor(android.graphics.Color.parseColor("#"+color.getColorHexCode()))
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(8)
                                    .showColorPreview(true)
                                    .setPositiveButton("ok", new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            try {
                                                color.setColorHexCode(Integer.toHexString(selectedColor));
                                                long result =color.update();
                                                if (result > 0) {
                                                    Toast.makeText(getApplicationContext(), "Color updated Successfully", Toast.LENGTH_SHORT).show();
                                                } else if (result == 0) {
                                                    Toast.makeText(getApplicationContext(), "Color Already Exist!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                                }
                                                loadColors();
                                            } catch (Exception e) {
                                                Log.i("ColorsSettings", "ColorPickerDialogBuilder -> setPositiveButton -> onClick", e);
                                            }
                                        }
                                    })
                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    popUpView.findViewById(R.id.deleteIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            new AlertDialog.Builder(ColorsSettings)
                                    .setTitle("Delete Color?")
                                    .setMessage("All cars Images In of this color will removed too.")
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            long result = color.remove();
                                            if (result == 1) {
                                                Toast.makeText(getApplicationContext(), "Color Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                loadColors();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();

                        }
                    });

                    return true;
                }
            });
            colorsContainer.addView(colorLayOut);

        }
    }


}


