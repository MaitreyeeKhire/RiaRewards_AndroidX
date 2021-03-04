package com.pegasusgroup.riarewards.activity;

import android.content.Intent;
import android.os.Bundle;

import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.fragments.AroundMe;

import java.util.Objects;

public class Map extends BaseAppCompatActivity {

    private AroundMe aroundMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_map;
    }

    @Override
    protected void initComponents() {
        aroundMe = (AroundMe) getSupportFragmentManager().findFragmentById(R.id.aroundMe);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101) {
            aroundMe.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}