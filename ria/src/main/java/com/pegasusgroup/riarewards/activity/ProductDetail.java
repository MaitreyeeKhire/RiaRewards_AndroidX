package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

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

public class ProductDetail extends BaseAppCompatActivity {

    private SingleCategoryMd singleCategoryMd;
    private ImageView imgProduct;
    private AppCompatTextView txtProductName;
    private AppCompatTextView txtMemberPrice;
    private AppCompatTextView txtPrice;
    private AppCompatTextView txtProductDetail;

    private AppCompatButton cmdPayWithDollar;
    private AppCompatButton cmdPayWithPoint;
    private AppCompatButton cmdPayWithDollarAndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        singleCategoryMd = (SingleCategoryMd) Objects.requireNonNull(getIntent().getExtras()).getSerializable("singleCategoryMd");

        CommonMethods.printLog(Objects.requireNonNull(singleCategoryMd).toString());

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtMemberPrice = findViewById(R.id.txtMemberPrice);
        txtProductDetail = findViewById(R.id.txtProductDetail);
        txtPrice = findViewById(R.id.txtPrice);

        cmdPayWithDollar = findViewById(R.id.cmdPayWithDollar);
        cmdPayWithPoint = findViewById(R.id.cmdPayWithPoint);
        cmdPayWithDollarAndPoint = findViewById(R.id.cmdPayWithDollarAndPoint);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        txtProductName.setText(singleCategoryMd.getName());
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

        cmdPayWithDollar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(singleCategoryMd.getId(), "Pay");
            }
        });

        cmdPayWithPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(sessionManager.getUserPoint()) >= Integer.parseInt(singleCategoryMd.getPoints())) {
                    addToCart(singleCategoryMd.getId(), "Points");
                } else {
                    showToast("Sorry, you do not have enough points to buy this product");
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
            Glide.with(ProductDetail.this).load(imageUrl).fitCenter()
                    .crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgProduct);
        }
    }

    private void addToCart(final String product_id, String payType) {
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(ProductDetail.this);
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

    @SuppressWarnings("deprecation")
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                ProductDetail.this).create();
        alertDialog.setTitle("Response");
        alertDialog.setMessage("Product added to cart");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getCardItemCount();
            }
        });

        alertDialog.show();
    }

    private void getCardItemCount() {
        String countURL = AppConstants.CART_ITEMS + "uid=" + sessionManager.getUserId() + "&cart_id=" + sessionManager.getCartId();
        RequestQueue requestQueue = Volley.newRequestQueue(ProductDetail.this);

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
                        onBackPressed();
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