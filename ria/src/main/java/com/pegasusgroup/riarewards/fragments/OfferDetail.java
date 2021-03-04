package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.model.SingleCategoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.ADD_TO_CART;
import static com.pegasusgroup.riarewards.interfaces.AppConstants.CLIENT_ID;
import static com.pegasusgroup.riarewards.interfaces.AppConstants.TIME_OUT;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferDetail extends BaseFragment {

    private String productId;

    private SingleCategoryMd singleCategoryMd;
    private ImageView imgProduct;
    private AppCompatTextView txtProductName;
    private AppCompatTextView txtMemberPrice;
    private AppCompatTextView txtPrice;
    private AppCompatTextView txtProductDetail;

    private AppCompatButton cmdPayWithDollar;
    private AppCompatButton cmdPayWithPoint;
    private AppCompatButton cmdPayWithDollarAndPoint;
    private AppCompatButton cmdBuyNow;

    private String redeem_button_img;

    public OfferDetail() {
        // Required empty public constructor
    }


    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_offer_detail, container, false);
    }

    @Override
    protected void initComponents(View view) {
        productId = Objects.requireNonNull(getArguments()).getString("productId", "");
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtMemberPrice = findViewById(R.id.txtMemberPrice);
        txtProductDetail = findViewById(R.id.txtProductDetail);
        txtPrice = findViewById(R.id.txtPrice);

        cmdPayWithDollar = findViewById(R.id.cmdPayWithDollar);
        cmdPayWithPoint = findViewById(R.id.cmdPayWithPoint);
        cmdPayWithDollarAndPoint = findViewById(R.id.cmdPayWithDollarAndPoint);
        cmdBuyNow = findViewById(R.id.cmdBuyNow);
        singleCategoryMd = new SingleCategoryMd();
    }

    @Override
    protected void setListeners() {
        if (productId.contains("http")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(productId));
            startActivity(browserIntent);
        } else {
            getOfferDetail();
        }

        cmdPayWithDollar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(singleCategoryMd.getId(), "Pay");
            }
        });

        cmdPayWithPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Integer.parseInt(sessionManager.getUserPoint()) >= Integer.parseInt(singleCategoryMd.getPoints())) {
//                    addToCart(singleCategoryMd.getId(), "Points");
//                } else {
//                    showToast("Sorry, you do not have enough points to buy this product");
//                }
                if (singleCategoryMd.getPrice() != null && !singleCategoryMd.getPrice().equalsIgnoreCase("null")) {
                    if (Double.parseDouble(sessionManager.getUserPoint()) >= Math.round(Double.parseDouble(singleCategoryMd.getPrice())
                            * Double.parseDouble(sessionManager.getPointsConversion())) + sessionManager.getPoints()) {
                        addToCart(singleCategoryMd.getId(), "Points");
                        float points = sessionManager.getPoints();
                        sessionManager.setPoints(points + Math.round(Double.parseDouble(singleCategoryMd.getPrice())
                                * Double.parseDouble(sessionManager.getPointsConversion())));
                    } else {
                        showToast("Sorry, you do not have enough points to buy this product");
                    }
                } else {
                    showToast("Price is not available");
                }
            }
        });

        cmdPayWithDollarAndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(singleCategoryMd.getId(), "PayPoints");
            }
        });

        cmdBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cmdBuyNow.getText().toString().equalsIgnoreCase("Redeem Now")) {
                    Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(redeem_button_img));
                    startActivity(browse);
                    redeemTracker(singleCategoryMd.getId());
                } else if (cmdBuyNow.getText().toString().equalsIgnoreCase("Get Now")) {
                    String voucherAPI = "https://www.myrewards.com.au/newapp/" + "get_voucher.php?uid="
                            + sessionManager.getUserId()
                            + "&cid=" + CLIENT_ID
                            + "&mid=" + singleCategoryMd.getMerchant_id()
                            + "&pid=" + singleCategoryMd.getId();
                    String pid = singleCategoryMd.getId();

                    Bundle bundle = new Bundle();
                    bundle.putString("voucherAPI", voucherAPI);
                    bundle.putString("pid", pid);
                    VoucherWebView voucherWebView = new VoucherWebView();
                    voucherWebView.setArguments(bundle);
                    fragmentChanger.change(voucherWebView);
                }
            }
        });
    }

    private void redeemTracker(String product_id) {
        try {
            JSONObject jsonObject = new JSONObject();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            try {
                jsonObject.put("cid", CLIENT_ID);
                jsonObject.put("uid", sessionManager.getUserId());
                jsonObject.put("product_id", product_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, AppConstants.REDEEM_ACTION, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            CommonMethods.printLog(response.toString());
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            jsObjRequest.setShouldCache(false);
            requestQueue.add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description This method is used to call Category API
     */
    private void getOfferDetail() {
        try {
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            String URL;

            URL = "https://www.myrewards.com.au/newapp/get_product.php?client_id="
                    + CLIENT_ID + "&id=" + productId + "&uid=" + sessionManager.getUserId() + "&response_type=json";
            URL = URL.replaceAll(" ", "%20");

            CommonMethods.printLog("ProductList URL : " + URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URL, null, new Response.Listener<JSONObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(JSONObject response) {
                    CommonMethods.printLog("response : " + response.toString());

                    try {
                        JSONArray jsonObject = null;
                        try {
                            jsonObject = response.getJSONArray("product");
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        JSONObject items = Objects.requireNonNull(jsonObject).getJSONObject(0);

//                    SingleCategoryMd singleCategoryMd = new SingleCategoryMd();
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
                        singleCategoryMd.setDetails(items.getString("details"));
                        if (items.has("pay_type"))
                            singleCategoryMd.setPay_type(items.getString("pay_type"));
                        if (items.has("price"))
                            singleCategoryMd.setPrice(items.getString("price"));
                        if (items.has("points"))
                            singleCategoryMd.setPoints(items.getString("points"));

//                        if (singleCategoryMd.getIs_favourite().equals("1")) {
//                            imgMyFavourite.setImageResource(R.drawable.favourite_selected);
//                        } else if (singleCategoryMd.getIs_favourite().equals("0")) {
//                            imgMyFavourite.setImageResource(R.drawable.favourite_unselected);
//                        }

                        txtProductName.setText(singleCategoryMd.getName());

                        if (singleCategoryMd.getDetails() != null)
                            txtProductDetail.setText(Html.fromHtml(singleCategoryMd.getDetails()));

                        if (singleCategoryMd.getPrice() != null && !singleCategoryMd.getPrice().equals("null")) {
                            txtPrice.setText("$" + singleCategoryMd.getPrice());
                            txtMemberPrice.setPaintFlags(txtMemberPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            if (singleCategoryMd.getName().contains("$")) {
                                String originalPrice = singleCategoryMd.getName().substring(singleCategoryMd.getName().lastIndexOf("$"));
                                txtMemberPrice.setText(originalPrice);
                            }
                        } else {
                            txtPrice.setVisibility(View.GONE);
                            txtMemberPrice.setVisibility(View.GONE);
                        }

//                        txtHighLight.setText(singleCategoryMd.getHighlight());

                        String imageUrl = "";
                        switch (singleCategoryMd.getDisplay_image()) {
                            case "Product Image":
                                if (!singleCategoryMd.getId().equals("") && !singleCategoryMd.getImage_extension().equals("")) {
                                    imageUrl = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/product_image/"
                                            + singleCategoryMd.getId() + "." + singleCategoryMd.getImage_extension();
                                } else {
                                    if (!singleCategoryMd.getMerchant_id().equals("") && !singleCategoryMd.getLogo_extension().equals("")) {
                                        imageUrl = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/merchant_logo/"
                                                + singleCategoryMd.getMerchant_id() + "." + singleCategoryMd.getLogo_extension();
                                    }
                                }
                                break;
                            case "Merchant Logo":
                                if (!singleCategoryMd.getMerchant_id().equals("") && !singleCategoryMd.getLogo_extension().equals("")) {
                                    imageUrl = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/merchant_logo/"
                                            + singleCategoryMd.getMerchant_id() + "." + singleCategoryMd.getLogo_extension();
                                } else {
                                    if (!singleCategoryMd.getId().equals("") && !singleCategoryMd.getImage_extension().equals("")) {
                                        imageUrl = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/product_image/"
                                                + singleCategoryMd.getId() + "." + singleCategoryMd.getImage_extension();
                                    }
                                }
                                break;
                            default:
                                imageUrl = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/merchant_logo/"
                                        + singleCategoryMd.getMerchant_id() + "." + singleCategoryMd.getLogo_extension();
                                break;
                        }
                        if (!imageUrl.isEmpty()) {
                            Glide.with(mContext).load(imageUrl).fitCenter()
                                    .crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgProduct);
                        }

                        redeem_button_img = singleCategoryMd.getRedeem_button_img();
                        int web_coupon = Integer.parseInt(singleCategoryMd.getWeb_coupon());
                        int user_used_count = Integer.parseInt(singleCategoryMd.getUser_used_count());
                        int used_count = Integer.parseInt(singleCategoryMd.getUsed_count());

                        if (redeem_button_img != null && !redeem_button_img.isEmpty()) {
                            if (!TextUtils.isEmpty(redeem_button_img)) {
                                cmdBuyNow.setText("Redeem Now");
                                cmdBuyNow.setVisibility(View.VISIBLE);
                                cmdPayWithDollar.setVisibility(View.GONE);
                                cmdPayWithPoint.setVisibility(View.GONE);
                                cmdPayWithDollarAndPoint.setVisibility(View.GONE);
                            }
                        } else if ((web_coupon == 1) && (user_used_count > 0 || user_used_count == -1) && (used_count > 0 || used_count == -1)) {
                            cmdBuyNow.setText("Get Now");
                            cmdBuyNow.setVisibility(View.VISIBLE);
                            cmdPayWithDollar.setVisibility(View.GONE);
                            cmdPayWithPoint.setVisibility(View.GONE);
                            cmdPayWithDollarAndPoint.setVisibility(View.GONE);
                        } else if (singleCategoryMd.getProduct_quantity() != null && !singleCategoryMd.getProduct_quantity().equals("")) {
                            cmdBuyNow.setVisibility(View.GONE);
                            cmdPayWithDollar.setVisibility(View.VISIBLE);
                            cmdPayWithPoint.setVisibility(View.VISIBLE);
                            cmdPayWithDollarAndPoint.setVisibility(View.VISIBLE);
                        } else {
                            CommonMethods.printLog("in else");
                        }

//                        if (singleCategoryMd.getIs_favourite().equals("1")) {
//                            imgMyFavourite.setImageResource(R.drawable.favourite_selected);
//                        } else if (singleCategoryMd.getIs_favourite().equals("0")) {
//                            imgMyFavourite.setImageResource(R.drawable.favourite_unselected);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                        hideSoftKeyboard();
                    }
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

    private void addToCart(final String product_id, String payType) {
        if (singleCategoryMd.getPrice() == null) {
            showToast("Price not available");
        } else {
            progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("pid", product_id);
                jsonObject.put("uid", sessionManager.getUserId());
                jsonObject.put("pay_type", payType);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CommonMethods.printLog("ADD_TO_CART : " + ADD_TO_CART);
            CommonMethods.printLog("ADD_TO_CART Params : " + jsonObject.toString());

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.PUT, ADD_TO_CART, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            CommonMethods.printLog("Add to Cart URL :" + ADD_TO_CART);
                            CommonMethods.printLog("Response : " + response.toString());
                            CommonMethods.printLog("Params : " + jsonObject.toString());
                            try {
                                if (response.has("cart_id")) {
                                    String cart_id = response.getString("cart_id");
                                    CommonMethods.printLog("cart_id: " + cart_id);
                                    sessionManager.setCartId(cart_id);
                                }
                            } catch (Exception iox) {
                                CommonMethods.printLog("iox: " + iox);
                            } finally {
                                progressDialog.dismiss();
                            }
                            showAlert();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("PutDetails", "");
                            progressDialog.dismiss();

                        }
                    });

            jsObjRequest.setShouldCache(false);
            requestQueue.add(jsObjRequest);
        }
    }

    @SuppressWarnings("deprecation")
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                mContext).create();
        alertDialog.setTitle("Response");
        alertDialog.setMessage("Product added to cart");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getCardItemCount();
            }
        });

        alertDialog.show();
        Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(Color.parseColor("#FFFF0400"));
    }

    private void getCardItemCount() {
        String countURL = AppConstants.CART_ITEMS + "uid=" + sessionManager.getUserId() + "&cart_id=" + sessionManager.getCartId();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest postReq = new StringRequest(Request.Method.GET, countURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CommonMethods.printLog("response : " + response);
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("status")) {
                            String cart_items = jsonObject.getString("cart_items");
                            sessionManager.setCartCount(cart_items);
                        }
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
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