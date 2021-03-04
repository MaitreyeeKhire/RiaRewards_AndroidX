package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerificationCode extends BaseAppCompatActivity implements TextWatcher {

    private AppCompatEditText editTextOne, editTextTwo, editTextThree, editTextFour, editTextFive, editTextSix;
    private AppCompatTextView txtResend;

    private AppCompatTextView txtMobile;
    private AppCompatButton cmdVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_verification_code;
    }

    @Override
    protected void initComponents() {
        txtMobile = findViewById(R.id.txtMobile);
        cmdVerificationCode = findViewById(R.id.cmdVerificationCode);
        String mobile = Objects.requireNonNull(getIntent().getExtras()).getString("mobile");
        hideNumber(Objects.requireNonNull(mobile));

        txtResend = findViewById(R.id.txtResend);
        editTextOne = findViewById(R.id.editText1);
        editTextTwo = findViewById(R.id.editText2);
        editTextThree = findViewById(R.id.editText3);
        editTextFour = findViewById(R.id.editText4);
        editTextFive = findViewById(R.id.editText5);
        editTextSix = findViewById(R.id.editText6);
    }

    @Override
    protected void setListeners() {
        imgBack.setVisibility(View.INVISIBLE);
        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtpApi();
            }
        });

        cmdVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtpApi();
            }
        });

        editTextOne.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextOne.setTransformationMethod(new MyPasswordTransformationMethod());
        editTextTwo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextTwo.setTransformationMethod(new MyPasswordTransformationMethod());
        editTextThree.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextThree.setTransformationMethod(new MyPasswordTransformationMethod());
        editTextFour.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextFour.setTransformationMethod(new MyPasswordTransformationMethod());
        editTextFive.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextFive.setTransformationMethod(new MyPasswordTransformationMethod());
        editTextSix.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editTextSix.setTransformationMethod(new MyPasswordTransformationMethod());

        editTextOne.addTextChangedListener(this);
        editTextTwo.addTextChangedListener(this);
        editTextThree.addTextChangedListener(this);
        editTextFour.addTextChangedListener(this);
        editTextFive.addTextChangedListener(this);

        editTextSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence == editTextFive.getEditableText() && after != 0) {
                    editTextSix.setSelection(Objects.requireNonNull(editTextSix.getText()).length());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextSix.setOnKeyListener(new View.OnKeyListener() {
                    @Override

                    public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                        String six = Objects.requireNonNull(editTextSix.getText()).toString().trim();

                        if (keycode == KeyEvent.KEYCODE_DEL && six.trim().length() == 0) {
                            editTextFive.requestFocus();
                        }
                        return false;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAllFieldsFilled()) {
                    hideSoftKeyboard();
                    cmdVerificationCode.performClick();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hidePointView();
    }

    @SuppressLint("SetTextI18n")
    private void hideNumber(String mobile) {
        if (mobile.startsWith("0")) {
            mobile = mobile.substring(1);
        }
        String first = "+61" + mobile.substring(0, 3);
        String middle = "*****";
        String last = mobile.substring(mobile.length() - 2);
        txtMobile.setText(first + middle + last);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (charSequence == editTextOne.getEditableText() && after != 0) {
            editTextTwo.requestFocus();
        } else if (charSequence == editTextTwo.getEditableText() && after != 0) {
            editTextThree.requestFocus();
        } else if (charSequence == editTextThree.getEditableText() && after != 0) {
            editTextFour.requestFocus();
        } else if (charSequence == editTextFour.getEditableText() && after != 0) {
            editTextFive.requestFocus();
        } else if (charSequence == editTextFive.getEditableText() && after != 0) {
            editTextSix.requestFocus();
        } else if (charSequence == editTextSix.getEditableText() && after != 0) {
            editTextSix.setSelection(Objects.requireNonNull(editTextSix.getText()).length());
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        editTextTwo.setOnKeyListener(new View.OnKeyListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                String two = Objects.requireNonNull(editTextTwo.getText()).toString().trim();

                if (keycode == KeyEvent.KEYCODE_DEL && two.trim().length() == 0) {
                    editTextOne.requestFocus();
                }
                return false;
            }
        });

        editTextThree.setOnKeyListener(new View.OnKeyListener() {
            @Override

            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                String three = Objects.requireNonNull(editTextThree.getText()).toString().trim();

                if (keycode == KeyEvent.KEYCODE_DEL && three.trim().length() == 0) {
                    editTextTwo.requestFocus();
                }
                return false;
            }
        });

        editTextFour.setOnKeyListener(new View.OnKeyListener() {
            @Override

            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                String four = Objects.requireNonNull(editTextFour.getText()).toString().trim();

                if (keycode == KeyEvent.KEYCODE_DEL && four.trim().length() == 0) {
                    editTextThree.requestFocus();
                }
                return false;
            }
        });
        editTextFive.setOnKeyListener(new View.OnKeyListener() {
            @Override

            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                String five = Objects.requireNonNull(editTextFive.getText()).toString().trim();

                if (keycode == KeyEvent.KEYCODE_DEL && five.trim().length() == 0) {
                    editTextFour.requestFocus();
                }
                return false;
            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isAllFieldsFilled() {
        return (!getText(editTextOne).isEmpty() && !getText(editTextTwo).isEmpty() && !getText(editTextThree).isEmpty() && !getText(editTextFour).isEmpty()
                && !getText(editTextFive).isEmpty() && !getText(editTextSix).isEmpty());
    }

    /**
     * @description This method is used to call resend OTP Api
     */
    private void verifyOtpApi() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

            final String otp = getText(editTextOne) + getText(editTextTwo) + getText(editTextThree)
                    + getText(editTextFour) + getText(editTextFive) + getText(editTextSix);

            CommonMethods.printLog(AppConstants.VERIFY_OTP);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.VERIFY_OTP, new Response.Listener<String>() {
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
                                showToast("OTP Verified successfully");
                                startNextActivity(VerificationCode.this, Register2.class);
                                putDetails(sessionManager.getClientId(), sessionManager.getUserId(), "android", androidId, "click", "",
                                        "", "", "", "TempPinActivation", reportDate, "Success");
                            } else {
                                if (status.startsWith("4")) {
                                    showToast("Invalid Parameters");
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
                    params.put("user_id", sessionManager.getUserId());
                    params.put("otp", otp);
                    CommonMethods.printLog("Verify OTP Params : " + params);
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call resend OTP Api
     */
    private void resendOtpApi() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.RESEND_OTP, new Response.Listener<String>() {
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
                                showToast("OTP resend successfully");
                                //startNextActivity(VerificationCode.this, Register2.class);
                            } else {
                                if (status.startsWith("4")) {
                                    showToast("Invalid Parameters");
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
                    params.put("user_id", sessionManager.getUserId());
                    params.put("platform", "android");
                    params.put("dob", "");
                    params.put("address", "");
                    params.put("c_number", "");
                    params.put("gender", "");
                    params.put("newsletter", "");
                    params.put("terms", "");
                    params.put("password", "");
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }
}