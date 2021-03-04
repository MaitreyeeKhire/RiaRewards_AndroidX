package com.pegasusgroup.riarewards.fragments;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pegasusgroup.riarewards.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerCard extends BaseFragment {

    private FrameLayout frameLayout;
    private AppCompatTextView txtRiaCustomerNumber;
    private AppCompatImageView imgBarCode;

    public CustomerCard() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_customer_card, container, false);
    }

    @Override
    protected void initComponents(View view) {
        frameLayout = findViewById(R.id.frameLayout);
        txtRiaCustomerNumber = findViewById(R.id.txtRiaCustomerNumber);
        imgBarCode = findViewById(R.id.imgBarCode);
    }

    @Override
    protected void setListeners() {
        txtRiaCustomerNumber.setText(sessionManager.getUserName());

        ViewTreeObserver viewTreeObserver = frameLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    try {
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        BitMatrix bitMatrix = multiFormatWriter.encode(sessionManager.getUserName(), BarcodeFormat.CODE_128, frameLayout.getWidth(), 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        imgBarCode.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}