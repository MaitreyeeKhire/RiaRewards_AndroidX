package com.pegasusgroup.riarewards.fragments;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.FavouriteAdapter;
import com.pegasusgroup.riarewards.adapter.PointAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.model.PointMd;
import com.pegasusgroup.riarewards.model.SingleCategoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.GET_USER_INFO;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long prev = 0;

    // Customer Card
    private FrameLayout frameLayout;
    private AppCompatTextView txtRiaCustomerNumber;
    private AppCompatImageView imgBarCode;

    // Points
    private RecyclerView point_recycler_view;
    private ArrayList<PointMd> pointMds;
    private PointAdapter pointAdapter;
    private AppCompatTextView txtPointFullHistory;

    // Favourite
    private AppCompatTextView txtFavourites;
    private LinearLayout favLinearLayout;
    private RecyclerView fav_recycler_view;
    private ArrayList<SingleCategoryMd> singleCategoryMds;
    private FavouriteAdapter favouriteAdapter;

    public Home() {
        // Required empty public constructor
    }

    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initComponents(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Customer Card
        frameLayout = findViewById(R.id.frameLayout);
        txtRiaCustomerNumber = findViewById(R.id.txtRiaCustomerNumber);
        imgBarCode = findViewById(R.id.imgBarCode);

        // Point
        pointMds = new ArrayList<>();
        point_recycler_view = findViewById(R.id.point_recycler_view);
        pointAdapter = new PointAdapter(mContext, pointMds);
        txtPointFullHistory = findViewById(R.id.txtPointFullHistory);

        // Favourite
        txtFavourites = findViewById(R.id.txtFavourites);
        favLinearLayout = findViewById(R.id.favLinearLayout);
        fav_recycler_view = findViewById(R.id.fav_recycler_view);
        singleCategoryMds = new ArrayList<>();
        favouriteAdapter = new FavouriteAdapter(mContext, singleCategoryMds, fragmentChanger);
    }

    @Override
    protected void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        txtRiaCustomerNumber.setText(sessionManager.getUserName());

        ViewTreeObserver viewTreeObserver = frameLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    try {
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        BitMatrix bitMatrix = multiFormatWriter.encode(sessionManager.getUserName(), BarcodeFormat.CODE_128, frameLayout.getWidth(), 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        imgBarCode.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        txtPointFullHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fragmentChanger.change(new PointHistory(), true);
                fragmentReplacer.replace(new PointHistory());
//            logDetails("Point History");
            }
        });

        point_recycler_view.setAdapter(pointAdapter);
        point_recycler_view.setNestedScrollingEnabled(false);

        fav_recycler_view.setAdapter(favouriteAdapter);
        fav_recycler_view.setNestedScrollingEnabled(false);

        txtFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fragmentChanger.change(new Favourite(), true);
//                fragmentReplacer.replace(new Favourite());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        baseAppCompatActivity.imgBack.setVisibility(View.INVISIBLE);
        if (System.currentTimeMillis() - prev > 3000) {
            prev = System.currentTimeMillis();
//            callPromotionApi();
            pointHistory();
            callFavouriteApi();
        }
        callGetCardItemCount();
        getTotalUserPoints();
    }


    @Override
    public void onRefresh() {
        pointHistory();
        callFavouriteApi();

        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * @description This method is used to call Api
     */
    private void pointHistory() {
        try {
            String url = "https://www.myrewards.com.au/newapp/my_points_history.php?user_id="
                    + sessionManager.getUserId() + "&start=0&limit=10"
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
                        CommonMethods.printLog("point response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.has("status")) {
                                pointMds.clear();
                                if (jsonObject.getString("status").equals("200")) {
                                    JSONArray dataArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject singleObject = dataArray.getJSONObject(i);
                                        PointMd pointMd = new PointMd();
                                        pointMd.setTransaction_date(singleObject.getString("transaction_date"));
                                        pointMd.setReason(singleObject.getString("reason"));
                                        pointMd.setUpdate_points(singleObject.getString("updated_points"));
                                        pointMd.setPoints(singleObject.getString("points"));
                                        if (i < 4) {
                                            pointMds.add(pointMd);
                                        } else {
                                            break;
                                        }
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

    /**
     * @description This method is used to call Favourite Api
     */
    private void callFavouriteApi() {
        try {
            String Url = "https://www.myrewards.com.au/newapp/get_favourites.php?uid=" + sessionManager.getUserId() + "&start=0&limit=150";
            CommonMethods.printLog("Favourite URL : " + Url);

            RequestQueue queue = Volley.newRequestQueue(mContext);
            progressDialog.show();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
//                            Log.d("onResponse", "onResponse: " + response);
                            singleCategoryMds.clear();
                            try {
                                favLinearLayout.setVisibility(View.VISIBLE);
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

                                        if (jsonObject.has("details"))
                                            modelClass.setDetails(jsonObject.getString("details"));

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

                                        modelClass.setRedeem_button_img(jsonObject.getString("redeem_button_img"));
                                        modelClass.setProduct_quantity(jsonObject.getString("product_quantity"));
                                        modelClass.setOut_of_order(jsonObject.getString("out_of_stock"));
                                        modelClass.setWeb_coupon(jsonObject.getString("web_coupon"));
                                        modelClass.setUsed_count(jsonObject.getString("used_count"));
                                        modelClass.setUser_used_count(jsonObject.getString("user_used_count"));
                                        modelClass.setRedeem_button_img(jsonObject.getString("redeem_button_img"));

                                        if (jsonObject.has("pay_type"))
                                            modelClass.setPay_type(jsonObject.getString("pay_type"));

                                        if (jsonObject.has("points"))
                                            modelClass.setPoints(jsonObject.getString("points"));

                                        singleCategoryMds.add(modelClass);
                                    }
                                } else {
                                    showToast("No Favourites Found");
                                    favLinearLayout.setVisibility(View.GONE);
                                }

                                // get course_description
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                favLinearLayout.setVisibility(View.GONE);
                            } finally {
                                progressDialog.dismiss();
                            }
//                            Log.d("", "onResponse: ");
                            favouriteAdapter.notifyDataSetChanged();

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            favLinearLayout.setVisibility(View.GONE);
                        }
                    });

            jsObjRequest.setShouldCache(false);
            queue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            favLinearLayout.setVisibility(View.GONE);
        }
    }

    /**
     * @description This method is used to call Api
     */
    private void callGetCardItemCount() {
        if (!sessionManager.getCartId().isEmpty()) {
            String countURL = AppConstants.CART_ITEMS + "uid=" + sessionManager.getUserId() + "&cart_id=" + sessionManager.getCartId();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            StringRequest postReq = new StringRequest(Request.Method.GET, countURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    CommonMethods.printLog("response : " + response);
                    //{"status":{"200":"success"},"cart_items":"1"}
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.contains("200")) {
                                sessionManager.setCartCount(jsonObject.getString("cart_items"));
//                                cart_counter.setText(sessionManager.getCartCount());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("", "Error: " + error.getMessage());
                }

            });

            requestQueue.add(postReq);
        }
    }

    /*
     * Used for getting total point earned by the user
     * This point is used for Payment
     */
    private void getTotalUserPoints() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest postReq = new StringRequest(Request.Method.POST, GET_USER_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethods.printLog("GET_USER_INFO Response::" + response);
                        try {
                            if (response.contains("cart_id")) {
                                JSONArray array = new JSONArray(response);
                                JSONObject user = array.getJSONObject(0);
                                CommonMethods.printLog("User data" + user);
                                sessionManager.setUserPoint(user.getString("points"));
                                sessionManager.setUserName(user.getString("username"));

                                if (user.has("points_conversion"))
                                    sessionManager.setPointsConversion(user.getString("points_conversion"));

                                if (user.has("merchnat_fee"))
                                    sessionManager.setMerchantFee(user.getString("merchnat_fee"));


                                txtRiaCustomerNumber.setText(sessionManager.getUserName());
                            } else {
                                showToast(getString(R.string.failed_to_get_data_from_server));
                            }
                        } catch (Exception je) {
                            CommonMethods.printLog(je.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("uname", sessionManager.getUserId());
                params.put("client_id", sessionManager.getClientId());
                params.put("response_type", "json");
                return params;
            }
        };
        requestQueue.add(postReq);
    }
}