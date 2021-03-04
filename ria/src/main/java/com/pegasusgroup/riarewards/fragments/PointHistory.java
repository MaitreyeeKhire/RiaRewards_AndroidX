package com.pegasusgroup.riarewards.fragments;

import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class PointHistory extends BaseFragment {

    private RecyclerView recycler_view;
    private ArrayList<PointMd> pointMds;
    private PointAdapter pointAdapter;
    private int page_no;
    private int limit = 25;
    private RecyclerViewLoadMoreScroll scrollListener;


    public PointHistory() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_point_history, container, false);
    }

    @Override
    protected void initComponents(View view) {
        pointMds = new ArrayList<>();
        recycler_view = findViewById(R.id.recycler_view);
        pointAdapter = new PointAdapter(mContext, pointMds);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view.setLayoutManager(linearLayoutManager);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        page_no = 0;

    }

    @Override
    protected void setListeners() {
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
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

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