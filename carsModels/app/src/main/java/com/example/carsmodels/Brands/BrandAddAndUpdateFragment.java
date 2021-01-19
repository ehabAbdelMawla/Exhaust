package com.example.carsmodels.Brands;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.Main.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.Brand;
import com.example.carsmodels.util.AnimatedFragment;
import com.example.carsmodels.util.CloseLoaderThread;
import com.example.carsmodels.util.Loader.Loader;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class BrandAddAndUpdateFragment extends AnimatedFragment {
    /**
     * Instance Attributes
     */
    private final int GET_FROM_GALLERY = 1;

    private Bitmap bitmap;
    private ImageView imageView;
    private EditText brandNameText;
    private EditText brandAgentText;
    private Brand brand;
    private View brandView;

    /**
     * Default Constructor Used When Create New Brand
     */
    public BrandAddAndUpdateFragment() {

    }


    /**
     * Constructor Used When Update Existing Brand
     */
    public BrandAddAndUpdateFragment(Brand brand, View brandView) {
        this.brand = brand;
        this.brandView = brandView;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_new_brand, container, false);
        final util utilMethods = util.getInstance();
        final boolean updateMode = brand != null;
        /**
         * Map Components
         */
        brandNameText = rootView.findViewById(R.id.brandName);
        brandAgentText = rootView.findViewById(R.id.brandAgent);
        Button addButton = rootView.findViewById(R.id.addButton);
        FloatingActionButton uploadImage = rootView.findViewById(R.id.addImageButton);
        imageView = rootView.findViewById(R.id.imageView);

        final Loader loaderDialog = new Loader(getContext());

        if (updateMode) {
            /**
             * Set Basic Data when update Mode
             */
            if (brand.getImg() != null && !brand.getImg().trim().equals("")) {
                util.getInstance().setGlideImage(this, brand.getImg(), imageView);
            }
            brandNameText.setText(brand.getBrandName());
            brandAgentText.setText(brand.getBrandAgent());
            addButton.setText(R.string.update);
        }

        /**
         * Set Buttons Actions
         */
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilMethods.getVal(brandNameText).equals("")) {
                    Toast.makeText(getActivity(), R.string.brand_name_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (utilMethods.getVal(brandAgentText).equals("")) {
                    Toast.makeText(getActivity(), R.string.brand_agent_required_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                loaderDialog.displayLoader();
                final Thread addOrUpdate=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Brand newBrand = new Brand(util.getInstance().getMaximum("id", "brands"), utilMethods.getVal(brandNameText), utilMethods.getVal(brandAgentText), bitmap == null ? "" : util.getInstance().saveToInternalStorage(BrandAddAndUpdateFragment.this.getContext(), bitmap, "brandImages", new Date().getTime() + ".png"));
                        if (updateMode) {
                            newBrand.setId(brand.getId());
                            if (bitmap != null) { //he change Image
                                util.getInstance().removeFile(brand.getImg());
                            } else {
                                newBrand.setImg(brand.getImg());
                            }
                        }
                        long operationResult = updateMode ? newBrand.update() : newBrand.insert();
                        if (operationResult > 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), updateMode ? R.string.update_brand_success_msg : R.string.add_brand_success_msg, Toast.LENGTH_SHORT).show();
                                }
                            });

                            if (getDialog() != null) {
                                getDialog().dismiss();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((FlexboxLayout) brandView.getParent()).removeView(brandView);
                                        ((MainActivity) getActivity()).addBrand(newBrand);
                                    }
                                });
                            } else {
                                Intent newBrandIntent = new Intent();
                                newBrandIntent.putExtra("newBrand", newBrand);
                                getActivity().setResult(Activity.RESULT_OK, newBrandIntent);
                                getActivity().finish();
                            }
                        } else if ((!updateMode && operationResult == -1) || (updateMode && operationResult == 0)) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.duplicate_brand_error_msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.uncatched_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                new CloseLoaderThread(addOrUpdate,loaderDialog).start();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        return rootView;
    }


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
                Toast.makeText(getActivity(), R.string.image_file_does_notExists, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                bitmap = null;
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), R.string.image_file_does_notExists, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
