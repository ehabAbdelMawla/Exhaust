package com.example.carsmodels.BrandCars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.ImageViewPager.FullView;
import com.example.carsmodels.ImageViewPager.ScreenSlidePagerAdapter;
import com.example.carsmodels.MainActivity;
import com.example.carsmodels.R;
import com.example.carsmodels.dataModel.CarImage;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CarColorImages extends AppCompatActivity {
    private FloatingActionButton addButton;
    private String cardName;
    private int relationId;
    private int GET_FROM_GALLERY = 1;
    private FlexboxLayout imagesContainer;
    private Menu menu = null;
    private boolean selectedMode = false;

    private Map<Integer, CarImage> selectedIds = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specification_settings);
//        get Bundle Paramteres
        Bundle b = getIntent().getExtras();
        if (b != null) {
            cardName = b.getString("CarName");
            relationId = b.getInt("relationId");
        }
        setTitle(cardName);


//        Map Componnet
        addButton = findViewById(R.id.addNewSpec);
        imagesContainer = findViewById(R.id.specificationContainer);

//        Set Button Action
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                if (selectedMode) {
                    clearSelectionMode();
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_FROM_GALLERY);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadImages();
    }

    private void loadImages() {
        imagesContainer.removeAllViews();
        ScreenSlidePagerAdapter.images = util.getInstance().getCarImages(relationId);
        for (final CarImage imgObj : ScreenSlidePagerAdapter.images) {
            final View ImageViewParent = View.inflate(this, R.layout.car_image_item, null);
            ((ImageView) ImageViewParent.findViewById(R.id.ImageView)).setImageBitmap(BitmapFactory.decodeByteArray(imgObj.getImg(), 0, imgObj.getImg().length));
            final RadioButton rbtn = ImageViewParent.findViewById(R.id.selectedRadio);
            final int i = imagesContainer.getChildCount();
            ImageViewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!selectedMode) {
                        Intent in = new Intent(CarColorImages.this, FullView.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("start", i);
                        in.putExtras(bundle);
                        startActivity(in);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    } else {
                        selectImage(rbtn, imgObj);
                    }
                }
            });
            ImageViewParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!selectedMode) {
                        applaySelectionMode();
                        selectImage(rbtn, imgObj);
                        return true;
                    }
                    return false;

                }
            });

            imagesContainer.addView(ImageViewParent);
        }
    }


    //   Load Image when retreve result
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        try {

            if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
                // Get the Image from data
                if (data.getData() != null) {
                    try {
                        Uri mImageUri = data.getData();
                        Bitmap bitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                        long result = util.getInstance().addColorImage(relationId, util.getInstance().getBitmapAsByteArray(bitMap));
                        if (result > 0) {
                            Toast.makeText(getApplicationContext(), "Image Added Successfully", Toast.LENGTH_SHORT).show();
                        } else if (result == -1) {
                            Toast.makeText(getApplicationContext(), "Image Already Exist!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Uncatched Error ", Toast.LENGTH_SHORT).show();
                        }

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error ,Cannot Load Image", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (data.getClipData() != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ClipData mClipData = data.getClipData();
                                final int totalImages = mClipData.getItemCount();
                                int repeatedImage = 0, failImage = 0;
                                long result;
                                for (int i = 0; i < mClipData.getItemCount(); i++) {
                                    try {
                                        ClipData.Item item = mClipData.getItemAt(i);
                                        Uri uri = item.getUri();
                                        Bitmap bitMap = MediaStore.Images.Media.getBitmap(CarColorImages.this.getContentResolver(), uri);
                                        result = util.getInstance().addColorImage(relationId, util.getInstance().getBitmapAsByteArray(bitMap));
                                        if (result == -1) {
                                            repeatedImage += 1;
                                        } else if (result < 0) {
                                            failImage += 1;
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("Exception " + ex);
                                    }
                                }
                                showToast(totalImages, repeatedImage, failImage);
                            }
                        }).start();
                    }
                }


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


    }

    // show Toast that indicate details of Adding Multiple Images
    public void showToast(final int totalImages, final int finalRepeatedImage, final int finalFailImage) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalRepeatedImage == 0 && finalFailImage == 0) {
                    Toast.makeText(CarColorImages.this, totalImages + " Images Added Successfully", Toast.LENGTH_LONG).show();
                } else if (finalRepeatedImage == 0) {
                    Toast.makeText(CarColorImages.this, (totalImages - finalFailImage) + " Images Added \n" + finalFailImage + " Faild ", Toast.LENGTH_LONG).show();

                } else if (finalFailImage == 0) {
                    Toast.makeText(CarColorImages.this, (totalImages - finalRepeatedImage) + " Images Added \n" + finalRepeatedImage + " Already Exists", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CarColorImages.this, (totalImages - (finalRepeatedImage + finalFailImage)) + " Images Added \n" + finalRepeatedImage + " Already Exists \n" + finalFailImage + "Faild", Toast.LENGTH_LONG).show();
                }
                loadImages();   // High Cost , must fix in updates
            }
        });


    }

//    Action Bar Section

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                clearSelectionMode();
                return true;
            case R.id.shareIcon:
                Iterator<Integer> itr = selectedIds.keySet().iterator();
                File f = null;
                Bitmap bitMap = null;
                CarImage cimg = null;
                ArrayList<Uri> imagesUri = new ArrayList<>();
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                whatsappIntent.setPackage("com.whatsapp");

                while (itr.hasNext()) {
                    try {
                        cimg = selectedIds.get(itr.next());
                        bitMap = BitmapFactory.decodeByteArray(cimg.getImg(), 0, cimg.getImg().length);
                        f = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name)+cimg.getId() + ".png");
                        FileOutputStream outputStream = new FileOutputStream(f);
                        bitMap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        imagesUri.add(Uri.fromFile(f));

                    } catch (Exception e) {
                        Log.i("CarColorImages", "share", e);

                    }
                }
                whatsappIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imagesUri);
                whatsappIntent.setType("image/*");
                whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(whatsappIntent);
                clearSelectionMode();
                return true;
            case R.id.deleteIcon:
                new AlertDialog.Builder(this)
                        .setTitle("Delete " + selectedIds.size() + " Images?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                StringBuilder sb = new StringBuilder();
                                Iterator<Integer> itr = selectedIds.keySet().iterator();
                                final int size = selectedIds.size();
                                while (itr.hasNext()) {
                                    sb.append(itr.next() + (itr.hasNext() ? "," : ""));
                                }
                                deleteImagesWithIds(sb.toString());
                                clearSelectionMode();
                                Toast.makeText(CarColorImages.this, +size + " Image Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deleteImagesWithIds(String ids) {
        try {
            MainActivity.db.getWritableDatabase().execSQL(
                    "DELETE FROM carImages WHERE id IN(" + ids + ")");
            loadImages();
        } catch (Exception e) {
            Log.i("CarColorImages", "deleteImagesWithIds", e);
        }
    }

    private void selectImage(RadioButton rButton, CarImage obj) {
        if (selectedIds.get(obj.getId()) == null) {
            selectedIds.put(obj.getId(), obj);
            rButton.setChecked(true);
        } else {
            selectedIds.remove(obj.getId());
            rButton.setChecked(false);
        }
        updateSelectedNumber();

    }

    private void updateSelectedNumber() {
        menu.findItem(R.id.selectionNumber).setTitle(selectedIds.size() + " Selected");
    }

    private void applaySelectionMode() {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_menu, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);   // to hide the app Name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // to visible back arrow icon
        selectedMode = true;
        resetRadioButton(View.VISIBLE, false);
    }

    private void clearSelectionMode() {
        menu.clear();
        selectedIds.clear();
        selectedMode = false;
        getSupportActionBar().setDisplayShowTitleEnabled(true);   // to show the app Name
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);   // to hide back arrow icon
        resetRadioButton(View.INVISIBLE, false);
    }

    private void resetRadioButton(final int viewStat, final boolean radioButtonState) {
        RadioButton temp = null;
        for (View imgView : imagesContainer.getTouchables()) {
            temp = imgView.findViewById(R.id.selectedRadio);
            temp.setVisibility(viewStat);
            temp.setChecked(radioButtonState);
        }
    }


    @Override
    public void onBackPressed() {
        if (selectedMode)
            clearSelectionMode();
        else
            super.onBackPressed();
    }
}


