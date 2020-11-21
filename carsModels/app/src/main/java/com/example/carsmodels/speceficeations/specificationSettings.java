package com.example.carsmodels.speceficeations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carsmodels.BrandCars.BrandCars;
import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.Brand;
import com.example.carsmodels.dataModel.Specification;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.carsmodels.MainActivity.db;

public class specificationSettings extends AppCompatActivity {

    FloatingActionButton addNewSpecificationButton;
    public static boolean updateData = false;
    int GET_FROM_GALLERY=1;
    byte[] imageBytes;
    ImageView imageView;    //for edit dialg
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specification_settings);

//       Start Init component
        addNewSpecificationButton = findViewById(R.id.addNewSpec);

//       End Init component

        final specificationSettings globalThis = this;

//        Set Actions
        addNewSpecificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewSpecification = new Intent(globalThis, addNewSpecification.class);
                startActivity(addNewSpecification);
            }
        });


//        load Data
        loadSpecifications();


    }


    @Override
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.SlideInUp)
                .duration(500).playOn(addNewSpecificationButton);
        if (updateData) {
            loadSpecifications();
            updateData = false;
        }

    }

    private void loadSpecifications() {
        FlexboxLayout specificationsContainer = findViewById(R.id.specificationContainer);
        specificationsContainer.removeAllViews();
        ArrayList<Specification> specs = getAllspecifications();
        final specificationSettings globalThis = this;
        for (final Specification spec : specs) {
            final View modelLayOut = View.inflate(this, R.layout.model_box, null);
            ((TextView) modelLayOut.findViewById(R.id.modelName)).setText(spec.getName());
            if (spec.getImg() != null) {
                ((ImageView) modelLayOut.findViewById(R.id.modelImage)).setImageBitmap(BitmapFactory.decodeByteArray(spec.getImg(), 0, spec.getImg().length));
            }
            modelLayOut.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder alertDialogBuilde = new AlertDialog.Builder(globalThis);
                    final View popUpView = getLayoutInflater().inflate(R.layout.edit_delete_popup, null);
                    alertDialogBuilde.setView(popUpView);
                    final AlertDialog alert = alertDialogBuilde.create();
                    alert.show();

                    popUpView.findViewById(R.id.editIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            final AlertDialog.Builder editAlertDialogBuilder = new AlertDialog.Builder(globalThis);
                            View editView = getLayoutInflater().inflate(R.layout.add_new_specification, null);
                            editAlertDialogBuilder.setView(editView);
                            final AlertDialog editAlert= editAlertDialogBuilder.create();
                            editAlert.show();


//                          Init components
                            final EditText specificationEditText= editView.findViewById(R.id.specificationEditText);
                            imageView=  editView.findViewById(R.id.imageView);
                            FloatingActionButton uploadImageButton=editView.findViewById(R.id.addImageButton);
                            Button editButton=editView.findViewById(R.id.addButton);

//                          Set Init Data
                            specificationEditText.setText(spec.getName());
                            editButton.setText("Edit");
                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(spec.getImg(), 0, spec.getImg().length));
                            imageBytes=spec.getImg();
//                           Set Actions
                            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                                }
                            });

                            editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(util.getInstance().getVal(specificationEditText).equalsIgnoreCase("")){
                                        Toast.makeText(globalThis,"Specification cannot be empty",Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        spec.setName(util.getInstance().getVal(specificationEditText));
                                        spec.setImg(imageBytes);
                                        long result = spec.update();

                                        if (result > 0) {
                                            Toast.makeText(getApplicationContext(), "Specification updated Successfully", Toast.LENGTH_SHORT).show();
                                        } else if (result == 0) {
                                            Toast.makeText(getApplicationContext(), "Specification Already Exist!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                        }
                                        loadSpecifications();
                                        editAlert.cancel();
                                    }

                                }
                            });



                        }
                    });


                    popUpView.findViewById(R.id.deleteIcon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                            new AlertDialog.Builder(globalThis)
                                    .setTitle("Delete Specification?")
                                    .setMessage("this Specification will delete from all cars categories too.")
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            long result = spec.remove();
                                            if (result == 1) {
                                                Toast.makeText(getApplicationContext(), "Specification Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                loadSpecifications();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    });
                    return true;
                }
            });
            specificationsContainer.addView(modelLayOut);
        }
    }


    public ArrayList<Specification> getAllspecifications() {
        ArrayList<Specification> specifications = new ArrayList<>();
        try {
            Cursor res = db.getReadableDatabase().rawQuery("SELECT * FROM specifications", null);
            while (res.moveToNext()) {
                specifications.add(new Specification(res.getInt(res.getColumnIndex("id")),
                        res.getString(res.getColumnIndex("name")),
                        res.getBlob(res.getColumnIndex("img"))));
            }

        } catch (Exception e) {
            Log.i(MainActivity.class.getName(), "getAll specifications", e);
        }
        return specifications;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//               === Compress Image ===
//                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                imageBytes = util.getInstance().getBitmapAsByteArray(bitmap);

                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                imageView.setImageResource(R.drawable.placholder);
                Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
