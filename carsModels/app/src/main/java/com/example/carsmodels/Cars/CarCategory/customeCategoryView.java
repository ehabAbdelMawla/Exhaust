package com.example.carsmodels.Cars.CarCategory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.carsmodels.Cars.CarsDetails;
import com.example.carsmodels.DataModel.CarCategoty;
import com.example.carsmodels.R;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Dialogs.EditOrDeleteDialog;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;

import java.util.Map;

public class customeCategoryView extends ConstraintLayout implements View.OnLongClickListener {

    private CarCategoty category;
    private AppCompatActivity parentActivity;
    private Map<Integer, Boolean> sepcIds;

    public customeCategoryView(AppCompatActivity activity, CarCategoty category) {
        super(activity);
        this.parentActivity = activity;
        this.category = category;
        View.inflate(activity, R.layout.car_category_view, this);
        ((TextView) this.findViewById(R.id.carCategoryName)).setText(category.getCategName());
        this.sepcIds = util.getInstance().getSpecificationsIdsOf(category.getId());
        FlexboxLayout sepecificationImagesContainer = this.findViewById(R.id.specificationImages);

        for (int specificationId : sepcIds.keySet()) {
            View imageViewConatiner = View.inflate(getContext(), R.layout.small_image, null);
            util.getInstance().setGlideImage(this, CarsDetails.specificationImages.get(specificationId), (ImageView) imageViewConatiner.findViewById(R.id.image));
            sepecificationImagesContainer.addView(imageViewConatiner);
        }

        setOnLongClickListener(this);

    }

    public CarCategoty getCategory() {
        return category;
    }

    public void setCategory(CarCategoty category) {
        this.category = category;
    }

    public AppCompatActivity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(AppCompatActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public Map<Integer, Boolean> getSepcIds() {
        return sepcIds;
    }

    public void setSepcIds(Map<Integer, Boolean> sepcIds) {
        this.sepcIds = sepcIds;
    }

    @Override
    public boolean onLongClick(View v) {
        new EditOrDeleteDialog(getContext()) {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.editIcon:
                        this.cancel();
                        new CategoryAddAndUpdateFragment(customeCategoryView.this, sepcIds).show(parentActivity.getSupportFragmentManager(), "edit_carCategory");
                        break;
                    case R.id.deleteIcon:
                        this.cancel();
                        new ConfirmDialog(parentActivity, "Delete Category?", android.R.drawable.ic_delete) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long result = category.remove();
                                if (result == 1) {
                                    Toast.makeText(parentActivity, "Category Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    ((FlexboxLayout) customeCategoryView.this.getParent()).removeView(customeCategoryView.this);
                                } else {
                                    Toast.makeText(parentActivity, "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.show();
                        break;
                }
            }
        }.show();
        return true;
    }
}
