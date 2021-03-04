package com.pegasusgroup.riarewards.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

public class ForgotPassword extends BaseAppCompatActivity {

    private TextInputEditText edtUserName;
    //    private TextInputEditText edtEmail;
    private AppCompatButton cmdReset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initComponents() {
        edtUserName = findViewById(R.id.edtUserName);
//        edtEmail = findViewById(R.id.edtEmail);
        cmdReset = findViewById(R.id.cmdResetPassword);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidePointView();
    }

    @Override
    protected void setListeners() {
        imgBack.setVisibility(View.INVISIBLE);
        cmdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getText(edtUserName).isEmpty()) {
                    callForgotPassword();
                } else {
                    showToast("Please Enter Username");
                }
            }
        });
    }

    /**
     * @description This method is used to call Forgot Password api
     */
    private void callForgotPassword() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.FORGOT_PASSWORD, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("forgot password response : " + response);
                        jsonObject = new JSONObject(response);
                        if (jsonObject.has("status")) {
                            String status = jsonObject.getString("status");
                            if (status.equals("200")) {
                                if (jsonObject.has("email")) {
                                    showLongToast("Reset password email has been sent to " + jsonObject.getString("email"));
                                } else {
                                    showLongToast("Reset password email has been sent to " + getText(edtUserName) + "'s registered email");
                                }
                                startActivity(new Intent(ForgotPassword.this, Login.class));
                                overridePendingTransition(0, 0);
                                finish();
                            } else {
                                if (status.startsWith("4")) {
                                    showToast("Invalid Parameters");
                                } else if (status.startsWith("5")) {
                                    showToast("User not found");
                                }
                            }
                        } else {
                            showToast("Invalid Response");
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
                    params.put("username", getText(edtUserName));
                    return params;
                }
            };

            requestQueue.add(otpReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}