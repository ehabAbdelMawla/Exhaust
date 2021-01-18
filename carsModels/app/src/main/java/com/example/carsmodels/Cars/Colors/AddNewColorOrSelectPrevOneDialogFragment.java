package com.example.carsmodels.Cars.Colors;

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

import com.example.carsmodels.Cars.CarsDetails;
import com.example.carsmodels.DataModel.CarColor;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Color;
import com.example.carsmodels.util.util;
import com.flask.colorpicker.ColorPickerView;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Objects;

public class AddNewColorOrSelectPrevOneDialogFragment extends DialogFragment {

    /**
     * Variables Declaration
     */
    private CustomeColorView customeColorView = null;
    private ColorPickerView colorPickerView;
    private Button addButton;
    private int carId;
    private int existReplationId;
    private int colorId;

    /**
     * Default Constructor
     */
    public AddNewColorOrSelectPrevOneDialogFragment() {
    }

    /**
     * Constructor For Add New Color
     */
    public AddNewColorOrSelectPrevOneDialogFragment(int carId) {
        this.carId = carId;
    }

    /**
     * Constructor For Update Exists Color
     */
    public AddNewColorOrSelectPrevOneDialogFragment(int carId, int existReplationId, int colorId, CustomeColorView customeColorView) {
        this.carId = carId;
        this.existReplationId = existReplationId;
        this.colorId = colorId;
        this.customeColorView = customeColorView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_new_color_or_select_prev_one, container, false);
//        ..... load Prev Colors .....
        loadColors(rootView);
//        ..... Map Elements .....
        colorPickerView = rootView.findViewById(R.id.color_picker_view);
        addButton = rootView.findViewById(R.id.addButton);
        final boolean updateMode = existReplationId != 0;
        final int relationId;
        if (updateMode) {
            addButton.setText("Update");
            relationId = existReplationId;
        } else {
            relationId = util.getInstance().getMaximum("id", "Car_Colors");
        }
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Color Color = new Color(Integer.toHexString(colorPickerView.getSelectedColor()));
                Color.insert();
                addAction(updateMode, relationId, Color);
            }
        });
        return rootView;
    }


    private void loadColors(View view) {
        /**
         * Load Prev Usage Colors and Add Click Action For Each
         */
        FlexboxLayout colorsContainer = view.findViewById(R.id.prevColorsContainer);
        colorsContainer.removeAllViews();
        ArrayList<Color> colors = Color.getAllColors();
        final boolean updateMode = existReplationId != 0;

        for (int i = 0; i < colors.size(); i++) {
            final View colorLayOut = View.inflate(view.getContext(), R.layout.color_item, null);
            final Color color = colors.get(i);
            // if update Mode Is On You Not Need To Add Updated Color To List
            if (updateMode && colorId == color.getColorId()) continue;
            try {
                colorLayOut.findViewById(R.id.view).setBackgroundColor(android.graphics.Color.parseColor(color.getColorHexCode()));
                colorLayOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int relationId;
                        if (updateMode) {
                            relationId = existReplationId;
                        } else {
                            relationId = util.getInstance().getMaximum("id", "Car_Colors");
                        }
                        addAction(updateMode, relationId, color);
                    }
                });
                colorsContainer.addView(colorLayOut);
            }catch(Exception e){
                Log.i("colorFragment", "loadColors -> ", e);
            }

        }
    }


    public void addAction(boolean updateMode, int relationId, Color Color) {
        try {
            // ..... DataBase Effect .....
            long result = updateMode ?
                    CarColor.updateColorRelation(existReplationId, Color.getColorId()) :
                    CarColor.addColorRelation(carId, Color.getColorId(), relationId);
            // ..... User FeedBack .....
            showToast(result, updateMode);

            //.... Close Dialog ....
            if (getDialog() != null) {
                getDialog().dismiss();
            }

            if (updateMode) {
                ((FlexboxLayout) customeColorView.getParent()).removeView(customeColorView);
            }
            if (result > 0){
                ((CarsDetails) Objects.requireNonNull(getActivity())).addColor(new CarColor(relationId, carId, Color.getColorId(), Color.getColorHexCode().replace("#", "")));
            }
            
        } catch (Exception e) {
            Log.i("colorFragment", "addAction -> ", e);
        }
    }

    public void showToast(long result, boolean updateMode) {
        if (result > 0) {
            Toast.makeText(getActivity(), updateMode ? "Color Update Successfully" : "Color Added Successfully", Toast.LENGTH_SHORT).show();
        } else if (result == -1) {
            Toast.makeText(getActivity(), "Color Already Exist!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
        }
    }
}
