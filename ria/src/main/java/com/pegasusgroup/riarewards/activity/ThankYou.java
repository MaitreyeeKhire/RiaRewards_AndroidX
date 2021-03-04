package com.pegasusgroup.riarewards.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.pegasusgroup.riarewards.R;

import java.util.Objects;

public class ThankYou extends BaseAppCompatActivity {

    private AppCompatButton cmdDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_thank_you;
    }

    @Override
    protected void initComponents() {
        cmdDone = findViewById(R.id.cmdDone);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        cmdDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}