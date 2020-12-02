package com.example.carsmodels.Colors;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.BrandCars.CarsCategories;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Color;
import com.example.carsmodels.util.util;
import com.flask.colorpicker.ColorPickerView;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class addNewColorOrSelectPrevOneDialogFragment extends DialogFragment {
    ColorPickerView colorPickerView;
    Button addButton;
    int carId;
    //    default Constructor
    public addNewColorOrSelectPrevOneDialogFragment() {
    }
    public addNewColorOrSelectPrevOneDialogFragment( int carId) {
        this.carId=carId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_new_color_or_select_prev_one, container, false);
        loadColors(rootView);
        colorPickerView= rootView.findViewById(R.id.color_picker_view);
        addButton=rootView.findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Color Color= new Color(Integer.toHexString(colorPickerView.getSelectedColor()));
                Color.insert();
                long result= util.getInstance().addRelation(carId,Color.getColorId());
                try {
                    if (result > 0) {
                        Toast.makeText(getActivity(), "Color Added Successfully", Toast.LENGTH_SHORT).show();
                    } else if (result == -1) {
                        Toast.makeText(getActivity(), "Color Already Exist!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                    }
                    if(getDialog()!=null){
                        getDialog().dismiss();
                    }
                    ((CarsCategories)getActivity()).loadCarColors();
                } catch (Exception e) {
                    Log.i("ColorsSettings", "ColorPickerDialogBuilder -> setPositiveButton -> onClick", e);
                }
            }
        });
        return rootView;
    }

    private void loadColors(View view) {
        FlexboxLayout colorsContainer = view.findViewById(R.id.prevColorsContainer);
        colorsContainer.removeAllViews();
        ArrayList<Color> colors = Color.getAllColors();

        for (int i = 0; i < colors.size(); i++) {
            final View colorLayOut = View.inflate(view.getContext(), R.layout.color_item, null);
            final Color color = colors.get(i);
            colorLayOut.findViewById(R.id.view).setBackgroundColor(android.graphics.Color.parseColor("#" + color.getColorHexCode()));
            colorLayOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long result= util.getInstance().addRelation(carId,color.getColorId());
                    try {
                        if (result > 0) {
                            Toast.makeText(getActivity(), "Color Added Successfully", Toast.LENGTH_SHORT).show();
                        } else if (result == -1) {
                            Toast.makeText(getActivity(), "Color Already Exist!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                        }
                        if(getDialog()!=null){
                            getDialog().dismiss();
                        }
                        ((CarsCategories)getActivity()).loadCarColors();
                    } catch (Exception e) {
                        Log.i("ColorsSettings", "ColorPickerDialogBuilder -> setPositiveButton -> onClick", e);
                    }
                }
            });
            colorsContainer.addView(colorLayOut);
        }
    }

}
