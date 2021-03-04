package com.pegasusgroup.riarewards.fragments;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.AddressAdapter;
import com.pegasusgroup.riarewards.model.AddressMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.CLIENT_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressList extends BaseFragment {

    private AddressAdapter addressAdapter;
    private ArrayList<AddressMd> addressMds;
    private RecyclerView recyclerView;
    private FloatingActionButton cmdAdd;

    public AddressList() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_address_list, container, false);
    }

    @Override
    protected void initComponents(View view) {
        addressMds = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        addressAdapter = new AddressAdapter(mContext, addressMds, fragmentChanger, this);
        cmdAdd = findViewById(R.id.cmdAdd);
        DrawableCompat.setTintList(DrawableCompat.wrap(cmdAdd.getDrawable()),
                ColorStateList.valueOf(getResources().getColor(R.color.white)));
    }

    @Override
    protected void setListeners() {
        recyclerView.setAdapter(addressAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    cmdAdd.hide();
                } else {
                    cmdAdd.show();
                }
            }
        });

        cmdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startNextActivity(com.pegasusgroup.riarewards.activity.AddressList.this, AddAddress.class);
                fragmentChanger.change(new AddAddress());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        callAddressApi();
    }

    /**
     * @description This method is used to call Set Password Method
     */
    private void callAddressApi() {
        try {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            String url = "https://www.myrewards.com.au/newapp/" + "get_user_address.php?uname=" + sessionManager.getUserId()
                    + "&client_id=" + CLIENT_ID + "&response_type=" + "json";
            CommonMethods.printLog("URL : " + url);

            StringRequest otpReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (!jsonObject.has("status")) {
                                addressMds.clear();
                                JSONArray dataArray = jsonObject.getJSONArray("address");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject singleObject = dataArray.getJSONObject(i);
                                    AddressMd addressMd = new AddressMd();
                                    addressMd.setId(singleObject.getString("id"));
                                    addressMd.setCart_id(singleObject.getString("cart_id"));
                                    addressMd.setUser_id(singleObject.getString("user_id"));
                                    addressMd.setDate(singleObject.getString("date"));
                                    addressMd.setLast_update(singleObject.getString("last_update"));
                                    addressMd.setUser_name(singleObject.getString("username"));
                                    addressMd.setMember_name(singleObject.getString("member_name"));
                                    addressMd.setEmail(singleObject.getString("email"));
                                    addressMd.setMobile(singleObject.getString("mobile"));
                                    addressMd.setAddress1(singleObject.getString("address1"));
                                    addressMd.setAddress2(singleObject.getString("address2"));
                                    addressMd.setCountry(singleObject.getString("country"));
                                    addressMd.setState(singleObject.getString("state"));
                                    addressMd.setCity(singleObject.getString("city"));
                                    addressMd.setZipcode(singleObject.getString("zipcode"));
                                    addressMd.setActive(singleObject.getString("active"));
                                    addressMd.setCreated(singleObject.getString("created"));
                                    addressMd.setModified(singleObject.getString("modified"));
                                    addressMds.add(addressMd);
                                }
                                addressAdapter.notifyDataSetChanged();
                            } else {
                                showToast(jsonObject.getString("status"));
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
                    progressDialog.dismiss();
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