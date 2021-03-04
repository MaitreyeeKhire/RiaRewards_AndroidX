package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.activity.BaseAppCompatActivity;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.interfaces.FragmentReplacer;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public abstract class BaseFragment extends Fragment {

    public ProgressDialog progressDialog;
    public Context mContext;
    public BaseAppCompatActivity baseAppCompatActivity;
    public SessionManager sessionManager;
    String androidId;
    String reportDate;
    FragmentChanger fragmentChanger;
    FragmentReplacer fragmentReplacer;
    ViewGroup container;
    private Date currentTime;
    private SimpleDateFormat dateFormat;
    private View view;

    protected abstract View getLayoutResource();

    protected abstract void initComponents(View view);

    protected abstract void setListeners();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.container = container;
        mContext = getActivity();
        baseAppCompatActivity = (BaseAppCompatActivity) getActivity();
        fragmentChanger = (FragmentChanger) getActivity();
        fragmentReplacer = (FragmentReplacer) getActivity();
        return getLayoutResource();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initializeGenericVariables();
        initComponents(view);
        setListeners();
    }

    @SuppressLint({"HardwareIds", "SimpleDateFormat"})
    private void initializeGenericVariables() {
        sessionManager = new SessionManager(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        currentTime = Calendar.getInstance().getTime();
        reportDate = dateFormat.format(currentTime);
        if (this instanceof Home) {
            baseAppCompatActivity.imgBack.setVisibility(View.INVISIBLE);
        } else {
            baseAppCompatActivity.imgBack.setVisibility(View.VISIBLE);
        }
    }

    public <T extends View> T findViewById(int id) {
        return view.findViewById(id);
    }

    /**
     * @param message Message
     * @description This message is use to Show Toast Message
     */
    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param message Message
     * @description This message is use to Show Toast Message
     */
    @SuppressWarnings("WeakerAccess")
    public void showLongToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
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
    void putDetails(final String cid, final String uid, final String platform, final String device_id, final String category_id,
                    final String categoryName, final String product_id, final String productName, final String page, final String date_time,
                    final String status, final String message) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
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

    /**
     * Hides the soft keyboard
     */
    void hideSoftKeyboard() {
        if (Objects.requireNonNull(getActivity()).getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
        }
    }

    /**
     * @param email Email Address
     * @return True or False
     * @description This method is used to validate Email Address
     */
    boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    @Override
    public void onResume() {
        super.onResume();
        baseAppCompatActivity.pointApi();
    }
}