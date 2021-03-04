package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register2 extends BaseAppCompatActivity {

    private DatePickerDialog datePickerDialog;

    private AppCompatTextView txtBirthDate;
    private AppCompatTextView txtTermsConditions;
    private TextInputEditText edtRiaCustomerNumber;
    private TextInputEditText edtPassword;
    private TextInputEditText edtConfirmPassword;

    private AppCompatRadioButton optNewsLetterYes;
    private AppCompatRadioButton optNewsLetterNo;
    private AppCompatRadioButton optMale;
    private AppCompatRadioButton optFemale;
    private AppCompatRadioButton optOther;
    private AppCompatRadioButton optAccept;

    private AppCompatButton cmdSubmit;

    private String newsLetter;
    private String gender;
    private String terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_register2;
    }

    @SuppressLint("NewApi,SetTextI18n")
    @Override
    protected void initComponents() {
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtTermsConditions = findViewById(R.id.txtTermsConditions);
        edtRiaCustomerNumber = findViewById(R.id.edtRiaCustomerNumber);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        optNewsLetterYes = findViewById(R.id.optNewsLetterYes);
        optNewsLetterNo = findViewById(R.id.optNewsLetterNo);
        optMale = findViewById(R.id.optMale);
        optFemale = findViewById(R.id.optFemale);
        optOther = findViewById(R.id.optOther);
        optAccept = findViewById(R.id.optAccept);

        cmdSubmit = findViewById(R.id.cmdSubmit);

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtBirthDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
    }

    @SuppressLint("NewApi")
    @Override
    protected void setListeners() {
        imgBack.setVisibility(View.INVISIBLE);
        txtTermsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextActivity(Register2.this, TermsConditions.class);
            }
        });

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        optNewsLetterYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsLetter = "1";
            }
        });

        optNewsLetterNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsLetter = "0";
            }
        });

        optMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "1";
            }
        });

        optFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "2";
            }
        });

        optOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "0";
            }
        });

        optAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms = "1";
            }
        });

        if (optNewsLetterYes.isChecked()) {
            newsLetter = "1";
        } else if (optNewsLetterNo.isChecked()) {
            newsLetter = "0";
        }

        if (optMale.isChecked()) {
            gender = "1";
        } else if (optFemale.isChecked()) {
            gender = "2";
        } else if (optOther.isChecked()) {
            gender = "0";
        }

        if (optAccept.isChecked()) {
            terms = "1";
        } else {
            terms = "0";
        }

        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getText(txtBirthDate).isEmpty()) {
                    if (!getText(edtRiaCustomerNumber).isEmpty()) {
                        if (getText(edtPassword).equals(getText(edtConfirmPassword))) {
                            if (getText(edtPassword).length() >= 8) {
                                if (isValidPassword(getText(edtPassword)))
                                    callRegisterApi();
                                else
                                    showToast("Password should contain Uppercase, Lowercase & Number");
                            } else {
                                showToast("Password should be atleast 8 character long");
                            }
                        } else {
                            showToast("Password and Confirm Password doesn't match");
                        }
                    } else {
                        edtRiaCustomerNumber.setError("Please enter Ria Customer Number");
                    }
                } else {
                    showToast("Please enter Birth Date");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidePointView();
    }

    private boolean checkString(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
        }
        return numberFlag && capitalFlag && lowerCaseFlag;
    }

    /**
     * @description This method is used to call Api
     */
    private void callRegisterApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            CommonMethods.printLog("URL : " + AppConstants.USER_REGISTER);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.USER_REGISTER, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("200")) {
                                    sessionManager.setUserName(getText(edtRiaCustomerNumber));
                                    sessionManager.setPassword(getText(edtPassword));
                                    sessionManager.setUserId(jsonObject.getString("user_id"));
                                    if (jsonObject.has("client_id"))
                                        sessionManager.setClientId(jsonObject.getString("client_id"));
                                    if (jsonObject.has("client_name"))
                                        sessionManager.setClientName(jsonObject.getString("client_name"));
                                    showToast(jsonObject.getString("msg"));
                                    //startNextActivity(Register2.this, Home.class);
                                    navigateToHome();
                                } else {
                                    showToast(jsonObject.getString("msg"));
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
                protected java.util.Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    params.put("platform", "android");
                    params.put("dob", getText(txtBirthDate));
                    params.put("address", "");
                    params.put("c_number", getText(edtRiaCustomerNumber));
                    params.put("gender", gender);
                    params.put("newsletter", newsLetter);
                    params.put("terms", terms);
                    params.put("password", getText(edtPassword));
                    CommonMethods.printLog("Register Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to navigate to Home Screen.
     */
    private void navigateToHome() {
        try {
            startActivity(new Intent(Register2.this, Home.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}