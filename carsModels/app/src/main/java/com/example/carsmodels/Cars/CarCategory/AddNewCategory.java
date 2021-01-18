package com.example.carsmodels.Cars.CarCategory;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
/**
 * Activity That Holds Category Adding Fragment
 */
public class AddNewCategory extends AppCompatActivity {
    /**
     * Declare Instance Variables
     */
    private final int mainLayoutId = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *  Build Fragment parent
         */
        LinearLayout mainLayOut = new LinearLayout(this);
        mainLayOut.setId(mainLayoutId);
        mainLayOut.setGravity(Gravity.CENTER);
        mainLayOut.setOrientation(LinearLayout.VERTICAL);
        getSupportFragmentManager().beginTransaction()
                .add(mainLayOut.getId(), new CategoryAddAndUpdateFragment()).commit();
        setContentView(mainLayOut);

    }
}
