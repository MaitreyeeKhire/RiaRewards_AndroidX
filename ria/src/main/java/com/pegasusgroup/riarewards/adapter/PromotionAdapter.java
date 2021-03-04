package com.pegasusgroup.riarewards.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.FacebookShareListener;
import com.pegasusgroup.riarewards.interfaces.PromotionListener;
import com.pegasusgroup.riarewards.model.PromotionMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PromotionMd> promotionMds;
    private SessionManager sessionManager;
    private FacebookShareListener facebookShareListener;
    private PromotionListener promotionListener;

    public PromotionAdapter(Context context, ArrayList<PromotionMd> promotionMds) {
        mContext = context;
        this.promotionMds = promotionMds;
        sessionManager = new SessionManager(context);
        facebookShareListener = (FacebookShareListener) context;
        promotionListener = (PromotionListener) context;
    }

    public PromotionAdapter(Context context, ArrayList<PromotionMd> promotionMds,
                            FacebookShareListener facebookShareListener, PromotionListener promotionListener) {
        mContext = context;
        this.promotionMds = promotionMds;
        sessionManager = new SessionManager(context);
        this.facebookShareListener = facebookShareListener;
        this.promotionListener = promotionListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.promotion_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(promotionMds.get(position));
    }

    @Override
    public int getItemCount() {
        return promotionMds.size();
    }

    /**
     * @description This method is used to call Api
     */
    private void callPromotionClaim(final String promotionId) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            CommonMethods.printLog("URL : " + AppConstants.PROMOTION_CLAIM);

            StringRequest otpReq = new StringRequest(Request.Method.POST, AppConstants.PROMOTION_CLAIM, new Response.Listener<String>() {
                JSONObject jsonObject;

                @Override
                public void onResponse(String response) {
                    try {
                        CommonMethods.printLog("response : " + response);
                        if (response != null) {
                            jsonObject = new JSONObject(response);

                            if (jsonObject.has("status")) {
                                Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                promotionListener.refreshPromotion();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    params.put("internal_claimed", "1");
                    params.put("fb_claimed", "0");
                    params.put("promotion_id", promotionId);
                    CommonMethods.printLog(params.toString());
                    return params;
                }
            };

            requestQueue.add(otpReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imgPromotion;
        private AppCompatTextView txtPoints;
        private AppCompatTextView txtSubject;
        private AppCompatTextView txtDetail;
        private ShareButton cmdShareEarn;
        private AppCompatButton cmdViewEarn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPromotion = itemView.findViewById(R.id.imgPromotion);
            txtPoints = itemView.findViewById(R.id.txtPoints);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtDetail = itemView.findViewById(R.id.txtDetail);
            cmdShareEarn = itemView.findViewById(R.id.cmdShareEarn);
            cmdViewEarn = itemView.findViewById(R.id.cmdViewEarn);
        }

        @SuppressLint("SetTextI18n")
        void setData(final PromotionMd promotionMd) {

            Glide.with(mContext)
                    .load("https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/Ria-rewards/" + promotionMd.getFb_image())
                    .into(imgPromotion);

            if (promotionMd.getPoints().length() > 0) {
                txtPoints.setVisibility(View.VISIBLE);
                txtPoints.setText(promotionMd.getPoints() + "\n Points!");
            } else {
                txtPoints.setVisibility(View.GONE);
            }

            txtSubject.setText(Html.fromHtml(promotionMd.getSubject()));
            txtDetail.setText(Html.fromHtml(promotionMd.getDetails()));

            if (promotionMd.getInternal_claimed().equals("0")) {
                cmdViewEarn.setVisibility(View.VISIBLE);
            } else {
                cmdViewEarn.setVisibility(View.GONE);
            }

            if (promotionMd.getFb_claimed().equals("0")) {
                cmdShareEarn.setVisibility(View.VISIBLE);
            } else {
                cmdShareEarn.setVisibility(View.GONE);
            }

            cmdShareEarn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Please wait...", Toast.LENGTH_SHORT).show();
                    facebookShareListener.onPostShared(promotionMd.getId());
                }
            });

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(promotionMd.getFb_url()))
                    .build();
            cmdShareEarn.setShareContent(linkContent);

            cmdViewEarn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPromotionClaim(promotionMd.getId());
                }
            });
        }
    }
}