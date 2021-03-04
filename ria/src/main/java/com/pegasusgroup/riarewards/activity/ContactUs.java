package com.pegasusgroup.riarewards.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContactUs extends BaseAppCompatActivity {

    private AppCompatButton cmdSubmit;

    private TextInputEditText edtHelp;
    private TextInputEditText edtName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_contact_us;
    }

    @Override
    protected void initComponents() {
        cmdSubmit = findViewById(R.id.cmdSubmit);
        edtHelp = findViewById(R.id.edtHelp);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMessage = findViewById(R.id.edtMessage);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        edtName.setText(sessionManager.getFirstName() + " " + sessionManager.getLastName());
        edtEmail.setText(sessionManager.getEmail());
        edtMessage.requestFocus();

        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (!getText(edtHelp).isEmpty()) {
                if (!getText(edtName).isEmpty()) {
                    if (!getText(edtEmail).isEmpty()) {
                        if (!getText(edtMessage).isEmpty()) {
                            callContactUsApi();
                        } else {
                            edtHelp.setError(getResources().getString(R.string.blank_error));
                        }
                    } else {
                        edtHelp.setError(getResources().getString(R.string.blank_error));
                    }
                } else {
                    edtHelp.setError(getResources().getString(R.string.blank_error));
                }
//                } else {
//                    edtHelp.setError(getResources().getString(R.string.blank_error));
//                }

//                startNextActivity(ContactUs.this, ThankYou.class, new Bundle());
//                finish();
            }
        });
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callContactUsApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            CommonMethods.printLog("URL : " + AppConstants.CONTACT_US);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.CONTACT_US, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String code = jsonObject.getString("status");
                                if (code.equals("200")) {
                                    Toast.makeText(ContactUs.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    startNextActivity(ContactUs.this, ThankYou.class, new Bundle());
                                    finish();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", getText(edtName));
                    params.put("email", getText(edtEmail));
                    params.put("subject", getText(edtHelp));
                    params.put("message", getText(edtMessage));
//                    params.put("page", "");
                    CommonMethods.printLog("ContactUs Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}