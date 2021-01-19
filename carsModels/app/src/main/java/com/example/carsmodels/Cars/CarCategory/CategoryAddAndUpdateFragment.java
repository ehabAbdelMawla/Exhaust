package com.example.carsmodels.Cars.CarCategory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.Cars.CarsDetails;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.CarCategoty;
import com.example.carsmodels.util.AnimatedFragment;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;

import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class CategoryAddAndUpdateFragment extends AnimatedFragment {
    /**
     * Declare Instance Variables
     */
    private customeCategoryView existCateg;     // Edit Notation
    private Map<Integer, Boolean> selectedIds;     // Edit Notation

    /**
     * Defualt Constructor Used To Add Category
     */
    public CategoryAddAndUpdateFragment() {
    }

    /**
     * Constructor Used For Update Category
     */
    public CategoryAddAndUpdateFragment(customeCategoryView Categ, Map<Integer, Boolean> selectedIds) {
        this.existCateg = Categ;
        this.selectedIds = selectedIds;
    }

    /**
     * Layout Creation
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_category, container, false);
        final Button AddButton = rootView.findViewById(R.id.addButton);
        final EditText CategoryName = rootView.findViewById(R.id.categName);
        FlexboxLayout specificationsContainer = rootView.findViewById(R.id.specificationsContainer);

        final Map<Integer, View> allSystemSpecification = util.getInstance().getAllSystemSpecification(rootView.getContext());

        /**
         * Check Edit Mode and set Init Data
         */
        final boolean editMode = existCateg != null;
        if (editMode) {
            CategoryName.setText(existCateg.getCategory().getCategName());
            AddButton.setText("Edit");

        }
        /**
         *  Load Specifications
         */
        for (int id : allSystemSpecification.keySet()) {
            if (existCateg != null && selectedIds.get(id) != null) {
                ((Switch) allSystemSpecification.get(id).findViewById(R.id.switchItem)).setChecked(true);
            }
            specificationsContainer.addView(allSystemSpecification.get(id));
        }

        /**
         *  Set Action
         */
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (util.getInstance().getVal(CategoryName).equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Category Name is Required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                CarCategoty newCateg = new CarCategoty(util.getInstance().getMaximum("id", "carsCategory"), util.getInstance().getVal(CategoryName), CarsDetails.getCurrentCar().getId());

                if (editMode) {
                    newCateg.setId(existCateg.getCategory().getId());
                }
                long operationResult = editMode ? newCateg.update() : newCateg.insert();

                for (int specId : allSystemSpecification.keySet()) {
                    if (((Switch) allSystemSpecification.get(specId).findViewById(R.id.switchItem)).isChecked()) {
                        CarCategoty.addCategoryandSpecificationRelation(newCateg.getId(), specId);
                    } else if (editMode) {
                        CarCategoty.removeSpecificationAndCategoryRelation(newCateg.getId(), specId);
                    }
                }
                System.out.println("operationResult : "+operationResult);
                if (operationResult > 0) {
                    Toast.makeText(getActivity(), editMode ? "Category Updated Successfully" : "Category Added Successfully", Toast.LENGTH_SHORT).show();
                    if (getDialog() != null) {
                        getDialog().dismiss();
                        FlexboxLayout parent=(FlexboxLayout) existCateg.getParent();
                        parent.removeView(existCateg);
                        parent.addView(new customeCategoryView((AppCompatActivity)getActivity(),newCateg));
                    } else {
                        Intent newCategIntent=new Intent();
                        newCategIntent.putExtra("newObj",newCateg);
                        getActivity().setResult(RESULT_OK,newCategIntent);
                        getActivity().finish();
                    }
                } else if (operationResult == -1) {
                    Toast.makeText(getActivity(), "Category Already Exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }


}

