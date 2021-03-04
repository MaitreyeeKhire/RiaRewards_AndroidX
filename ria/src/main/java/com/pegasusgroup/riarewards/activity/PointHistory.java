package com.pegasusgroup.riarewards.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.PointAdapter;
import com.pegasusgroup.riarewards.interfaces.OnLoadMoreListener;
import com.pegasusgroup.riarewards.model.PointMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.RecyclerViewLoadMoreScroll;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PointHistory extends BaseAppCompatActivity {

    private RecyclerView recycler_view;
    private ArrayList<PointMd> pointMds;
    private PointAdapter pointAdapter;
    private int page_no;
    private int limit = 25;
    private RecyclerViewLoadMoreScroll scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_point_history;
    }

    @Override
    protected void initComponents() {
        pointMds = new ArrayList<>();
        recycler_view = findViewById(R.id.recycler_view);
        pointAdapter = new PointAdapter(PointHistory.this, pointMds);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PointHistory.this);
        recycler_view.setLayoutManager(linearLayoutManager);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        page_no = 0;
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recycler_view.setAdapter(pointAdapter);
        pointHistory();

        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page_no = page_no + limit;
                pointHistory();
            }
        });

        recycler_view.addOnScrollListener(scrollListener);
    }

    /**
     * @description This method is used to call Api
     */
    private void pointHistory() {
        try {
            String url = "https://www.myrewards.com.au/newapp/my_points_history.php?user_id="
                    + sessionManager.getUserId() + "&start=" + page_no + "&limit=" + limit
                    + "&client_id=" + sessionManager.getClientId();

            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            CommonMethods.printLog("URL : " + url);

            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("200")) {
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject singleObject = dataArray.getJSONObject(i);
                                        PointMd pointMd = new PointMd();
                                        pointMd.setTransaction_date(singleObject.getString("transaction_date"));
                                        pointMd.setReason(singleObject.getString("reason"));
                                        pointMd.setUpdate_points(singleObject.getString("updated_points"));
                                        pointMd.setPoints(singleObject.getString("points"));
                                        pointMds.add(pointMd);
                                    }
                                    pointAdapter.notifyDataSetChanged();
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