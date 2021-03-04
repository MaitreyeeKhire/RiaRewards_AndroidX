package com.pegasusgroup.riarewards.fragments;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.LastTransactionAdapter;
import com.pegasusgroup.riarewards.adapter.ReviewAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.model.ReviewMd;
import com.pegasusgroup.riarewards.model.TransactionHistoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Review extends BaseFragment {

    private TextInputLayout txtReview;
    private TextInputEditText edtReview;
    private AppCompatRatingBar ratingBar;
    private AppCompatButton cmdSubmit;

    private RecyclerView recycler_view;
    private ArrayList<ReviewMd> reviewMds;
    private ArrayList<TransactionHistoryMd> transactionHistoryMds;
    private ReviewAdapter reviewAdapter;
    private LastTransactionAdapter lastTransactionAdapter;

    private AppCompatTextView txtCustomerReview;
    private AppCompatTextView txtLastTransaction;

    private CardView llLastTransaction;
    private String reviewType;

    private AppCompatTextView txtTransactionId;
    private AppCompatTextView txtTransactionDate;
    private AppCompatTextView txtTransactionAmount;
    private AppCompatTextView txtStatus;


    public Review() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_review, container, false);
    }

    @Override
    protected void initComponents(View view) {
        reviewMds = new ArrayList<>();
        transactionHistoryMds = new ArrayList<>();
        recycler_view = findViewById(R.id.recycler_view);
        reviewAdapter = new ReviewAdapter(mContext, reviewMds);
        lastTransactionAdapter = new LastTransactionAdapter(mContext, transactionHistoryMds);

        txtReview = findViewById(R.id.txtReview);
        edtReview = findViewById(R.id.edtReview);
        ratingBar = findViewById(R.id.ratingBar);
        cmdSubmit = findViewById(R.id.cmdSubmit);

        txtCustomerReview = findViewById(R.id.txtCustomerReview);
        txtLastTransaction = findViewById(R.id.txtLastTransaction);

        llLastTransaction = findViewById(R.id.llLastTransaction);
        reviewType = getResources().getString(R.string.customer_review);

        txtTransactionId = findViewById(R.id.txtTransactionId);
        txtTransactionDate = findViewById(R.id.txtTransactionDate);
        txtTransactionAmount = findViewById(R.id.txtTransactionAmount);
        txtStatus = findViewById(R.id.txtStatus);
    }

    @Override
    protected void setListeners() {
        recycler_view.setNestedScrollingEnabled(false);

        txtCustomerReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCustomerReview.setBackground(mContext.getDrawable(R.drawable.my_order_selected_button));
                txtCustomerReview.setTextColor(getResources().getColor(R.color.white));
                txtLastTransaction.setBackground(mContext.getDrawable(R.drawable.my_order_unselected_button));
                txtLastTransaction.setTextColor(getResources().getColor(R.color.black));
                reviewType = getResources().getString(R.string.customer_review);
                canUserReview();
                callRatingApi();
                recycler_view.setAdapter(reviewAdapter);
            }
        });

        txtLastTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLastTransaction.setBackground(mContext.getDrawable(R.drawable.my_order_selected_button));
                txtLastTransaction.setTextColor(getResources().getColor(R.color.white));
                txtCustomerReview.setBackground(mContext.getDrawable(R.drawable.my_order_unselected_button));
                txtCustomerReview.setTextColor(getResources().getColor(R.color.black));
                reviewType = getResources().getString(R.string.last_transaction);
                canUserReview();
                callTransactionHistoryFeedbackApi();
                recycler_view.setAdapter(lastTransactionAdapter);
            }
        });

        txtCustomerReview.performClick();

        cmdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getText(edtReview).isEmpty()) {
                    if (ratingBar.getRating() != 0.0f) {
                        if (reviewType.equals(getResources().getString(R.string.customer_review))) {
                            addReview();
                        } else if (reviewType.equals(getResources().getString(R.string.last_transaction))) {
                            addLastTransactionReview();
                        }
                    } else {
                        showToast("Please rate a star");
                    }
                } else {
                    showToast("Please write a review");
                }
            }
        });
    }

    /**
     * @description This method is used to call Api
     */
    private void canUserReview() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            String URL = "";
            if (reviewType.equals(getResources().getString(R.string.customer_review))) {
                URL = AppConstants.CAN_REVIEW;
            } else if (reviewType.equals(getResources().getString(R.string.last_transaction))) {
                URL = AppConstants.GET_LAST_TRANSACTION;
            }

            CommonMethods.printLog("URL : " + URL);

            StringRequest otpReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String status = jsonObject.getString("status");
                                if (status.equals("200")) {
                                    showReview();
                                    if (reviewType.equals(getResources().getString(R.string.last_transaction))) {
                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                        JSONObject object = jsonArray.getJSONObject(0);
                                        txtTransactionId.setText(object.getString("order_number"));
                                        txtTransactionDate.setText(CommonMethods.getFormattedDate(object.getString("transaction_date")));
                                        txtTransactionAmount.setText(object.getString("amount"));
                                        txtStatus.setText(object.getString("status"));
                                    }
                                } else {
                                    hideReview();
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
                    HashMap<String, String> params = new HashMap<>();
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
     * @description This method is used to show display comment section
     */
    private void showReview() {
        txtReview.setVisibility(View.VISIBLE);
        edtReview.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        cmdSubmit.setVisibility(View.VISIBLE);
        if (reviewType.equals(getResources().getString(R.string.last_transaction))) {
            llLastTransaction.setVisibility(View.VISIBLE);
        } else {
            llLastTransaction.setVisibility(View.GONE);
        }
    }

    /**
     * @description This method is used to hide comment section
     */
    private void hideReview() {
        txtReview.setVisibility(View.GONE);
        edtReview.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
        cmdSubmit.setVisibility(View.GONE);
        llLastTransaction.setVisibility(View.GONE);
    }

    /**
     * @description This method is used to call Api
     */
    private void addLastTransactionReview() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.LAST_TRANSACTION_REVIEW);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.LAST_TRANSACTION_REVIEW, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String status = jsonObject.getString("status");
                                if (status.equals("200")) {
                                    edtReview.setText("");
                                    ratingBar.setRating(0);
                                    hideReview();
                                }
                                showToast(jsonObject.getString("msg"));
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
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    params.put("comment", getText(edtReview));
                    params.put("transaction_id", getText(txtTransactionId));
                    params.put("rating", String.valueOf(ratingBar.getRating()));
                    CommonMethods.printLog("params : " + params);
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
    private void addReview() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.ADD_REVIEW);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.ADD_REVIEW, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                String status = jsonObject.getString("status");
                                if (status.equals("200")) {
                                    showToast(jsonObject.getString("msg"));
                                    edtReview.setText("");
                                    ratingBar.setRating(0);
                                    hideReview();
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
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    params.put("review", getText(edtReview));
                    params.put("rating", String.valueOf(ratingBar.getRating()));
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
    private void callRatingApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.ALL_REVIEW);

            StringRequest otpReq = new StringRequest(Request.Method.GET, AppConstants.ALL_REVIEW, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                reviewMds.clear();
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject data = dataArray.getJSONObject(i);
                                    ReviewMd reviewMd = new ReviewMd();
                                    reviewMd.setId(data.getString("id"));
                                    reviewMd.setCommentedBy(data.getString("commented_by"));
                                    reviewMd.setComment(data.getString("comment"));
                                    reviewMd.setRating(data.getString("rating"));
                                    reviewMd.setPoints(data.getString("points"));
                                    reviewMd.setStatus(data.getString("status"));
                                    reviewMds.add(reviewMd);
                                }
                            }
                            reviewAdapter.notifyDataSetChanged();
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
        }
    }

    /**
     * @description This method is used to call Api
     */
    private void callTransactionHistoryFeedbackApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.LAST_TRANSACTION_FEEDBACK_HISTORY);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.LAST_TRANSACTION_FEEDBACK_HISTORY, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                transactionHistoryMds.clear();
                                showToast(jsonObject.getString("msg"));
                                if (jsonObject.getString("status").equals("200")) {
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject data = dataArray.getJSONObject(i);
                                        TransactionHistoryMd transactionHistoryMd = new TransactionHistoryMd();
                                        transactionHistoryMd.setId(data.getString("id"));
                                        transactionHistoryMd.setUser_id(data.getString("user_id"));
                                        transactionHistoryMd.setTransaction_id(data.getString("transaction_id"));
                                        transactionHistoryMd.setComment(data.getString("comment"));
                                        transactionHistoryMd.setRating(data.getString("rating"));
                                        transactionHistoryMd.setPoints(data.getString("points"));
                                        transactionHistoryMd.setStatus(data.getString("status"));
                                        transactionHistoryMd.setApproved_by(data.getString("approved_by"));
                                        transactionHistoryMd.setCreated(data.getString("created"));
                                        transactionHistoryMd.setModified(data.getString("modified"));
                                        transactionHistoryMds.add(transactionHistoryMd);
                                    }
                                }
                            }
                            lastTransactionAdapter.notifyDataSetChanged();
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
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", sessionManager.getUserId());
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}