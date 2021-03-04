package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.model.AddressMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.CLIENT_ID;

public class AddAddress extends BaseAppCompatActivity {

    private TextInputEditText edtEmail;
    private TextInputEditText edtMobile;
    private TextInputEditText edtAddress1;
    private TextInputEditText edtAddress2;
    private TextInputEditText edtCountry;
    private TextInputEditText edtState;
    private TextInputEditText edtCity;
    private TextInputEditText edtPostCode;

    private AppCompatButton cmdAdd;

    private AddressMd addressMd;
    private String address_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_address;
    }

    @Override
    protected void initComponents() {
        edtEmail = findViewById(R.id.edtEmail);
        edtMobile = findViewById(R.id.edtMobile);
        edtAddress1 = findViewById(R.id.edtAddress1);
        edtAddress2 = findViewById(R.id.edtAddress2);
        edtCountry = findViewById(R.id.edtCountry);
        edtState = findViewById(R.id.edtState);
        edtCity = findViewById(R.id.edtCity);
        edtPostCode = findViewById(R.id.edtPincode);
        cmdAdd = findViewById(R.id.cmdAdd);

        addressMd = (AddressMd) Objects.requireNonNull(getIntent().getExtras()).getSerializable("addressMd");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (addressMd != null) {
            address_id = addressMd.getId();
            edtEmail.setText(addressMd.getEmail() != null ? addressMd.getEmail() : "");
            edtMobile.setText(addressMd.getMobile() != null ? addressMd.getMobile() : "");
            edtAddress1.setText(addressMd.getAddress1() != null ? addressMd.getAddress1() : "");
            edtAddress2.setText(addressMd.getAddress2() != null ? addressMd.getAddress2() : "");
            edtCountry.setText(addressMd.getCountry() != null ? addressMd.getCountry() : "");
            edtState.setText(addressMd.getState() != null ? addressMd.getState() : "");
            edtCity.setText(addressMd.getCity() != null ? addressMd.getCity() : "");
            edtPostCode.setText(addressMd.getZipcode() != null ? addressMd.getZipcode() : "");
            cmdAdd.setText("Update");
        } else {
            cmdAdd.setText("Save");
            address_id = "";
        }

        edtPostCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    cmdAdd.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cmdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getText(edtEmail).isEmpty()) {
                    if (isValidEmail(getText(edtEmail))) {
                        if (!getText(edtMobile).isEmpty()) {
                            if (!getText(edtAddress1).isEmpty()) {
                                if (!getText(edtAddress2).isEmpty()) {
                                    if (!getText(edtCountry).isEmpty()) {
                                        if (!getText(edtState).isEmpty()) {
                                            if (!getText(edtCity).isEmpty()) {
                                                if (!getText(edtPostCode).isEmpty()) {
                                                    callAddAddressApi();
                                                } else
                                                    edtPostCode.setError(getResources().getString(R.string.blank_error));
                                            } else
                                                edtCity.setError(getResources().getString(R.string.blank_error));
                                        } else
                                            edtState.setError(getResources().getString(R.string.blank_error));
                                    } else
                                        edtCountry.setError(getResources().getString(R.string.blank_error));
                                } else
                                    edtAddress2.setError(getResources().getString(R.string.blank_error));
                            } else
                                edtAddress1.setError(getResources().getString(R.string.blank_error));
                        } else
                            edtMobile.setError(getResources().getString(R.string.blank_error));
                    } else {
                        showToast("Please enter valid email");
                        edtEmail.requestFocus();
                    }
                } else
                    edtEmail.setError(getResources().getString(R.string.blank_error));
            }
        });
    }

    /**
     * @description This method is used to call API
     */
    private void callAddAddressApi() {
        try {
            String url = "https://www.myrewards.com.au/newapp/" + "user_shipping_details.php?"
                    + "user_id=" + sessionManager.getUserId()
                    + "&client_id=" + CLIENT_ID
                    + "&email=" + getText(edtEmail)
                    + "&mobile=" + getText(edtMobile)
                    + "&address1=" + getText(edtAddress1)
                    + "&address2=" + getText(edtAddress2)
                    + "&city=" + getText(edtCity)
                    + "&state=" + getText(edtState)
                    + "&pincode=" + getText(edtPostCode)
                    + "&country=" + getText(edtCountry)
                    + "&name=" + sessionManager.getFirstName()
                    + "&suburb=" + ""
                    + "&address_id=" + address_id;

            url = url.replaceAll(" ", "%20");
            CommonMethods.printLog(url);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);
                            //{"response":"success","address_id":"21618"}
                            if (jsonObject.getString("response").equals("success")) {
                                String address = sessionManager.getFirstName() + " " + sessionManager.getLastName() + " \n" + getText(edtAddress1) + "," + getText(edtAddress2)
                                        + ",\n" + getText(edtCity) + "," + getText(edtState) + "," + getText(edtCountry) + "-" + getText(edtPostCode)
                                        + "\nMobile : " + getText(edtMobile);
                                sessionManager.setShippingAddress(address);
                                onBackPressed();
                            } else {
                                showToast(jsonObject.getString("response"));
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
                    return new HashMap<>();
                }
            };

            requestQueue.add(otpReq);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }
}