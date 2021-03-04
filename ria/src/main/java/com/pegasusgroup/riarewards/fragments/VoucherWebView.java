package com.pegasusgroup.riarewards.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class VoucherWebView extends BaseFragment {

    private AppCompatButton cmdMerchantRedeem;
    private WebView webView;
    private String pid;
    private String voucherAPI;

    public VoucherWebView() {
        // Required empty public constructor
    }

    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_voucher_web_view, container, false);
    }

    @Override
    protected void initComponents(View view) {
        webView = findViewById(R.id.webView);
        cmdMerchantRedeem = findViewById(R.id.cmdMerchantRedeem);
        pid = Objects.requireNonNull(getArguments()).getString("pid", "");
        voucherAPI = getArguments().getString("voucherAPI", "");
    }

    @Override
    protected void setListeners() {
        getOfferDetail(voucherAPI);
        cmdMerchantRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemed();
            }
        });
    }

    private void getOfferDetail(String url) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject status = obj.getJSONObject("status");
                            if (status.getString("200").equalsIgnoreCase("success")) {
                                webView.loadDataWithBaseURL(null, obj.getString("voucher"), "text/html", "utf-8", null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Access the RequestQueue through your singleton class.
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    private void redeemed() {
        String url = AppConstants.REDEEM;
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showAlert();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("cid", sessionManager.getClientId());
                params.put("lat", "0.00");
                params.put("lon", "0.00");
                params.put("pid", pid);
                params.put("user_id", sessionManager.getUserId());
                params.put("response_type", "json");
                return params;
            }

            @Override
            public java.util.Map<String, String> getHeaders() {
                return new HashMap<>();
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        // Access the RequestQueue through your singleton class.
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    @SuppressWarnings("deprecation")
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                mContext).create();
        alertDialog.setMessage("Success");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(Color.parseColor("#FFFF0400"));
    }
}