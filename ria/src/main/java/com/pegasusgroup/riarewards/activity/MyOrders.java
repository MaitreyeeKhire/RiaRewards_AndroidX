package com.pegasusgroup.riarewards.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.MyOrderAdapter;
import com.pegasusgroup.riarewards.model.OrdersMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MyOrders extends BaseAppCompatActivity {

    private RecyclerView recycler_view;
    private ArrayList<OrdersMd> ordersMds;
    private MyOrderAdapter myOrderAdapter;

    private AppCompatTextView txtPending;
    private AppCompatTextView txtCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_my_orders;
    }

    @Override
    protected void initComponents() {
        recycler_view = findViewById(R.id.recycler_view);
        txtPending = findViewById(R.id.txtPending);
        txtCompleted = findViewById(R.id.txtCompleted);
        ordersMds = new ArrayList<>();
        myOrderAdapter = new MyOrderAdapter(MyOrders.this, ordersMds);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recycler_view.setAdapter(myOrderAdapter);

        txtCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCompleted.setBackground(getDrawable(R.drawable.my_order_selected_button));
                txtCompleted.setTextColor(getResources().getColor(R.color.white));
                txtPending.setBackground(getDrawable(R.drawable.my_order_unselected_button));
                txtPending.setTextColor(getResources().getColor(R.color.black));
                callOrderStatusApi("completed");
            }
        });

        txtPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPending.setBackground(getDrawable(R.drawable.my_order_selected_button));
                txtPending.setTextColor(getResources().getColor(R.color.white));
                txtCompleted.setBackground(getDrawable(R.drawable.my_order_unselected_button));
                txtCompleted.setTextColor(getResources().getColor(R.color.black));
                callOrderStatusApi("pending");
            }
        });

        callOrderStatusApi("pending");
    }

    private void callOrderStatusApi(final String status) {
        try {
            progressDialog.show();

            String url = "https://www.myrewards.com.au/newapp/my_orders.php?user_id="
                    + sessionManager.getUserId() + "&type=" + status + "&start=0&limit=10"
                    + "&client_id=" + sessionManager.getClientId();
            CommonMethods.printLog("My Orders URL : " + url);

            RequestQueue requestQueue = Volley.newRequestQueue(MyOrders.this);
            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            ordersMds.clear();
                            if (jsonObject.has("status")) {
                                if (jsonObject.has("data")) {
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject data = dataArray.getJSONObject(i);
                                        OrdersMd ordersMd = new OrdersMd();
//                                        ordersMd.setOrder_number(data.getString("order_number"));
                                        ordersMd.setCart_id(data.getString("cart_id"));
                                        ordersMd.setProduct_id(data.getString("product_id"));
                                        ordersMd.setProduct_name(data.getString("product_name"));
                                        ordersMd.setImage_extension(data.getString("image_extension"));
                                        ordersMd.setCart_date(data.getString("cart_date"));
                                        ordersMd.setMin_delivery_days(data.getString("min_delivery_days"));
                                        ordersMd.setMax_delivery_days(data.getString("max_delivery_days"));
                                        ordersMd.setProduct_qty(data.getString("product_qty"));
                                        ordersMd.setTotal(data.getString("total"));
                                        ordersMd.setPay_points(data.getString("pay_points"));
                                        ordersMd.setProduct_image(data.getString("product_image"));
                                        ordersMd.setCart_status(status);
                                        ordersMd.setCart_price(data.getString("cart_price"));
                                        ordersMds.add(ordersMd);
                                    }
                                } else {
                                    showToast(jsonObject.getString("msg"));
                                }
                            } else {
                                showToast(jsonObject.getString("msg"));
                            }
                            myOrderAdapter.notifyDataSetChanged();
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
}