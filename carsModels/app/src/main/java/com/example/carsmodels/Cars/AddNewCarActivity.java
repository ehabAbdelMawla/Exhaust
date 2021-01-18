package com.example.carsmodels.Cars;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This Activity Just Hold Add and Update Fragment
 */
public class AddNewCarActivity extends AppCompatActivity {

    private final int mainLayoutId=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainLayOut=new LinearLayout(this);
        mainLayOut.setId(mainLayoutId);
        mainLayOut.setGravity(Gravity.CENTER);
        mainLayOut.setOrientation(LinearLayout.VERTICAL);

        getSupportFragmentManager().beginTransaction()
                .add(mainLayOut.getId(), new CarsAddAndUpdateFragment()).commit();

        setContentView(mainLayOut);

    }

}
