package com.example.carsmodels.speceficeations;


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
import androidx.fragment.app.DialogFragment;

import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Specification;
import com.example.carsmodels.util.util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;


public class SpecificationCuDialogFragment extends DialogFragment {

    private final int GET_FROM_GALLERY = 1;
    private byte[] imageBytes;
    private Button AddnewSpecificationButton;
    private FloatingActionButton uploadImageButton;
    private ImageView imageView;
    private EditText specificationName;
    private Specification exitsSpec;    //edit Notation

    //    Default Constructor
    public SpecificationCuDialogFragment() {
    }

    //    Edit Window Constuructor
    public SpecificationCuDialogFragment(Specification spec) {
        this.exitsSpec = spec;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_new_specification, container, false);

        //        Init Vars
        imageView = rootView.findViewById(R.id.imageView);
        AddnewSpecificationButton = rootView.findViewById(R.id.addButton);
        uploadImageButton = rootView.findViewById(R.id.addImageButton);
        specificationName = rootView.findViewById(R.id.specificationEditText);

        if (exitsSpec != null) {
            imageBytes=exitsSpec.getImg();
            if(imageBytes!=null){
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
            specificationName.setText(exitsSpec.getName());
            AddnewSpecificationButton.setText("Update");
        }


//        Set Actions
        AddnewSpecificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (util.getInstance().getVal(specificationName).equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Specification Name is Required", Toast.LENGTH_LONG).show();
                } else if (imageBytes == null) {
                    Toast.makeText(getActivity(), "Specification icon is Required", Toast.LENGTH_LONG).show();
                } else {
                    Specification newSpecification = new Specification(util.getInstance().getVal(specificationName), imageBytes);
                    if(exitsSpec != null){
                        newSpecification.setId(exitsSpec.getId());
                    }
                    long operationResult = exitsSpec == null ? newSpecification.insert() : newSpecification.update();
                    System.out.println(operationResult);
                    System.out.println(operationResult);
                    System.out.println(operationResult);

                    if (operationResult > 0) {
                        Toast.makeText(getActivity(), exitsSpec == null ? "Specification Added Successfully" : "Specification updated Successfully", Toast.LENGTH_SHORT).show();

//                    if fragment usage finish parent and set parent update Notation
                        if (getDialog() == null) {
                            getActivity().finish();
                            specificationSettings.updateData = true;
                        } else {
//                        if dialog usage close dialog and update Data
                            getDialog().dismiss();
                            ((specificationSettings) getActivity()).loadSpecifications();
                        }
                    } else if ((operationResult == -1 && exitsSpec == null) || (operationResult == 0 && exitsSpec != null)) {
                        Toast.makeText(getActivity(), "Specification Already Exist!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                    }
                }
                /**
                 *                                          if (result > 0) {
                 * //                                            Toast.makeText(getApplicationContext(), "Specification updated Successfully", Toast.LENGTH_SHORT).show();
                 * //                                        } else if (result == 0) {
                 * //                                            Toast.makeText(getApplicationContext(), "Specification Already Exist!", Toast.LENGTH_SHORT).show();
                 * //                                        } else {
                 * //                                            Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                 * //                                        }
                 */

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
            System.out.println(selectedImage);
            System.out.println(selectedImage);
            System.out.println(selectedImage);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
//               === Compress Image ===
//                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
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
