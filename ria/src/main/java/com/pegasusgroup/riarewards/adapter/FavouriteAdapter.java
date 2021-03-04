package com.pegasusgroup.riarewards.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.activity.BaseAppCompatActivity;
import com.pegasusgroup.riarewards.fragments.ProductDetail;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.FragmentChanger;
import com.pegasusgroup.riarewards.model.SingleCategoryMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SingleCategoryMd> singleCategoryMds;
    private SessionManager sessionManager;
    private FragmentChanger fragmentChanger;

    public FavouriteAdapter(Context context, ArrayList<SingleCategoryMd> singleCategoryMds) {
        mContext = context;
        this.singleCategoryMds = singleCategoryMds;
        sessionManager = new SessionManager(context);
    }

    public FavouriteAdapter(Context context, ArrayList<SingleCategoryMd> singleCategoryMds, FragmentChanger fragmentChanger) {
        mContext = context;
        this.singleCategoryMds = singleCategoryMds;
        sessionManager = new SessionManager(context);
        this.fragmentChanger = fragmentChanger;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favourite_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(singleCategoryMds.get(position));
    }

    @Override
    public int getItemCount() {
        return singleCategoryMds.size();
    }

    /**
     * @param userId    User Id
     * @param productId ProductList Id
     * @param flag      True or False
     * @description This method is used to call Favourite API
     */
    private void setFavApiCall(final String userId, final String productId, final boolean flag, final SingleCategoryMd singleCategoryMd) {
        ((BaseAppCompatActivity) mContext).progressDialog.setMessage("Loading");
        ((BaseAppCompatActivity) mContext).progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest postReq = new StringRequest(Request.Method.POST, AppConstants.SET_FAV, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        jsonObject.getJSONObject("status");
                        CommonMethods.printLog("onResponse:setFavorite " + jsonObject);
                        JSONObject status = jsonObject.getJSONObject("status");
//                        JSONObject obj4 = null;
                        //Getting all the keys inside json object with key- pages
                        Iterator<String> keys = status.keys();
                        while (keys.hasNext()) {
                            String keyValue = keys.next();

                            if (keyValue.equals("200")) {
                                if (flag) {
                                    singleCategoryMd.setIs_favourite("1");
                                } else {
                                    singleCategoryMd.setIs_favourite("0");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        ((BaseAppCompatActivity) mContext).progressDialog.dismiss();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ((BaseAppCompatActivity) mContext).progressDialog.dismiss();
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatImageView imgProduct;
        private AppCompatTextView txtProductName;
        private AppCompatTextView txtDetail1;
        private AppCompatTextView txtDetail2;
        private AppCompatImageView imgFavourite;
        private AppCompatTextView txtPoint;
        private AppCompatTextView txtHeightLight;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtDetail1 = itemView.findViewById(R.id.txtDetail1);
            txtDetail2 = itemView.findViewById(R.id.txtDetail2);
            txtPoint = itemView.findViewById(R.id.txtPoint);
            txtHeightLight = itemView.findViewById(R.id.txtHighLight);
            imgFavourite = itemView.findViewById(R.id.imgFavourite);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setData(final SingleCategoryMd singleCategoryMd) {
//            if (singleCategoryMd.getPoints() != null) {
//                txtPoint.setVisibility(View.VISIBLE);
//                if (!singleCategoryMd.getPoints().equalsIgnoreCase("null"))
//                    txtPoint.setText("Points : " + singleCategoryMd.getPoints());
//            } else {
//                txtPoint.setVisibility(View.GONE);
//            }

            if (singleCategoryMd.getPay_type() != null) {
                txtPoint.setVisibility(View.VISIBLE);
                if (!singleCategoryMd.getPay_type().equalsIgnoreCase("null") &&
                        !singleCategoryMd.getPoints().equals("0"))
                    txtPoint.setText("Points : " + Math.round(Double.parseDouble(singleCategoryMd.getPoints())
                            * Double.parseDouble(sessionManager.getPointsConversion())));
                else
                    txtPoint.setText("");

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
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgProduct);
            }

            imgFavourite.setImageResource(R.drawable.favourite_selected);

            txtProductName.setText(singleCategoryMd.getName());

            imgFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (singleCategoryMd.getIs_favourite().equals("1")) {
                        setFavApiCall(new SessionManager(mContext).getUserId(), singleCategoryMd.getId(), false, singleCategoryMd);
                        imgFavourite.setImageResource(R.drawable.favourite_unselected);
                        ((BaseAppCompatActivity) mContext).putDetails(AppConstants.CLIENT_ID, sessionManager.getUserId(), "android", ((BaseAppCompatActivity) mContext).androidId,
                                null, "", singleCategoryMd.getId(), singleCategoryMd.getName(), "Product List", ((BaseAppCompatActivity) mContext).reportDate,
                                null, "Product favourite removed");
                    } else if (singleCategoryMd.getIs_favourite().equals("0")) {
                        setFavApiCall(new SessionManager(mContext).getUserId(), singleCategoryMd.getId(), true, singleCategoryMd);
                        imgFavourite.setImageResource(R.drawable.favourite_selected);
                        ((BaseAppCompatActivity) mContext).putDetails(AppConstants.CLIENT_ID, sessionManager.getUserId(), "android", ((BaseAppCompatActivity) mContext).androidId,
                                null, "", singleCategoryMd.getId(), singleCategoryMd.getName(), "Product List", ((BaseAppCompatActivity) mContext).reportDate,
                                null, "Product favourite added");
                    }
                }
            });

            if (singleCategoryMd.getPrice() != null) {
                txtDetail1.setVisibility(View.VISIBLE);
                txtDetail1.setText("Member Price : $" + singleCategoryMd.getPrice());
                if (singleCategoryMd.getName().contains("$")) {
                    txtDetail2.setVisibility(View.VISIBLE);
                    String price = singleCategoryMd.getName().substring(singleCategoryMd.getName().indexOf("$"));
                    txtDetail2.setText("RRP : " + price);
                } else {
                    txtDetail2.setVisibility(View.GONE);
                }
            } else {
                txtDetail1.setVisibility(View.GONE);
                txtDetail2.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("singleCategoryMd", singleCategoryMds.get(getAdapterPosition()));
            ProductDetail productDetail = new ProductDetail();
            productDetail.setArguments(bundle);
            fragmentChanger.change(productDetail, true);
        }
    }
}