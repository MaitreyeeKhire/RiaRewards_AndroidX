package com.pegasusgroup.riarewards.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.FavouriteAdapter;
import com.pegasusgroup.riarewards.model.SingleCategoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Favourite extends BaseAppCompatActivity {

    private RecyclerView recycler_view;
    private ArrayList<SingleCategoryMd> singleCategoryMds = new ArrayList<>();
    private FavouriteAdapter favouriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_favourite;
    }

    @Override
    protected void initComponents() {
        recycler_view = findViewById(R.id.recycler_view);
        favouriteAdapter = new FavouriteAdapter(Favourite.this, singleCategoryMds);
    }

    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recycler_view.setAdapter(favouriteAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callFavouriteApi();
    }

    /**
     * @description This method is used to call Favourite Api
     */
    private void callFavouriteApi() {
        try {
            String Url = "https://www.myrewards.com.au/newapp/get_favourites.php?uid=" + sessionManager.getUserId() + "&start=0&limit=150";
            CommonMethods.printLog("Favourite URL : " + Url);

            RequestQueue queue = Volley.newRequestQueue(Favourite.this);
            progressDialog.show();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("onResponse", "onResponse: " + response);
                            try {
                                JSONObject status = response.getJSONObject("status");

                                Iterator<String> iter = status.keys();
                                ArrayList<String> keys = new ArrayList<>();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    keys.add(key);
                                }

                                if (keys.get(0).equals("200")) {
                                    JSONArray fav = response.getJSONArray("favourites");

                                    for (int i = 0; i < fav.length(); i++) {
                                        JSONObject jsonObject = fav.getJSONObject(i);
                                        SingleCategoryMd modelClass = new SingleCategoryMd();
                                        //getting string values with keys- pageid and title
                                        String name = jsonObject.getString("name");
                                        String highlight = jsonObject.getString("highlight");
                                        String id = jsonObject.getString("id");
                                        String display_image = jsonObject.getString("display_image");
                                        String logoExtension = jsonObject.getString("logo_extension");
                                        String stripimage = jsonObject.getString("strip_image");
                                        String merchant_id = jsonObject.getString("merchant_id");
                                        String offer = jsonObject.getString("offer");
                                        String image_extension = jsonObject.getString("image_extension");

                                        if (jsonObject.has("price"))
                                            modelClass.setPrice(jsonObject.getString("price"));

                                        modelClass.setName(name);
                                        modelClass.setHighlight(highlight);
                                        modelClass.setId(id);
                                        modelClass.setIs_favourite("1");
                                        modelClass.setDisplay_image(display_image);
                                        modelClass.setLogo_extension(logoExtension);
                                        modelClass.setStrip_image(stripimage);
                                        modelClass.setMerchant_id(merchant_id);
                                        modelClass.setOffer(offer);
                                        modelClass.setImage_extension(image_extension);

                                        modelClass.setProduct_quantity(jsonObject.getString("product_quantity"));
                                        modelClass.setOut_of_order(jsonObject.getString("out_of_stock"));
                                        modelClass.setWeb_coupon(jsonObject.getString("web_coupon"));
                                        modelClass.setUsed_count(jsonObject.getString("used_count"));
                                        modelClass.setUser_used_count(jsonObject.getString("user_used_count"));
                                        modelClass.setRedeem_button_img(jsonObject.getString("redeem_button_img"));
//                                        modelClass.setProductPoints(jsonObject.getString("product_points"));

                                        singleCategoryMds.add(modelClass);
                                    }
                                } else {
                                    Toast.makeText(Favourite.this, "No Favourites Found", Toast.LENGTH_SHORT).show();
                                }

                                // get course_description
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            } finally {
                                progressDialog.dismiss();
                            }
                            Log.d("", "onResponse: ");
                            favouriteAdapter.notifyDataSetChanged();

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                        }
                    });

            jsObjRequest.setShouldCache(false);
            queue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }
}