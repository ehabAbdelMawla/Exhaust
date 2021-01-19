package com.example.carsmodels.Speceficeations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.Brands.BrandDetails;
import com.example.carsmodels.Cars.CarsAddAndUpdateFragment;
import com.example.carsmodels.Cars.Images.CarColorImages;
import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Specification;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Dialogs.EditOrDeleteDialog;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.carsmodels.Main.MainActivity.db;

public class specificationSettings extends AnimatedActivity {

    private FloatingActionButton addNewSpecificationButton;
    private FlexboxLayout specificationsContainer;
    private final int GET_NEW_SPEC = 200;

    /**
     * Activity LifeCycle Events
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specification_settings);
        addNewSpecificationButton = findViewById(R.id.addNewSpec);
        specificationsContainer = findViewById(R.id.specificationContainer);
        /**
         * Set Button Action
         */
        addNewSpecificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewSpecification = new Intent(specificationSettings.this, addNewSpecification.class);
                startActivityForResult(addNewSpecification, GET_NEW_SPEC);
            }
        });
        loadSpecifications();
    }


    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.SlideInUp)
                .duration(500).playOn(addNewSpecificationButton);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_NEW_SPEC && resultCode == RESULT_OK && data != null) {
            addSpecification((Specification) data.getSerializableExtra("newSpec"));
        }
    }

    /**
     * Specifications Section
     */

    public void loadSpecifications() {

        specificationsContainer.removeAllViews();
        ArrayList<Specification> specs = Specification.getAllspecifications();
        final specificationSettings globalThis = this;
        for (final Specification spec : specs) {
            addSpecification(spec);
        }
    }

    public void addSpecification(final Specification spec) {
        final View specificationView = View.inflate(this, R.layout.model_box, null);
        ((TextView) specificationView.findViewById(R.id.modelName)).setText(spec.getName());
        if (spec.getImg() != null && !spec.getImg().trim().equals("")) {
            util.getInstance().setGlideImage(this, spec.getImg(), (ImageView) specificationView.findViewById(R.id.modelImage));
        }
        specificationView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new EditOrDeleteDialog(specificationSettings.this) {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.editIcon:
                                this.cancel();
                                new SpecificationCuDialogFragment(spec, specificationView).show(getSupportFragmentManager(), "edit_Specification");
                                break;
                            case R.id.deleteIcon:
                                this.cancel();
                                new ConfirmDialog(specificationSettings.this, "Delete Specification?", "this Specification will delete from all cars categories too.", android.R.drawable.ic_delete) {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long result = spec.remove();
                                        if (result == 1) {
                                            Toast.makeText(getApplicationContext(), "Specification Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            specificationsContainer.removeView(specificationView);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }.show();
                                break;
                        }
                    }
                }.show();
                return true;
            }
        });
        specificationsContainer.addView(specificationView);
    }
}
