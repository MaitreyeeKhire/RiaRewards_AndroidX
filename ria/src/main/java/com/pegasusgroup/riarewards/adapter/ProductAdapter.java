package com.pegasusgroup.riarewards.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.fragments.ProductDetail;
import com.pegasusgroup.riarewards.fragments.VoucherWebView;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.interfaces.OnFavouriteClickListener;
import com.pegasusgroup.riarewards.model.SingleCategoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<SingleCategoryMd> singleCategoryMds;
    private OnFavouriteClickListener onFavouriteClickListener;
    private SessionManager sessionManager;
    private FragmentChanger fragmentChanger;

    public ProductAdapter(Context context, ArrayList<SingleCategoryMd> singleCategoryMds) {
        mContext = context;
        this.singleCategoryMds = singleCategoryMds;
        onFavouriteClickListener = (OnFavouriteClickListener) context;
        sessionManager = new SessionManager(context);
    }

    public ProductAdapter(Context context, ArrayList<SingleCategoryMd> singleCategoryMds,
                          OnFavouriteClickListener onFavouriteClickListener, FragmentChanger fragmentChanger) {
        mContext = context;
        this.singleCategoryMds = singleCategoryMds;
        this.onFavouriteClickListener = onFavouriteClickListener;
        this.fragmentChanger = fragmentChanger;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(singleCategoryMds.get(position));
        viewHolder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return singleCategoryMds.size();
    }

    private void redeemTracker(String product_id) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("cid", sessionManager.getClientId());
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView txtProductName;
        private AppCompatTextView txtMemberPrice;
        private AppCompatTextView txtPrice;
        private AppCompatImageView imgProduct;
        private SingleCategoryMd singleCategoryMd;
        private AppCompatImageView imgFavourite;
        private AppCompatButton cmdBuyNow;
        private String redeem_button_img;
        private AppCompatTextView txtPoint;
        private AppCompatTextView txtHeightLight;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtMemberPrice = itemView.findViewById(R.id.txtMemberPrice);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtPoint = itemView.findViewById(R.id.txtPoint);
            txtHeightLight = itemView.findViewById(R.id.txtHighLight);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgFavourite = itemView.findViewById(R.id.imgFavourite);
            cmdBuyNow = itemView.findViewById(R.id.cmdBuyNow);
            imgFavourite.setOnClickListener(this);
            itemView.setOnClickListener(this);
            cmdBuyNow.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        private void setData(SingleCategoryMd singleCategoryMd) {
            this.singleCategoryMd = singleCategoryMd;
            txtProductName.setText(singleCategoryMd.getName());

            if (singleCategoryMd.getPay_type() != null) {
                txtPoint.setVisibility(View.VISIBLE);
                if (!singleCategoryMd.getPay_type().equalsIgnoreCase("null"))
                    txtPoint.setText("Points : " + Math.round(Double.parseDouble(singleCategoryMd.getPoints())
                            * Double.parseDouble(sessionManager.getPointsConversion())));
            } else {
                txtPoint.setVisibility(View.GONE);
            }

            if (singleCategoryMd.getHighlight() != null) {
                txtHeightLight.setVisibility(View.VISIBLE);
                txtHeightLight.setText(singleCategoryMd.getHighlight());
            } else {
                txtHeightLight.setVisibility(View.GONE);
            }

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
                        .placeholder(R.drawable.profile_image)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgProduct);
            }

            imgProduct.setClipToOutline(true);

            if (singleCategoryMd.getIs_favourite().equals("1")) {
                imgFavourite.setImageResource(R.drawable.favourite_selected);
            } else if (singleCategoryMd.getIs_favourite().equals("0")) {
                imgFavourite.setImageResource(R.drawable.favourite_unselected);
            }

            if (singleCategoryMd.getPrice() != null) {
                txtMemberPrice.setVisibility(View.VISIBLE);
                txtMemberPrice.setText("Member Price : $" + singleCategoryMd.getPrice());
            } else {
                txtMemberPrice.setVisibility(View.GONE);
            }

            if (singleCategoryMd.getPrice() != null) {
                txtPrice.setVisibility(View.VISIBLE);
                if (singleCategoryMd.getName().contains("$")) {
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
            } else {
                txtPrice.setVisibility(View.GONE);
            }

            redeem_button_img = singleCategoryMd.getRedeem_button_img();
            int web_coupon = Integer.parseInt(singleCategoryMd.getWeb_coupon());
            int user_used_count = Integer.parseInt(singleCategoryMd.getUser_used_count());
            int used_count = Integer.parseInt(singleCategoryMd.getUsed_count());

            if (redeem_button_img != null && !redeem_button_img.isEmpty()) {
                if (!TextUtils.isEmpty(redeem_button_img)) {
                    cmdBuyNow.setText("Redeem Now");
                }
            } else if ((web_coupon == 1) && (user_used_count > 0 || user_used_count == -1) && (used_count > 0 || used_count == -1)) {
                cmdBuyNow.setText("Get Now");
            } else if (singleCategoryMd.getProduct_quantity() != null && !singleCategoryMd.getProduct_quantity().equals("")) {
                cmdBuyNow.setText("Buy Now");
            } else {
                cmdBuyNow.setText("Read More");
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cmdBuyNow) {
                if (cmdBuyNow.getText().equals("Buy Now")) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("singleCategoryMd", singleCategoryMd);
                    ProductDetail productDetail = new ProductDetail();
                    productDetail.setArguments(bundle);
                    fragmentChanger.change(productDetail);
//                    mContext.startActivity(new Intent(mContext, ProductDetail.class)
//                            .putExtras(bundle));
//                    Activity activity = (Activity) mContext;
//                    activity.overridePendingTransition(0, 0);
                } else if (cmdBuyNow.getText().equals("Redeem Now")) {
                    Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(redeem_button_img));
                    mContext.startActivity(browse);
                    redeemTracker(singleCategoryMd.getId());
                } else if (cmdBuyNow.getText().equals("Get Now")) {
                    String voucherAPI = "https://www.myrewards.com.au/newapp/" + "get_voucher.php?uid="
                            + sessionManager.getUserId()
                            + "&cid=" + sessionManager.getClientId()
                            + "&mid=" + singleCategoryMd.getMerchant_id()
                            + "&pid=" + singleCategoryMd.getId();
                    String pid = singleCategoryMd.getId();

                    //Intent intent = new Intent(mContext, VoucherWebView.class);
//                    intent.putExtra("voucherAPI", voucherAPI);
//                    intent.putExtra("pid", pid);
//                    mContext.startActivity(intent);
                    Bundle bundle = new Bundle();
                    bundle.putString("pid", pid);
                    bundle.putString("voucherAPI", voucherAPI);
                    VoucherWebView voucherWebView = new VoucherWebView();
                    voucherWebView.setArguments(bundle);
                    fragmentChanger.change(voucherWebView);
                } else if (cmdBuyNow.getText().equals("Read More")) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("singleCategoryMd", singleCategoryMd);
                    if (cmdBuyNow.getText().equals("Read More"))
                        bundle.putString("readMore", "readMore");
                    ProductDetail productDetail = new ProductDetail();
                    productDetail.setArguments(bundle);
                    fragmentChanger.change(productDetail);
                }
            } else if (v.getId() == R.id.imgFavourite) {
                if (singleCategoryMd.getIs_favourite().equals("1")) {
                    onFavouriteClickListener.favouriteClick(sessionManager.getUserId(), singleCategoryMd.getId(), false, getAdapterPosition());
                    imgFavourite.setImageResource(R.drawable.favourite_unselected);
                } else if (singleCategoryMd.getIs_favourite().equals("0")) {
                    onFavouriteClickListener.favouriteClick(sessionManager.getUserId(), singleCategoryMd.getId(), true, getAdapterPosition());
                    imgFavourite.setImageResource(R.drawable.favourite_selected);
                }
            } else if (v.getId() == itemView.getId()) {
                if (cmdBuyNow.getText().equals("Get Now")) {
                    String voucherAPI = "https://www.myrewards.com.au/newapp/" + "get_voucher.php?uid="
                            + sessionManager.getUserId()
                            + "&cid=" + sessionManager.getClientId()
                            + "&mid=" + singleCategoryMd.getMerchant_id()
                            + "&pid=" + singleCategoryMd.getId();
                    String pid = singleCategoryMd.getId();

                    //Intent intent = new Intent(mContext, VoucherWebView.class);
//                    intent.putExtra("voucherAPI", voucherAPI);
//                    intent.putExtra("pid", pid);
//                    mContext.startActivity(intent);
                    Bundle bundle = new Bundle();
                    bundle.putString("pid", pid);
                    bundle.putString("voucherAPI", voucherAPI);
                    VoucherWebView voucherWebView = new VoucherWebView();
                    voucherWebView.setArguments(bundle);
                    fragmentChanger.change(voucherWebView);
                } else if (cmdBuyNow.getText().equals("Redeem Now")) {
                    Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(redeem_button_img));
                    mContext.startActivity(browse);
                    redeemTracker(singleCategoryMd.getId());
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("singleCategoryMd", singleCategoryMd);
                    if (cmdBuyNow.getText().equals("Read More"))
                        bundle.putString("readMore", "readMore");
                    ProductDetail productDetail = new ProductDetail();
                    productDetail.setArguments(bundle);
                    fragmentChanger.change(productDetail);
                }
            }
        }
    }
}