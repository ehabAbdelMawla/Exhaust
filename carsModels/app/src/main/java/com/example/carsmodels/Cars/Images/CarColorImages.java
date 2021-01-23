package com.example.carsmodels.Cars.Images;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.zooming_entrances.ZoomInAnimator;
import com.example.carsmodels.Cars.Images.ImageViewPager.FullView;
import com.example.carsmodels.Cars.Images.ImageViewPager.ScreenSlidePagerAdapter;
import com.example.carsmodels.Cars.Images.ImagesThreads.AddImageThread;
import com.example.carsmodels.util.AnimatedActivity;
import com.example.carsmodels.util.CloseLoaderThread;
import com.example.carsmodels.util.Dialogs.ConfirmDialog;
import com.example.carsmodels.util.Loader.Loader;
import com.example.carsmodels.R;
import com.example.carsmodels.DataModel.CarImage;
import com.example.carsmodels.util.util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.carsmodels.Cars.Images.ImageViewPager.ScreenSlidePagerAdapter.updateIndicesOfImageViewList;

public class CarColorImages extends AnimatedActivity implements View.OnClickListener, View.OnLongClickListener {
    /**
     * Variables Declarations
     */
    private String carName;
    private int relationId;
    private final int GET_FROM_GALLERY = 1;
    private FlexboxLayout imagesContainer;
    private Menu menu = null;
    protected boolean selectedMode = false;
    private final Map<Integer, CustomeImage> selectedIds = new HashMap<>();

    /**
     * Activity LifeCycle Events
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specification_settings);
        /**
         * get Bundles
         */
        /**
         * Map Components
         */
        FloatingActionButton addImagesButton = findViewById(R.id.addNewSpec);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            carName = b.getString("CarName");
            relationId = b.getInt("relationId");
        }
        /**
         * Update Activity Title According to Car Name
         */
        setTitle(carName);

        addImagesButton.setOnClickListener(this);
        imagesContainer = findViewById(R.id.specificationContainer);
        loadImages(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        YoYo.with(Techniques.ZoomInUp)
                .duration(500).playOn(findViewById(R.id.addNewSpec));
        selectedIds.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenSlidePagerAdapter.images.clear();
    }

    /**
     * Activity Events
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
                //  Add One Image
                if (data.getData() != null) {
                    loaderDialog.displayLoader();
                    new CloseLoaderThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Uri mImageUri = data.getData();
                                Bitmap image = MediaStore.Images.Media.getBitmap(CarColorImages.this.getContentResolver(), mImageUri);
                                image = util.getInstance().rotateImage(CarColorImages.this, image, mImageUri);
                                final int id = util.getInstance().getMaximum("id", "carImages");

                                final long result = CarImage.addCarImageRelation(relationId, util.getInstance().saveToInternalStorage(CarColorImages.this, image, "colorImages", relationId + new Date().getTime() + ".png"), id);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result > 0) {
                                            Toast.makeText(getApplicationContext(), R.string.add_image_success_msg, Toast.LENGTH_SHORT).show();
                                            loadImages(id);
                                        } else if (result == -1) {
                                            Toast.makeText(getApplicationContext(), R.string.duplicate_image_error_msg, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), R.string.uncatched_error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), R.string.image_file_does_notExists, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }), loaderDialog).start();


                } else {
                    //  Add Multible Images
                    if (data.getClipData() != null) {
                        final ClipData mClipData = data.getClipData();
                        final ProgressDialog progressBar = util.getInstance().createProgressDialog(this, getResources().getString(R.string.add_images_loader_msg), 0, mClipData.getItemCount());
                        progressBar.show();
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        int id = util.getInstance().getMaximum("id", "carImages");
                                        int startId = id;
                                        AddImageThread.setInit(CarColorImages.this, progressBar);
                                        AddImageThread.operationResults[0] = mClipData.getItemCount();
                                        ExecutorService executor = Executors.newFixedThreadPool(5);
                                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                                            executor.execute(new AddImageThread(mClipData.getItemAt(i).getUri(), relationId, id++));
                                        }
                                        executor.shutdown();
                                        try {
                                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        AddImageThread.clear();
                                        showToast(AddImageThread.operationResults[0], AddImageThread.operationResults[1], AddImageThread.operationResults[2], progressBar, startId);
                                        System.gc();
                                    }
                                }
                        ).start();
                    }
                }
            } else {
                Toast.makeText(this, R.string.un_picked_image_error, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.uncatched_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back Arrow icon
                clearSelectionMode();
                return true;
            case R.id.shareIcon:
                if (!selectedIds.isEmpty()) {
                    shareSelectedImages();
                    return true;
                }
                return false;
            case R.id.deleteIcon:
                if (!selectedIds.isEmpty()) {
                    deleteSelectedImages();
                    return true;
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        if (selectedMode)
            clearSelectionMode();
        else
            super.onBackPressed();
    }

    /**
     * Methods To Load All Page Images and add to UI
     */
    private void loadImages(int startId) {
        loaderDialog.displayLoader();
        final StringBuilder sqlStat = new StringBuilder("SELECT * FROM carImages WHERE relationId=" + relationId);
        if (startId <= 0) {
            /**
             * Clear Images Container
             * Clear Slider List
             */
            imagesContainer.removeAllViews();
            ScreenSlidePagerAdapter.images.clear();
        } else {
            sqlStat.append(" And id>=").append(startId);
        }
        final Thread loadImagesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CarImage> images = CarImage.getCarImages(sqlStat.toString());
                for (final CarImage imgObj : images) {
                    final CustomeImage ImageViewParent = new CustomeImage(CarColorImages.this, ScreenSlidePagerAdapter.images.size(), imgObj);
//                    Add To Slider List
                    ScreenSlidePagerAdapter.images.add(ImageViewParent);
                    // set Listener
                    ImageViewParent.setOnClickListener(CarColorImages.this);
                    ImageViewParent.setOnLongClickListener(CarColorImages.this);
//                    Add To images Container
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.getInstance().setGlideImage(CarColorImages.this, imgObj.getImgPath(), ImageViewParent.getImage());
                            addViewWithAnimate(imagesContainer, ImageViewParent, Techniques.ZoomInUp, 350);

                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkIfEmpty(imagesContainer.getChildCount() == 0, imagesContainer, R.string.empty_image__msg);
                    }
                });

            }
        });
        new CloseLoaderThread(loadImagesThread, loaderDialog).start();


    }


    /**
     * Action Bar Actions
     */
    private void shareSelectedImages() {
        final ArrayList<Uri> imagesUri = new ArrayList<>();
        loaderDialog.displayLoader();
        Thread createImagesUri = new Thread(new Runnable() {
            @Override
            public void run() {
                Iterator<CustomeImage> itr = selectedIds.values().iterator();
                File tempFile = null;
                CarImage tempCarImage = null;
                while (itr.hasNext()) {
                    try {
                        tempCarImage = itr.next().getCarImageObj();
//                                    API Lower 24
                        tempFile = new File(tempCarImage.getImgPath());
                        Uri imageUri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //N => 24
                            imageUri = FileProvider.getUriForFile(
                                    CarColorImages.this,
                                    "com.example.carsmodels.provider",
                                    new File(tempCarImage.getImgPath()));
                        } else {
                            imageUri = Uri.fromFile(tempFile);
                        }
                        imagesUri.add(imageUri);
                    } catch (Exception e) {
                        Log.i("CarColorImages", "share", e);
                    }
                }
                util.getInstance().shareIntent(CarColorImages.this, imagesUri, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loaderDialog.dismissLoader();
                        clearSelectionMode();
                    }
                });
            }
        });
        createImagesUri.start();
    }

    private void deleteSelectedImages() {
        new ConfirmDialog(this, String.format(getResources().getString(R.string.delete_images_dialog_title), selectedIds.size()), android.R.drawable.ic_delete) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder sb = new StringBuilder();
                ArrayList<String> imagesPathsToDelete = new ArrayList<>();
                Iterator<Integer> itr = selectedIds.keySet().iterator();
                final int size = selectedIds.size();
                int currentId;
                while (itr.hasNext()) {
                    currentId = itr.next();
                    // Remove View From UI
                    removeViewWithAnimate(imagesContainer, selectedIds.get(currentId), Techniques.ZoomOutDown, 250, R.string.empty_image__msg);
                    //    Remove View From Silder
                    ScreenSlidePagerAdapter.images.remove(selectedIds.get(currentId));
                    imagesPathsToDelete.add(selectedIds.get(currentId).getCarImageObj().getImgPath());
                    sb.append(currentId + (itr.hasNext() ? "," : ""));
                }
                updateIndicesOfImageViewList();
                util.getInstance().removeFiles(imagesPathsToDelete);
                util.getInstance().deleteImagesWithIds(sb.toString());
                clearSelectionMode();
                Toast.makeText(CarColorImages.this, String.format(getResources().getString(R.string.delete_images_success_msg), size), Toast.LENGTH_SHORT).show();
            }
        }.show();
    }


    /**
     * Show Detailed Feedback  of Adding # of Images
     */
    public void showToast(final int totalImages, final int finalRepeatedImage, final int finalFailImage, final ProgressDialog progressBar, final int startId) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalRepeatedImage == 0 && finalFailImage == 0) {
                    Toast.makeText(CarColorImages.this, String.format(getResources().getString(R.string.add_images_success_msg), totalImages), Toast.LENGTH_LONG).show();
                } else if (finalRepeatedImage == 0) {
                    Toast.makeText(CarColorImages.this, String.format(getResources().getString(R.string.add_images_success_msg), totalImages - finalFailImage) + "\n" + String.format(getResources().getString(R.string.add_images_faild_msg), finalFailImage) + +finalFailImage + " Faild ", Toast.LENGTH_LONG).show();

                } else if (finalFailImage == 0) {
                    Toast.makeText(CarColorImages.this, String.format(getResources().getString(R.string.add_images_success_msg), totalImages - finalRepeatedImage) + "\n" + String.format(getResources().getString(R.string.add_images_duplicated_msg), finalRepeatedImage), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CarColorImages.this, String.format(getResources().getString(R.string.add_images_success_msg), (totalImages - (finalRepeatedImage + finalFailImage))) + "\n" + String.format(getResources().getString(R.string.add_images_faild_msg), finalFailImage) + "" + String.format(getResources().getString(R.string.add_images_duplicated_msg), finalRepeatedImage), Toast.LENGTH_LONG).show();
                }
                progressBar.dismiss();
                loadImages(startId);
            }
        });


    }

    /**
     * Selection Methods
     */
    private void selectImage(CustomeImage obj) {
        if (selectedIds.get(obj.getCarImageObj().getId()) == null) {
            selectedIds.put(obj.getCarImageObj().getId(), obj);
            obj.getRadioButton().setChecked(true);
        } else {
            selectedIds.remove(obj.getCarImageObj().getId());
            obj.getRadioButton().setChecked(false);
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

    /**
     * Clicks Implementations
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewSpec:
                if (selectedMode) {
                    clearSelectionMode();
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_FROM_GALLERY);
                break;
            case R.id.parentId:
                CustomeImage obj = (CustomeImage) v;
                if (!selectedMode) {
                    Intent in = new Intent(CarColorImages.this, FullView.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("start", obj.getIndex());
                    in.putExtras(bundle);
                    startActivity(in);

                } else {
                    selectImage(obj);
                }
                break;

            default:
                // Do No Thing

        }
    }

    /**
     * Long Clicks Implementations
     */
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.parentId:
                CustomeImage obj = (CustomeImage) v;
                if (!selectedMode) {
                    applaySelectionMode();
                    selectImage(obj);
                    return true;
                }
        }
        return false;
    }
}





