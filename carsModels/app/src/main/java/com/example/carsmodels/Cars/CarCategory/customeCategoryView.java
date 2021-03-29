package com.example.carsmodels.Cars.CarCategory;


import android.content.DialogInterface;
import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.example.carsmodels.Cars.CarsDetails;
import com.example.carsmodels.DataModel.CarCategoty;
import com.example.carsmodels.R;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Dialogs.EditOrDeleteDialog;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import java.util.Map;

public class customeCategoryView extends ConstraintLayout implements View.OnLongClickListener {

    private CarCategoty category;
    private AppCompatActivity parentActivity;
    private Map<Integer, String> sepcIds;

    public customeCategoryView(AppCompatActivity activity, CarCategoty category) {
        super(activity);
        this.parentActivity = activity;
        this.category = category;
        View.inflate(activity, R.layout.car_category_view, this);
        ((TextView) this.findViewById(R.id.carCategoryName)).setText(category.getCategName());
        this.sepcIds = util.getInstance().getSpecificationsIdsOf(category.getId());

//        FlexboxLayout sepecificationImagesContainer = this.findViewById(R.id.specificationImages);
        GridLayout sepecificationImagesContainer = this.findViewById(R.id.specificationImages);

        /**
         * Change Number of Grid Column when oriantation change
         */

        if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            sepecificationImagesContainer.setColumnCount(2);
        }
        else{
            sepecificationImagesContainer.setColumnCount(3);
        }

        for (int specificationId : sepcIds.keySet()) {

            View imageViewConatiner = View.inflate(getContext(), R.layout.small_image, null);
            if(!CarsDetails.specificationImages.get(specificationId).trim().equals("")){
                util.getInstance().setGlideImage(this, CarsDetails.specificationImages.get(specificationId), (ImageView) imageViewConatiner.findViewById(R.id.image));
            }
            ((TextView)imageViewConatiner.findViewById(R.id.specificationName)).setText(sepcIds.get(specificationId));
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

    public Map<Integer, String> getSepcIds() {
        return sepcIds;
    }

    public void setSepcIds(Map<Integer, String> sepcIds) {
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
                        new ConfirmDialog(parentActivity, R.string.delete_category_dialog_title, android.R.drawable.ic_delete) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long result = category.remove();
                                if (result == 1) {
                                    Toast.makeText(parentActivity, R.string.delete_category_success_msg, Toast.LENGTH_SHORT).show();
                                    FlexboxLayout parent = ((FlexboxLayout) customeCategoryView.this.getParent());
                                    boolean isLastOne = parent.getChildCount() == 1;
                                    ((AnimatedActivity) parentActivity).removeViewWithAnimate(parent, customeCategoryView.this, Techniques.ZoomOutDown, 350, R.string.cars_categories_empty_msg);
                                    if (isLastOne) {
                                        parent.setAlignItems(AlignItems.CENTER);
                                    }

                                } else {
                                    Toast.makeText(parentActivity, R.string.uncatched_error, Toast.LENGTH_SHORT).show();
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
