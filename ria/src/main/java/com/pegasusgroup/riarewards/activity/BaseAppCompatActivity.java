package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.fragments.Home;
import com.pegasusgroup.riarewards.fragments.PointHistory;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.API_ENDPOINT;

@SuppressWarnings("unused")
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    private static int INTENT_AUTHENTICATE = 9876;
    private static boolean isAllow = false;
    public ProgressDialog progressDialog;
    public Toolbar toolbar;
    public SessionManager sessionManager;
    public String androidId;
    public String reportDate;
    public Date currentTime;
    public SimpleDateFormat dateFormat;
    public LinearLayout llPointsBalanceWithImage;
    public AppCompatImageView imgBack;
    public boolean displayBackImage = false;
    private boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout rtlPointsBalance;
    private AppCompatTextView txtSeeMore;
    private int points_earned = 0;
    private int points_spent = 0;
    private AppCompatTextView edtPointBalance;
    private AppCompatTextView edtSumPointBalance;
    private AppCompatTextView edtPointsEarned;
    private AppCompatTextView edtPointsSpent;
    private AppCompatTextView txtUserName;
    private AppCompatImageView imgProfile;
    private boolean callPointsApi = true;

    public static void setBackGround(boolean backGround) {
        isAllow = backGround;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        initializeGenericVariables();
        setSupportActionBar(toolbar);
        initComponents();
        setTitle("");
        setListeners();
        rtlPointsBalance = findViewById(R.id.rtlPointsBalance);
        llPointsBalanceWithImage = findViewById(R.id.llPointsBalanceWithImage);
        txtSeeMore = findViewById(R.id.txtSeeMore);
        edtPointBalance = findViewById(R.id.edtPointBalance);
        edtSumPointBalance = findViewById(R.id.edtSumPointBalance);
        edtPointsEarned = findViewById(R.id.edtPointsEarned);
        edtPointsSpent = findViewById(R.id.edtPointsSpent);
        imgProfile = findViewById(R.id.imgPointProfile);
        txtUserName = findViewById(R.id.txtUserName);
        pointsView();
        toolbar.setTitle("");
        onImageBackPress();
    }

    private void onImageBackPress() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1)
                        .getName()).equalsIgnoreCase("Payment")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseAppCompatActivity.this);
                    builder.setMessage("Sure to cancel Payment ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    updateCartOrderFails();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button negative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    negative.setTextColor(Color.parseColor("#FFFF0400"));
                    positive.setTextColor(Color.parseColor("#FFFF0400"));
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    loadFragment(new Home());
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void pointsView() {
        rtlPointsBalance.bringToFront();

        rtlPointsBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.expand(llPointsBalanceWithImage);
//                rtlPointsBalanceWithImage.setVisibility(View.VISIBLE);
                rtlPointsBalance.setVisibility(View.GONE);
            }
        });

        llPointsBalanceWithImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethods.collapse(llPointsBalanceWithImage);
//                rtlPointsBalanceWithImage.setVisibility(View.GONE);
                rtlPointsBalance.setVisibility(View.VISIBLE);
            }
        });

        txtSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llPointsBalanceWithImage.performClick();
                try {
                    //startNextActivity(BaseAppCompatActivity.this, PointHistory.class);
                    getSupportFragmentManager().popBackStack();
                    imgBack.setVisibility(View.VISIBLE);
                    PointHistory pointHistory = new PointHistory();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, pointHistory)
                            .addToBackStack(pointHistory.getClass().getSimpleName())
                            .commitAllowingStateLoss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        txtUserName.setText(sessionManager.getClientName());
        edtPointBalance.setText(sessionManager.getUserPoint() + " Points");
        edtSumPointBalance.setText(sessionManager.getUserPoint() + " Points");
    }

    public void hidePointView() {
        callPointsApi = false;
        rtlPointsBalance.setVisibility(View.GONE);
        llPointsBalanceWithImage.setVisibility(View.GONE);
    }

    @SuppressLint({"HardwareIds", "SimpleDateFormat"})
    private void initializeGenericVariables() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        toolbar = findViewById(R.id.toolbar);
        imgBack = findViewById(R.id.imgBack);
        sessionManager = new SessionManager(this);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        currentTime = Calendar.getInstance().getTime();
        reportDate = dateFormat.format(currentTime);
    }

    protected abstract int getLayoutResource();

    protected abstract void initComponents();

    protected abstract void setListeners();

    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManager.getUserName().isEmpty() && !sessionManager.getPassword().isEmpty()) {
            // Display Passcode or Patter Lock Screen
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if (!isAllow) {
                    if (Objects.requireNonNull(km).isKeyguardSecure()) {
                        Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
                        startActivityForResult(authIntent, INTENT_AUTHENTICATE);
                    }
                }
            }
        }

        // CommonMethods.printLog("callPointsApi " + callPointsApi);

        if (callPointsApi) {
            pointApi();
        }

        if (sessionManager.getProfileImage().isEmpty()) {
            if (callPointsApi)
                callGetProfileImage();
        } else {
            Glide.with(BaseAppCompatActivity.this)
                    .load(sessionManager.getProfileImage())
                    .asBitmap()
                    .centerCrop()
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                    .into(new BitmapImageViewTarget(imgProfile) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(BaseAppCompatActivity.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imgProfile.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                //do something you want when pass the security
                isAllow = true;
                Window window = this.getWindow();
                window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callGetProfileImage() {
        try {
//            progressDialog.setMessage("Please Wait...");
//            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // CommonMethods.printLog("URL : " + AppConstants.GET_IMAGE);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.GET_IMAGE, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        // CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String code = jsonObject.getString("status");
                                if (code.equals("200")) {
                                    Glide.with(BaseAppCompatActivity.this)
                                            .load(jsonObject.getString("user_image"))
                                            .asBitmap()
                                            .centerCrop()
                                            .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                                            .into(new BitmapImageViewTarget(imgProfile) {
                                                @Override
                                                protected void setResource(Bitmap resource) {
                                                    RoundedBitmapDrawable circularBitmapDrawable =
                                                            RoundedBitmapDrawableFactory.create(BaseAppCompatActivity.this.getResources(), resource);
                                                    circularBitmapDrawable.setCircular(true);
                                                    imgProfile.setImageDrawable(circularBitmapDrawable);
                                                    try {
                                                        sessionManager.setProfileImage(jsonObject.getString("user_image"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                }
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
                protected java.util.Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    // CommonMethods.printLog("Get Profile Image Params : " + params);
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
     * @description This method is used to call resend OTP Api
     */
    public void pointApi() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            progressDialog.setMessage("Please Wait...");
//            progressDialog.show();

//            CommonMethods.printLog(AppConstants.POINT_BRIEF + sessionManager.getUserId());

            StringRequest otpReq = new StringRequest(Request.Method.GET, AppConstants.POINT_BRIEF + sessionManager.getUserId(), new Response.Listener<String>() {
                JSONObject jsonObject;

                @SuppressWarnings("ConstantConditions")
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {
                    try {
//                        CommonMethods.printLog("point brief response : " + response);
                        jsonObject = new JSONObject(response);
                        if (jsonObject.has("status")) {
                            String status = jsonObject.getString("status");
                            if (status.equals("200")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                points_earned = 0;
                                points_spent = 0;
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject singlePoint = dataArray.getJSONObject(i);
                                    if (singlePoint.getString("points_earned") != null && !singlePoint.getString("points_earned").equals("null"))
                                        points_earned += Integer.parseInt(singlePoint.getString("points_earned"));

                                    if (singlePoint.getString("points_spent") != null && !singlePoint.getString("points_spent").equals("null"))
                                        points_spent += Integer.parseInt(singlePoint.getString("points_spent"));
                                }
                                edtPointsEarned.setText(String.valueOf(points_earned));
                                edtPointsSpent.setText(String.valueOf(points_spent));
                                edtPointBalance.setText((points_earned + points_spent) + " Points");
                                edtSumPointBalance.setText(points_earned + points_spent + " Points");
                                sessionManager.setUserPoint(String.valueOf(points_earned + points_spent));

//                                Glide.with(BaseAppCompatActivity.this).load(R.drawable.profile_image).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgProfile) {
//                                    @Override
//                                    protected void setResource(Bitmap resource) {
//                                        RoundedBitmapDrawable circularBitmapDrawable =
//                                                RoundedBitmapDrawableFactory.create(BaseAppCompatActivity.this.getResources(), resource);
//                                        circularBitmapDrawable.setCircular(true);
//                                        imgProfile.setImageDrawable(circularBitmapDrawable);
//                                    }
//                                });
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
                    }
//                    finally {
//                        progressDialog.dismiss();
//                    }
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
                    params.put("user_id", sessionManager.getUserId());
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param cid          CID
     * @param uid          UID
     * @param platform     PLATFORM
     * @param device_id    DEVICE_ID
     * @param category_id  CATEGORY_ID
     * @param categoryName CATEGORY_NAME
     * @param product_id   PRODUCT_ID
     * @param productName  PRODUCT_NAME
     * @param page         PAGE
     * @param date_time    DATE_TIME
     * @param status       STATUS
     * @param message      MESSAGE
     * @description Analytic API
     */
    public void putDetails(final String cid, final String uid, final String platform, final String device_id, final String category_id,
                           final String categoryName, final String product_id, final String productName, final String page,
                           final String date_time, final String status, final String message) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("cid", cid);
            jsonObject.put("uid", uid);
            jsonObject.put("platform", platform);
            jsonObject.put("device_id", device_id);

            JSONObject actionObj = new JSONObject();
            actionObj.put("category_id", category_id);
            actionObj.put("category_name", categoryName);
            actionObj.put("product_id", product_id);
            actionObj.put("product_name", productName);
            actionObj.put("page", page);
            actionObj.put("datetime", date_time);
            actionObj.put("status", status);
            actionObj.put("message", message);

            jsonObject.put("action", actionObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, AppConstants.USER_ACTION, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PutDetails", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("PutDetails", "");
                    }
                });
        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    /***
     * @description This method is used to navigate to next activity
     * @param packageContext First Activity or Current Context
     * @param cls Next Activity
     */
    public void startNextActivity(Context packageContext, Class<?> cls) {
        startNextActivity(packageContext, cls, new Bundle());
    }

    /***
     * @description This method is used to navigate to next activity
     * @param packageContext First Activity or Current Context
     * @param cls Next Activity
     */
    public void startNextActivity(Context packageContext, Class<?> cls, Bundle bundle) {
//        llPointsBalanceWithImage.performClick();
        startActivity(new Intent(packageContext, cls).putExtras(bundle));
        overridePendingTransition(0, 0);
    }

    /**
     * @param message Message
     * @description This message is use to Show Toast Message
     */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param message Message
     * @description This message is use to Show Toast Message
     */
    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * @param email Email Address
     * @return True or False
     * @description This method is used to validate Email Address
     */
    public boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * @param appCompatEditText Name of the EditText
     * @return String after trimming
     * @description This method returns EditText Box's text
     */
    public String getText(AppCompatTextView appCompatEditText) {
        return Objects.requireNonNull(appCompatEditText.getText()).toString().trim().length() > 0
                ? appCompatEditText.getText().toString().trim() : "";
    }

    /**
     * @param appCompatEditText Name of the EditText
     * @return String after trimming
     * @description This method returns EditText Box's text
     */
    public String getText(AppCompatEditText appCompatEditText) {
        return Objects.requireNonNull(appCompatEditText.getText()).toString().trim().length() > 0
                ? appCompatEditText.getText().toString().trim() : "";
    }

    /**
     * @param appCompatEditText EditText
     * @return false if EditText is not blank , else true.
     * @description This method is going to check if the EditText is blank or not ?
     */
    public boolean blankEditText(AppCompatEditText appCompatEditText) {
        if (Objects.requireNonNull(appCompatEditText.getText()).toString().trim().length() == 0) {
            appCompatEditText.setError(getString(R.string.blankError));
            appCompatEditText.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        assert inputMethodManager != null;
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * @param password Password
     * @return True or False
     * @description This method is used to verify entered password
     */
    public boolean isValidPassword(String password) {
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (password.length() < 8) {
            showToast("Password length must have at least 8 character !!");
            return false;
        }
        if (!specialCharPatten.matcher(password).find()) {
            showToast("Password must have at least one special character !!");
            return false;
        }
        if (!UpperCasePatten.matcher(password).find()) {
            showToast("Password must have at least one uppercase character !!\"");
            return false;
        }
        if (!lowerCasePatten.matcher(password).find()) {
            showToast("Password must have at least one lowercase character !!");
            return false;
        }
        if (!digitCasePatten.matcher(password).find()) {
            showToast("Password must have at least one digit character !!");
            return false;
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        if (this instanceof Home) {
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
////            android.os.Process.killProcess(android.os.Process.myPid());
//            } else if (llPointsBalanceWithImage.getVisibility() == View.VISIBLE) {
//                llPointsBalanceWithImage.performClick();
//            } else {
//                this.doubleBackToExitPressedOnce = true;
//                Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();
//
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
//            }
//        } else if (llPointsBalanceWithImage.getVisibility() == View.VISIBLE) {
//            llPointsBalanceWithImage.performClick();
//        } else {
//            super.onBackPressed();
//            overridePendingTransition(0, 0);
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        CommonMethods.printLog("Count : " + getSupportFragmentManager().getBackStackEntryCount());
//        try {
//            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                if (!Objects.requireNonNull(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager()
//                        .getBackStackEntryCount() - 1).getName()).equalsIgnoreCase("Home"))
//                    getSupportFragmentManager().popBackStack();
//                else
//                    super.onBackPressed();
//            } else {
//                imgBack.setVisibility(View.INVISIBLE);
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.main_container, new com.pegasusgroup.riarewards.fragments.Home())
//                        .commitAllowingStateLoss();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        hideSoftKeyboard();
        this.finish();
        overridePendingTransition(0, 0);
        return true;
    }


    private void updateCartOrderFails() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest postReq = new StringRequest(Request.Method.POST, API_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CommonMethods.printLog("Response:" + response);
                progressDialog.hide();
                loadFragment(new Home());
                imgBack.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("move_to_cart", "1");
                params.put("cart_id", sessionManager.getCartId());
                params.put("uid", sessionManager.getUserId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }
        };

        postReq.setShouldCache(false);
        requestQueue.add(postReq);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        imgBack.setVisibility(View.INVISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commitAllowingStateLoss();
    }
}