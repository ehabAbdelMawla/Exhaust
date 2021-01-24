package com.example.carsmodels.Speceficeations;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Specification;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.CloseLoaderThread;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Dialogs.EditOrDeleteDialog;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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
        YoYo.with(Techniques.ZoomInUp)
                .duration(500).playOn(addNewSpecificationButton);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_NEW_SPEC && resultCode == RESULT_OK && data != null) {
            addSpecification((Specification) data.getSerializableExtra("newSpec"),-1);
            checkIfEmpty(specificationsContainer.getChildCount() == 0, specificationsContainer, R.string.specification_empty_msg);
        }
    }

    /**
     * Specifications Section
     */

    public void loadSpecifications() {
        specificationsContainer.removeAllViews();
        loaderDialog.displayLoader();
        new CloseLoaderThread(new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Specification> specs = Specification.getAllspecifications();
                for (final Specification spec : specs) {
                    addSpecification(spec,-1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkIfEmpty(specificationsContainer.getChildCount() == 0, specificationsContainer, R.string.specification_empty_msg);
                    }
                });
            }
        }), loaderDialog).start();

    }


    public void addSpecification(final Specification spec,final int index) {
        final View specificationView = View.inflate(this, R.layout.model_box, null);
        ((TextView) specificationView.findViewById(R.id.modelName)).setText(spec.getName());

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
                                new ConfirmDialog(specificationSettings.this, R.string.delete_specification_dialog_title, R.string.delete_specification_dialog_msg, android.R.drawable.ic_delete) {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long result = spec.remove();
                                        if (result == 1) {
                                            Toast.makeText(getApplicationContext(), R.string.delete_specification_success_msg, Toast.LENGTH_SHORT).show();
                                            removeViewWithAnimate(specificationsContainer, specificationView, Techniques.ZoomOutDown, 300, R.string.specification_empty_msg);
                                        } else {
                                            Toast.makeText(getApplicationContext(), R.string.uncatched_error, Toast.LENGTH_SHORT).show();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (spec.getImg() != null && !spec.getImg().trim().equals("")) {
                    util.getInstance().setGlideImage(specificationSettings.this, spec.getImg(), (ImageView) specificationView.findViewById(R.id.modelImage));
                }
                addViewWithAnimate(specificationsContainer, specificationView, index, Techniques.ZoomInUp, 350);
            }
        });
    }
}
