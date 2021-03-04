package com.pegasusgroup.riarewards.fragments;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.pegasusgroup.riarewards.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThankYou extends BaseFragment {

    private AppCompatButton cmdDone;

    public ThankYou() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_thank_you, container, false);
    }

    @Override
    protected void initComponents(View view) {
        cmdDone = findViewById(R.id.cmdDone);
    }

    @Override
    protected void setListeners() {
        cmdDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentReplacer.replace(new Home());
            }
        });
    }
}