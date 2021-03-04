package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.ProductAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.OnFavouriteClickListener;
import com.pegasusgroup.riarewards.interfaces.OnLoadMoreListener;
import com.pegasusgroup.riarewards.model.SingleCategoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.RecyclerViewLoadMoreScroll;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.TIME_OUT;

public class ProductList extends BaseAppCompatActivity implements OnFavouriteClickListener {
    private String id;
    private String name;
    private String query;

    private AppCompatTextView txtCategory;

    private RecyclerView recycler_view;
    private ProductAdapter productAdapter;
    private ArrayList<SingleCategoryMd> singleCategoryMds;
    private RecyclerViewLoadMoreScroll scrollListener;

    private AppCompatEditText edtProduct;

    private int page_no;
    private int limit = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product_list;
    }

    @Override
    protected void initComponents() {
        // Get Data from Intent
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        query = getIntent().getStringExtra("query");

        txtCategory = findViewById(R.id.txtCategory);
        recycler_view = findViewById(R.id.recycler_view);
        singleCategoryMds = new ArrayList<>();
        productAdapter = new ProductAdapter(ProductList.this, singleCategoryMds);
        edtProduct = findViewById(R.id.edtProduct);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductList.this);
        recycler_view.setLayoutManager(linearLayoutManager);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        page_no = 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (name != null)
            txtCategory.setText(name);
        else
            txtCategory.setVisibility(View.GONE);
        recycler_view.setAdapter(productAdapter);


        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page_no = page_no + limit;
                callBrowseCategoryApi();
            }
        });

        recycler_view.addOnScrollListener(scrollListener);

        callBrowseCategoryApi();

        edtProduct.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    query = getText(edtProduct);
                    singleCategoryMds.clear();
                    callBrowseCategoryApi();
                    page_no = 0;
                    return true;
                }
                return false;
            }
        });

        edtProduct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtProduct.getRight() - edtProduct.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        query = getText(edtProduct);
                        singleCategoryMds.clear();
                        page_no = 0;
                        callBrowseCategoryApi();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    /**
     * @description This method is used to call Category API
     */
    private void callBrowseCategoryApi() {
        try {
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(ProductList.this);
            String URL;

            if (id != null && name != null && query != null) {
                URL = "https://www.myrewards.com.au/newapp/search_cat.php?q="
                        + query + "&cat_id=" + id + "&cid=" + sessionManager.getClientId() + "&country=Australia&start=" + page_no + "&limit=" + limit + "&uid="
                        + sessionManager.getUserId() + "&response_type=json";
            } else if (id != null && query != null) {
                URL = "https://www.myrewards.com.au/newapp/search_cat.php?q=" + query + "&cid=" + sessionManager.getClientId() + "&country=Australia&start="
                        + page_no + "&limit=" + limit + "&uid=" + sessionManager.getUserId() + "&response_type=json";
            } else if (id == null && name == null && query != null) {
                URL = "https://www.myrewards.com.au/newapp/search_cat.php?q=" + query + "&cid=" + sessionManager.getClientId()
                        + "&country=Australia&start=" + page_no + "&limit=" + limit + "&uid=" + sessionManager.getUserId() + "&response_type=json";
            } else {
                URL = "https://www.myrewards.com.au/newapp/search_cat.php?cat_id=" + id + "&cid=" + sessionManager.getClientId()
                        + "&country=Australia&start=" + page_no + "&limit=" + limit + "&uid=" + sessionManager.getUserId() + "&response_type=json";
            }

            URL = URL.replaceAll(" ", "%20");

            CommonMethods.printLog("ProductList URL : " + URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    CommonMethods.printLog("response : " + response.toString());
//                    if (response.toString().length() > 4000) {
//                        Log.e("CashBack", "sb.length = " + response.length());
//                        int chunkCount = response.toString().length() / 4000;     // integer division
//                        for (int i = 0; i <= chunkCount; i++) {
//                            int max = 4000 * (i + 1);
//                            if (max >= response.toString().length()) {
//                                Log.e("CashBack", "chunk " + i + " of " + chunkCount + ":" + response.toString().substring(4000 * i));
//                            } else {
//                                Log.e("CashBack", "chunk " + i + " of " + chunkCount + ":" + response.toString().substring(4000 * i, max));
//                            }
//                        }
//                    }

                    try {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = response.getJSONObject("cat");
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        JSONObject items;
                        Iterator<String> keys = Objects.requireNonNull(jsonObject).keys();
//                        singleCategoryMds.clear();
                        while (keys.hasNext()) {
                            String keyValue = keys.next();

                            if (!keyValue.equals("link_next") && !keyValue.equals("link_prev")) {
                                items = jsonObject.getJSONObject(keyValue);

                                SingleCategoryMd singleCategoryMd = new SingleCategoryMd();
                                //getting string values with keys- pageid and title
                                String name = items.getString("name");
                                String highlight = items.getString("highlight");
                                String id = items.getString("id");
                                String favValue = items.getString("is_favourite");
                                String display_image = items.getString("display_image");
                                String logoExtension = items.getString("logo_extension");
                                String stripimage = items.getString("strip_image");
                                String merchant_id = items.getString("merchant_id");
                                String offer = items.getString("offer");
                                String image_extension = items.getString("image_extension");

                                if (items.has("price"))
                                    singleCategoryMd.setPrice(items.getString("price"));

                                if (items.has("details"))
                                    singleCategoryMd.setDetails(items.getString("details"));

                                singleCategoryMd.setName(name);
                                singleCategoryMd.setHighlight(highlight);
                                singleCategoryMd.setId(id);
                                singleCategoryMd.setIs_favourite(favValue);
                                singleCategoryMd.setOut_of_order(items.getString("out_of_stock"));
                                singleCategoryMd.setDisplay_image(display_image);
                                singleCategoryMd.setLogo_extension(logoExtension);
                                singleCategoryMd.setStrip_image(stripimage);
                                singleCategoryMd.setMerchant_id(merchant_id);
                                singleCategoryMd.setOffer(offer);
                                singleCategoryMd.setImage_extension(image_extension);
                                singleCategoryMd.setRedeem_button_img(items.getString("redeem_button_img"));
                                singleCategoryMd.setProduct_quantity(items.getString("product_quantity"));
                                singleCategoryMd.setWeb_coupon(items.getString("web_coupon"));
                                singleCategoryMd.setUser_used_count(items.getString("user_used_count"));
                                singleCategoryMd.setUsed_count(items.getString("used_count"));
//                                singleCategoryMd.setProductPoints(items.getString("product_points"));

                                if (items.has("pay_type"))
                                    singleCategoryMd.setPay_type(items.getString("pay_type"));

                                if (items.has("points"))
                                    singleCategoryMd.setPoints(items.getString("points"));

                                singleCategoryMds.add(singleCategoryMd);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                        hideSoftKeyboard();
                    }
                    productAdapter.notifyDataSetChanged();
                    scrollListener.setLoaded();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    error.printStackTrace();
                    showToast("No such product is available.");
                    progressDialog.dismiss();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return TIME_OUT;
                }

                @Override
                public int getCurrentRetryCount() {
                    return TIME_OUT;
                }

                @Override
                public void retry(VolleyError error) {

                }
            });

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    /**
     * @param userId    User Id
     * @param productId Product Id
     * @param flag      True or False
     * @description This method is used to call Favourite API
     */
    private void setFavApiCall(final String userId, final String productId, final boolean flag, final int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(ProductList.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest postReq = new StringRequest(Request.Method.POST, AppConstants.SET_FAV, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        jsonObject.getJSONObject("status");
                        CommonMethods.printLog("onResponse:setFavorite " + jsonObject);
                        JSONObject status = jsonObject.getJSONObject("status");
                        //Getting all the keys inside json object with key- pages
                        Iterator<String> keys = status.keys();
                        while (keys.hasNext()) {
                            String keyValue = keys.next();

                            if (keyValue.equals("200")) {
                                if (flag) {
                                    singleCategoryMds.get(position).setIs_favourite("1");
                                } else {
                                    singleCategoryMds.get(position).setIs_favourite("0");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }

        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("uid", userId);
                params.put("pid", productId);
                params.put("flag", String.valueOf(flag));
                CommonMethods.printLog("getParams: " + params);
                return params;
            }
        };

        postReq.setShouldCache(false);
        requestQueue.add(postReq);
    }


    @Override
    public void favouriteClick(final String userId, final String productId, final boolean flag, int position) {
        setFavApiCall(userId, productId, flag, position);
        if (flag)
            putDetails(AppConstants.CLIENT_ID, userId, "android", androidId, id, name, singleCategoryMds.get(position).getId(), singleCategoryMds.get(position).getName(),
                    "Product List", reportDate, null, "Product favourite removed");
        else
            putDetails(AppConstants.CLIENT_ID, userId, "android", androidId, id, name, singleCategoryMds.get(position).getId(), singleCategoryMds.get(position).getName(),
                    "Product List", reportDate, null, "Product favourite removed");
    }
}