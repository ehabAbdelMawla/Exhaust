package com.example.carsmodels.Speceficeations;


import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.carsmodels.Cars.Images.CarColorImages;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Specification;
import com.example.carsmodels.util.AnimatedFragment;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;


public class SpecificationCuDialogFragment extends AnimatedFragment {
    /**
     * Instance Variables Declarations
     */
    private final int GET_FROM_GALLERY = 1;
    private Bitmap bitmap;
    private ImageView imageView;
    private EditText specificationName;
    private Specification exitsSpec;
    private View specificationView;

    /**
     * Constructor used for create new Specification
     */
    public SpecificationCuDialogFragment() {
    }

    /**
     * Constructor used for Update Existing Specification
     */
    public SpecificationCuDialogFragment(Specification spec, View specificationView) {
        this.exitsSpec = spec;
        this.specificationView = specificationView;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_new_specification, container, false);
        imageView = rootView.findViewById(R.id.imageView);
        specificationName = rootView.findViewById(R.id.specificationEditText);
        Button addnewSpecificationButton = rootView.findViewById(R.id.addButton);
        FloatingActionButton uploadImageButton = rootView.findViewById(R.id.addImageButton);
        final boolean updateMode = exitsSpec != null;

        if (updateMode) {
            if (!exitsSpec.getImg().trim().equals("")) {
                util.getInstance().setGlideImage(this, exitsSpec.getImg(), imageView);
            }
            specificationName.setText(exitsSpec.getName());
            addnewSpecificationButton.setText("Update");
        }

        /**
         * Set Buttons Actions
         */
        addnewSpecificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (util.getInstance().getVal(specificationName).equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Specification Name is Required", Toast.LENGTH_LONG).show();
                } else if (bitmap == null && exitsSpec == null) {
                    Toast.makeText(getActivity(), "Specification icon is Required", Toast.LENGTH_LONG).show();
                } else {
                    Specification newSpecification = new Specification(util.getInstance().getMaximum("id", "specifications"), util.getInstance().getVal(specificationName), (updateMode && bitmap == null) ? exitsSpec.getImg() : util.getInstance().saveToInternalStorage(SpecificationCuDialogFragment.this.getContext(), bitmap, "brandImages", new Date().getTime() + ".png"));
                    if (updateMode) {
                        newSpecification.setId(exitsSpec.getId());
                        if (bitmap != null) {
                            util.getInstance().removeFile(exitsSpec.getImg());
                        }
                    }
                    long operationResult = updateMode ? newSpecification.update() : newSpecification.insert();
                    if (operationResult > 0) {
                        Toast.makeText(getActivity(), updateMode ? "Specification updated Successfully" : "Specification Added Successfully", Toast.LENGTH_SHORT).show();
                        if (getDialog() != null) {
                            getDialog().dismiss();
                            ((FlexboxLayout) specificationView.getParent()).removeView(specificationView);
                            ((specificationSettings) getActivity()).addSpecification(newSpecification);
                        } else {
                            Intent newSpecificationIntent = new Intent();
                            newSpecificationIntent.putExtra("newSpec", newSpecification);
                            getActivity().setResult(Activity.RESULT_OK, newSpecificationIntent);
                            getActivity().finish();
                        }
                    } else if ((operationResult == -1 && exitsSpec == null) || (operationResult == 0 && exitsSpec != null)) {
                        Toast.makeText(getActivity(), "Specification Already Exist!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        return rootView;
    }


    //   Load Image when retreve result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                bitmap = null;
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                bitmap = null;
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
