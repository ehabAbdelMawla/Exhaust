package com.example.carsmodels.Speceficeations;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carsmodels.util.AnimatedActivity;

/**
 * This Activity just hold add & Update Specification Fragment
 */
public class addNewSpecification extends AnimatedActivity {

private final int mainLayoutId=100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainLayOut=new LinearLayout(this);
        mainLayOut.setId(mainLayoutId);
        mainLayOut.setGravity(Gravity.CENTER);
        mainLayOut.setOrientation(LinearLayout.VERTICAL);

        getSupportFragmentManager().beginTransaction()
                .add(mainLayOut.getId(), new SpecificationCuDialogFragment()).commit();

        setContentView(mainLayOut);
    }
}
