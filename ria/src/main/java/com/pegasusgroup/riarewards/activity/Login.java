package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.CLIENT_ID;

public class Login extends BaseAppCompatActivity {

    private AppCompatButton cmdSignIn;
    private AppCompatButton cmdFirstTimeLogin;
    private AppCompatButton cmdForgotPassword;

    private TextInputEditText edtUserName;
    private TextInputEditText edtPassword;
    private AppCompatCheckBox chkRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void initComponents() {
        toolbar = findViewById(R.id.toolbar);

        edtUserName = findViewById(R.id.edtMemberId);
        edtPassword = findViewById(R.id.edtPassword);
        chkRememberMe = findViewById(R.id.chkRememberMe);

        cmdSignIn = findViewById(R.id.cmdSignIn);
        cmdFirstTimeLogin = findViewById(R.id.cmdFirstTimeLogin);
        cmdForgotPassword = findViewById(R.id.cmdForgotPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        hidePointView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidePointView();
    }

    @Override
    protected void setListeners() {
        imgBack.setVisibility(View.INVISIBLE);
//        hidePointView();

        edtUserName.setHintTextColor(getResources().getColor(R.color.white));

        cmdSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startNextActivity(Login.this, Home.class, new Bundle());
//                overridePendingTransition(0, 0);

                if (!getText(edtUserName).isEmpty()) {
                    if (!getText(edtPassword).isEmpty()) {
//                        if (isValidPassword(getText(edtPassword))) {
                        if (CommonMethods.isNetworkAvailable(Login.this)) {
                            callLoginApi();
                        } else {
                            Toast.makeText(Login.this, getResources().getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
                        }
//                        }
                    } else {
                        edtPassword.setError("Please enter password");
                    }
                } else {
                    edtUserName.setError("Please enter username");
                }
            }
        });
        cmdFirstTimeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(Login.this, Register1.class);
                overridePendingTransition(0, 0);
            }
        });
        cmdForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(Login.this, ForgotPassword.class);
                overridePendingTransition(0, 0);
            }
        });
    }

    /**
     * @description This method is used to call Api
     */
    private void callLoginApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            CommonMethods.printLog("URL : " + AppConstants.USER_LOGIN);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.USER_LOGIN, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("status");
                                if (jsonObject1.get("503").equals("Domain not found")) {
                                    showToast("Web address does not exist. Please enter your program web address");
                                } else if (jsonObject1.get("503").equals("User not found")) {
                                    showToast("Username/Membership number does not exist. Please double check your username");
                                } else if (jsonObject1.get("503").equals("Password is incorrect")) {
                                    showToast("Incorrect Password, Please enter correct password");
                                } else {
                                    showToast(jsonObject1.getString("503"));
                                }
                                progressDialog.dismiss();
                            } else {
                                sessionManager.setUserId(jsonObject.getString("user_id"));
                                if (chkRememberMe.isChecked()) {
                                    if (jsonObject.has("user_id") && jsonObject.has("client_id")) {
                                        sessionManager.setUserName(getText(edtUserName));
                                        sessionManager.setPassword(getText(edtPassword));
//                                        sessionManager.setClientName(jsonObject.getString("client_name"));
                                        sessionManager.setClientId(jsonObject.getString("client_id"));
                                    }
                                }
                                callGetUserInfo();
//                                putDetails(jsonObject.getString("client_id"), "", "android", androidId, "", "", "", "", "Login Page", reportDate, "User Logged in", "Login");
//                                navigateToHome();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    params.put("uname", getText(edtUserName));
                    params.put("pwd", getText(edtPassword));
                    params.put("sub", "ria.myrewards.com.au"); // static param
                    params.put("response_type", "json");
                    params.put("page", "");
                    CommonMethods.printLog("Login Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call Api
     */
    private void callGetCardItemCount() {
        String countURL = AppConstants.CART_ITEMS + "uid=" + sessionManager.getUserId() + "&cart_id=" + sessionManager.getCartId();
        CommonMethods.printLog("countURL " + countURL);
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        StringRequest postReq = new StringRequest(Request.Method.GET, countURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CommonMethods.printLog("countURL response : " + response);
                //{"status":{"200":"success"},"cart_items":"1"}
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.contains("200")) {
                            sessionManager.setCartCount(jsonObject.getString("cart_items"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
            }

        });

        requestQueue.add(postReq);
    }

    /**
     * @description This method is used to call Set Password Method
     */
    @SuppressLint("SimpleDateFormat")
    private void callGetUserInfo() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String url = "https://www.myrewards.com.au/newapp/get_user1.php?uname=" + sessionManager.getUserId() + "&client_id=" + CLIENT_ID + "&response_type=json";
            CommonMethods.printLog("URL : " + url);

            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            sessionManager.setFirstName(jsonObject.getString("first_name"));
                            sessionManager.setLastName(jsonObject.getString("last_name"));
                            sessionManager.setClientEmail(jsonObject.getString("client_email"));
                            sessionManager.setClientName(jsonObject.getString("client_name"));
                            sessionManager.setMobile(jsonObject.getString("mobile"));
                            sessionManager.setNewsLetter(jsonObject.getString("newsletter"));
                            sessionManager.setGender(jsonObject.getString("gender"));
                            sessionManager.setCartId(jsonObject.getString("cart_id"));
                            sessionManager.setEmail(jsonObject.getString("email"));

                            String dob = jsonObject.getString("dob");
                            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat appFormat = new SimpleDateFormat("dd/MM/yyyy");
                            dob = appFormat.format(serverFormat.parse(dob));
                            sessionManager.setBirthDate(dob);
                            putDetails(jsonObject.getString("client_id"), "", "android", androidId, "", "", "", "", "Login Page", reportDate, "User Logged in", "Login");
                            callGetCardItemCount();
                            navigateToHome();
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
                protected java.util.Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    CommonMethods.printLog("Get UserInfo Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to navigate to Home Screen.
     */
    private void navigateToHome() {
        try {
            startActivity(new Intent(Login.this, Home.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}