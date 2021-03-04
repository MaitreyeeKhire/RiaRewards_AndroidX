package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.ADD_TO_CART;
import static com.pegasusgroup.riarewards.interfaces.AppConstants.CLIENT_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetail extends BaseFragment {

    private SingleCategoryMd singleCategoryMd;
    private ImageView imgProduct;
    private AppCompatTextView txtProductName;
    private AppCompatTextView txtMemberPrice;
    private AppCompatTextView txtPrice;
    private AppCompatTextView txtPoint;
    private AppCompatTextView txtProductDetail;

    private AppCompatButton cmdPayWithDollar;
    private AppCompatButton cmdPayWithPoint;
    private AppCompatButton cmdPayWithDollarAndPoint;

    private boolean readMore = false;
    private AppCompatButton cmdBuyNow;
    private String redeem_button_img;

    public ProductDetail() {
        // Required empty public constructor
    }

    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    protected void initComponents(View view) {
        singleCategoryMd = (SingleCategoryMd) Objects.requireNonNull(Objects.requireNonNull(getArguments()).getSerializable("singleCategoryMd"));

        CommonMethods.printLog(Objects.requireNonNull(singleCategoryMd).toString());

        readMore = (getArguments().getString("readMore") != null);

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtMemberPrice = findViewById(R.id.txtMemberPrice);
        txtProductDetail = findViewById(R.id.txtProductDetail);
        txtPrice = findViewById(R.id.txtPrice);
        txtPoint = findViewById(R.id.txtPoint);

        cmdPayWithDollar = findViewById(R.id.cmdPayWithDollar);
        cmdPayWithPoint = findViewById(R.id.cmdPayWithPoint);
        cmdPayWithDollarAndPoint = findViewById(R.id.cmdPayWithDollarAndPoint);

        cmdBuyNow = findViewById(R.id.cmdBuyNow);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setListeners() {

        if (singleCategoryMd.getName() != null)
            txtProductName.setText(singleCategoryMd.getName());
        if (singleCategoryMd.getDetails() != null)
            txtProductDetail.setText(Html.fromHtml(singleCategoryMd.getDetails()));


        if (singleCategoryMd.getPrice() != null) {
            txtMemberPrice.setText("Member Price : $" + singleCategoryMd.getPrice());
            if (singleCategoryMd.getName().contains("$")) {
//                String originalPrice = singleCategoryMd.getName().substring(singleCategoryMd.getName().lastIndexOf("$"));
//                txtPrice.setText("RRP : $" + originalPrice);
                String price;
                String[] parts = singleCategoryMd.getName().split(" ");
                for (String part : parts) {
                    if (part.contains("$")) {
                        price = part;
                        txtPrice.setText("RRP : " + price);
                        break;
                    }
                }
            }
        }

        if (singleCategoryMd.getPay_type() != null) {
            txtPoint.setVisibility(View.VISIBLE);
            if (!singleCategoryMd.getPay_type().equalsIgnoreCase("null"))
                txtPoint.setText("Points : " + Math.round(Double.parseDouble(singleCategoryMd.getPoints())
                        * Double.parseDouble(sessionManager.getPointsConversion())));
        } else {
            txtPoint.setVisibility(View.GONE);
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
        } else if (readMore) {
            cmdPayWithDollar.setVisibility(View.GONE);
            cmdPayWithPoint.setVisibility(View.GONE);
            cmdPayWithDollarAndPoint.setVisibility(View.GONE);
            cmdBuyNow.setVisibility(View.GONE);
        } else {
            if (singleCategoryMd.getPay_type() != null && singleCategoryMd.getPay_type().equalsIgnoreCase("Pay")) {
                cmdPayWithDollar.setVisibility(View.VISIBLE);
                cmdPayWithPoint.setVisibility(View.GONE);
                cmdPayWithDollarAndPoint.setVisibility(View.GONE);
            } else if (singleCategoryMd.getPay_type() != null && singleCategoryMd.getPay_type().equalsIgnoreCase("Points")) {
                cmdPayWithDollar.setVisibility(View.GONE);
                cmdPayWithPoint.setVisibility(View.VISIBLE);
                cmdPayWithDollarAndPoint.setVisibility(View.GONE);
            } else if (singleCategoryMd.getPay_type() != null && singleCategoryMd.getPay_type().equalsIgnoreCase("PayPoints")) {
                cmdPayWithDollar.setVisibility(View.VISIBLE);
                cmdPayWithPoint.setVisibility(View.VISIBLE);
                cmdPayWithDollarAndPoint.setVisibility(View.VISIBLE);
            }
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
////                if (Integer.parseInt(sessionManager.getUserPoint()) >= Integer.parseInt(singleCategoryMd.getPoints())) {
//                if (singleCategoryMd.getPrice() != null && !singleCategoryMd.getPrice().equalsIgnoreCase("null")) {
//                    if (Double.parseDouble(sessionManager.getUserPoint()) >= Math.round(Double.parseDouble(singleCategoryMd.getPrice())
//                            * Double.parseDouble(sessionManager.getPointsConversion()))) {
//                        addToCart(singleCategoryMd.getId(), "Points");
//                    } else {
//                        showToast("Sorry, you do not have enough points to buy this product");
//                    }
//                } else {
//                    showToast("Price is not available");
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
//                        CommonMethods.printLog("Add to Cart URL :" + ADD_TO_CART);
                            CommonMethods.printLog("Response : " + response.toString());
//                        CommonMethods.printLog("Params : " + jsonObject.toString());
                            try {
                                if (response.has("cart_id")) {
                                    String cart_id = response.getString("cart_id");
                                    CommonMethods.printLog("cart_id: " + cart_id);
                                    sessionManager.setCartId(cart_id);
//                                baseAppCompatActivity.imgBack.setVisibility(View.INVISIBLE);
                                    baseAppCompatActivity.getSupportFragmentManager().popBackStack();
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
//                        onBackPressed();
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
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