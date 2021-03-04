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

public class Register1 extends BaseAppCompatActivity {

    private TextInputEditText edtFirstName;
    private TextInputEditText edtLastName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtMobileNumber;
    private AppCompatButton cmdVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_register1;
    }

    @Override
    protected void initComponents() {
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtMobileNumber = findViewById(R.id.edtMobileNumber);
        cmdVerificationCode = findViewById(R.id.cmdVerificationCode);
    }

    @Override
    protected void setListeners() {
        imgBack.setVisibility(View.INVISIBLE);
        cmdVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!getText(edtFirstName).isEmpty()) {
                    if (!getText(edtLastName).isEmpty()) {
                        if (!getText(edtEmail).isEmpty()) {
                            if (!getText(edtMobileNumber).isEmpty() && getText(edtMobileNumber).length() >= 9) {
                                requestOTP(getText(edtFirstName), getText(edtLastName), getText(edtEmail), getText(edtMobileNumber));
                            } else {
                                edtMobileNumber.setError("Please enter valid mobile number");
                            }
                        } else {
                            edtEmail.setError("Please enter email");
                        }
                    } else {
                        edtLastName.setError("Please enter last name");
                    }
                } else {
                    edtFirstName.setError("Please enter first name");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidePointView();
    }

    /**
     * @param firstName First Name
     * @param lastName  Last Name
     * @param email     Email
     * @param mobile    Mobile
     * @description This method is used to call Generate OTP api
     */
    private void requestOTP(final String firstName, final String lastName, final String email, final String mobile) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

            CommonMethods.printLog("URL : " + AppConstants.GENERATE_OTP);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.GENERATE_OTP, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        jsonObject = new JSONObject(response);
                        if (jsonObject.has("status")) {
                            String status = jsonObject.getString("status");
                            if (status.equals("200")) {

                                // Store in SharedPreference
                                Bundle bundle = new Bundle();
                                bundle.putString("mobile", getText(edtMobileNumber));
                                bundle.putString("name", getText(edtFirstName));
                                bundle.putString("email", getText(edtEmail));

                                String userId = jsonObject.getString("user_id");
                                sessionManager.setUserId(userId);
                                sessionManager.setEmail(email);
                                sessionManager.setFirstName(firstName);
                                sessionManager.setLastName(lastName);
                                sessionManager.setMobile(mobile);

                                startNextActivity(Register1.this, VerificationCode.class, bundle);
                                overridePendingTransition(0, 0);
                            } else {
                                if (status.startsWith("4")) {
                                    Toast.makeText(Register1.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(Register1.this, "Invalid Response", Toast.LENGTH_SHORT).show();
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
                    params.put("first_name", firstName);
                    params.put("last_name", lastName);
                    params.put("email", email);
                    params.put("mobile", mobile);
                    params.put("platform", "android");
                    CommonMethods.printLog("Generate OTP Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}