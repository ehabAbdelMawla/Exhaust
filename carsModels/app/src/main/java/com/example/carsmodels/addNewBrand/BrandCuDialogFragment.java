 package com.example.carsmodels.addNewBrand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Brand;
import com.example.carsmodels.speceficeations.specificationSettings;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class BrandCuDialogFragment extends DialogFragment {


    private final int GET_FROM_GALLERY = 1;
    private byte[] imageBytes;
    private ImageView imageView;
    private EditText brandNameText;
    private EditText brandAgentText;
    private Button addButton;
    private FloatingActionButton uploadImage;
    private Brand brand;

    //Default Constructor
    public BrandCuDialogFragment() {

    }

    //    Edit Window Constuructor
    public BrandCuDialogFragment(Brand brand) {
        this.brand = brand;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.add_new_brand, container, false);

        final util utilMethods = util.getInstance();
//        Map Components
        brandNameText = rootView.findViewById(R.id.brandName);
        brandAgentText = rootView.findViewById(R.id.brandAgent);
        addButton = rootView.findViewById(R.id.addButton);
        uploadImage = rootView.findViewById(R.id.addImageButton);
        imageView=rootView.findViewById(R.id.imageView);

        if (brand != null) {
            imageBytes = brand.getImg();
            if(imageBytes!=null){
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
            brandNameText.setText(brand.getBrandName());
            brandAgentText.setText(brand.getBrandAgent());
            addButton.setText("Update");
        }


//        Add Button Actions

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utilMethods.getVal(brandNameText).equals("")) {
                    Toast.makeText(getActivity(), "Brand Name is Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (utilMethods.getVal(brandAgentText).equals("")) {
                    Toast.makeText(getActivity(), "Brand Agent is Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                Brand newBrand = new Brand(utilMethods.getVal(brandNameText), utilMethods.getVal(brandAgentText), imageBytes);
                if (brand != null) {
                    newBrand.setId(brand.getId());
                }
                long operationResult = brand == null ? newBrand.insert() : newBrand.update();
                if (operationResult > 0) {
                    Toast.makeText(getActivity(), brand == null ? "Brand Added Successfully" : "Brand Updated Successfully", Toast.LENGTH_SHORT).show();

//                    if fragment usage finish parent and set parent update Notation
                    if (getDialog() == null) {
                        getActivity().finish();
                        MainActivity.updateData = true;
                    } else {
//                        if dialog usage close dialog and update Data
                        getDialog().dismiss();
                        ((MainActivity) getActivity()).loadModels();
                    }
                } else if ((brand == null && operationResult == -1) || (brand != null && operationResult == 0)) {
                    Toast.makeText(getActivity(), "Brand Already Exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                }
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

    //   Load Image when retreve result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                imageBytes = util.getInstance().getBitmapAsByteArray(bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getActivity(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
