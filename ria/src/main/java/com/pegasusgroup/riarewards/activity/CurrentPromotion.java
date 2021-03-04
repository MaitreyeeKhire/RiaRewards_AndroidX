package com.pegasusgroup.riarewards.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.PromotionAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.FacebookShareListener;
import com.pegasusgroup.riarewards.interfaces.PromotionListener;
import com.pegasusgroup.riarewards.model.PromotionMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CurrentPromotion extends BaseAppCompatActivity
        implements FacebookShareListener,
        PromotionListener {

    private RecyclerView recycler_view;
    private ArrayList<PromotionMd> promotionMds;
    private PromotionAdapter promotionAdapter;

    // Facebook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String promotionId;
    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            CommonMethods.printLog("Successfully posted");
            // Write some code to do some operations when you shared content successfully.
            callPromotionClaim(promotionId);
        }

        @Override
        public void onCancel() {
            CommonMethods.printLog("Sharing cancelled");
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            CommonMethods.printLog(error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_current_promotion;
    }

    @Override
    protected void initComponents() {
        // Promotion
        promotionMds = new ArrayList<>();
        recycler_view = findViewById(R.id.recycler_view);
        promotionAdapter = new PromotionAdapter(CurrentPromotion.this, promotionMds);

        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(CurrentPromotion.this);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recycler_view.setAdapter(promotionAdapter);
        callPromotionApi();
        // this part is optional
        shareDialog.registerCallback(callbackManager, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @description This method is used to call Api
     */
    private void callPromotionClaim(final String promotionId) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(CurrentPromotion.this);

            CommonMethods.printLog("URL : " + AppConstants.PROMOTION_CLAIM);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.PROMOTION_CLAIM, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                showToast(jsonObject.getString("msg"));
                                callPromotionApi();
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
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    params.put("internal_claimed", "0");
                    params.put("fb_claimed", "1");
                    params.put("promotion_id", promotionId);
                    CommonMethods.printLog(params.toString());
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callPromotionApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            CommonMethods.printLog("URL : " + AppConstants.GET_PROMOTION);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.GET_PROMOTION, new Response.Listener<String>() {
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
                                    promotionMds.clear();
//                                    Toast.makeText(Home.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject singleObject = dataArray.getJSONObject(i);
                                        PromotionMd promotionMd = new PromotionMd();
                                        promotionMd.setId(singleObject.getString("id"));
                                        promotionMd.setSubject(singleObject.getString("subject"));
                                        promotionMd.setDetails(singleObject.getString("details"));
                                        promotionMd.setStart_date(singleObject.getString("start_date"));
                                        promotionMd.setEnd_date(singleObject.getString("end_date"));
                                        promotionMd.setPoints(singleObject.getString("points"));
                                        promotionMd.setFb_image(singleObject.getString("fb_image"));
                                        promotionMd.setFb_url(singleObject.getString("fb_url"));
                                        promotionMd.setViews(singleObject.getString("views"));
                                        promotionMd.setFb_shares(singleObject.getString("fb_shares"));
                                        promotionMd.setClaims(singleObject.getString("claims"));
                                        promotionMd.setStatus(singleObject.getString("status"));
                                        promotionMd.setCreated_by(singleObject.getString("created_by"));
                                        promotionMd.setCreated(singleObject.getString("created"));
                                        promotionMd.setModified(singleObject.getString("modified"));
                                        promotionMd.setCountries(singleObject.getString("countries"));
                                        promotionMd.setInternal_claimed(singleObject.getString("internal_claimed"));
                                        promotionMd.setFb_claimed(singleObject.getString("fb_claimed"));
                                        promotionMds.add(promotionMd);
                                    }
                                    //
                                    promotionAdapter.notifyDataSetChanged();
                                } else {
                                    recycler_view.setVisibility(View.GONE);
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
                    java.util.Map<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
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

    @Override
    public void onPostShared(String promotionId) {
        this.promotionId = promotionId;
    }

    @Override
    public void refreshPromotion() {
        callPromotionApi();
    }
}