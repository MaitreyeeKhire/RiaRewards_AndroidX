package com.pegasusgroup.riarewards.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.activity.BaseAppCompatActivity;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.ShippingCostListener;
import com.pegasusgroup.riarewards.interfaces.TotalListener;
import com.pegasusgroup.riarewards.model.CartMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CartMd> cartMds;
    private float maxGroupShippingCost;
    private int userPoints;
    private SessionManager sessionManager;
    private int userId;
    private TotalListener totalListener;
    private ArrayList<Integer> outOfStockProducts;
    private boolean isOutOfStock;
    private String extra;
    private BaseAppCompatActivity baseAppCompatActivity;
    private ShippingCostListener shippingCostListener;

    public CartAdapter(Context context, ArrayList<CartMd> cartMds, float maxGroupShippingCost) {
        mContext = context;
        this.cartMds = cartMds;
        this.maxGroupShippingCost = maxGroupShippingCost;
        sessionManager = new SessionManager(context);
        userPoints = Integer.parseInt(sessionManager.getUserPoint());
        userId = Integer.parseInt(sessionManager.getUserId());
        this.totalListener = (TotalListener) context;
        outOfStockProducts = new ArrayList<>();
        isOutOfStock = false;
        extra = "";
        shippingCostListener = (ShippingCostListener) this;
    }

    public CartAdapter(BaseAppCompatActivity baseAppCompatActivity, Context context,
                       ArrayList<CartMd> cartMds, float maxGroupShippingCost, TotalListener totalListener, ShippingCostListener shippingCostListener) {
        this.baseAppCompatActivity = baseAppCompatActivity;
        mContext = context;
        this.cartMds = cartMds;
        this.maxGroupShippingCost = maxGroupShippingCost;
        sessionManager = new SessionManager(context);
        userPoints = Integer.parseInt(sessionManager.getUserPoint());
        userId = Integer.parseInt(sessionManager.getUserId());
        this.totalListener = totalListener;
        outOfStockProducts = new ArrayList<>();
        isOutOfStock = false;
        extra = "";
        this.shippingCostListener = shippingCostListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mycart_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setData(cartMds.get(position));
    }

    @Override
    public int getItemCount() {
        return cartMds.size();
    }

    public String getExtra() {
        return extra;
    }

    private void removeFromCart(final int cart_id, final int pid, final CartMd cartMd) {
        baseAppCompatActivity.progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        //https://www.myrewards.com.au/newapp/remove_cart_product.php?uid=6799719&client_id=1963&cart_id=24472&pid=1040967
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, AppConstants.SERVER_ROOT + "remove_cart_product.php?uid=" + userId
                        + "&client_id=" + sessionManager.getClientId() + "&cart_id=" + cart_id + "&pid=" + pid, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonMethods.printLog("Remove Cart : " + response.toString());
                        cartMds.remove(cartMd);
                        notifyDataSetChanged();

                        try {
                            JSONObject obj4 = null;
                            Iterator<String> keys = response.keys();
                            while (keys.hasNext()) {
                                String keyValue = keys.next();
                                obj4 = response.getJSONObject(keyValue);

                                //getting string values with keys- pageid and title
                                String status = obj4.getString("200");
                                if (status.equalsIgnoreCase("success")) {
                                    //showAlert("success");
                                    //totalListener.total(0.0);
                                    // Updating Points
                                    if (cartMd.getPayType().equalsIgnoreCase("Points")) {
                                        float points = sessionManager.getPoints();
                                        float currPoints = (cartMd.getQty() * Float.parseFloat(cartMd.getPrice()));
                                        sessionManager.setPoints(points - currPoints);
                                    }
                                    callGetCardItemCount();
                                    //getCartDetail(url);
                                    //txt_total.setText("$" + (response.getString("total")));
                                } else {
                                    showAlert(response.toString());
                                }
                            }
                        } catch (Exception iox) {
                            CommonMethods.printLog("iox: " + iox);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        baseAppCompatActivity.progressDialog.dismiss();
                    }
                });

        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    /**
     * @description This method is used to call Api
     */
    private void callGetCardItemCount() {
        baseAppCompatActivity.progressDialog.show();
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
                                totalListener.total(0.0);
                                sessionManager.setCartCount(jsonObject.getString("cart_items"));
//                                cart_counter.setText(sessionManager.getCartCount());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            baseAppCompatActivity.progressDialog.dismiss();
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("", "Error: " + error.getMessage());
                    baseAppCompatActivity.progressDialog.dismiss();
                }

            });

            requestQueue.add(postReq);
        }
    }


    @SuppressWarnings("deprecation")
    private void showAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Ria Rewards");
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
        Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positive.setTextColor(Color.parseColor("#FFFF0400"));
    }

    //    private void UpdateCart(final int cart_id, final int cart_item_id, final int max_group_ship_cost,
//                            final int pid, final int qty, final int shippingtype, final int uid, final String payType, final String operation) {
    private void UpdateCart(final CartMd cartMd, final int uid, final String operation) {
        final RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        baseAppCompatActivity.progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cart_id", cartMd.getCart_id());
            jsonObject.put("cart_item_id", cartMd.getCart_item_id());
            jsonObject.put("max_group_ship_cost", cartMd.getMax_group_ship_cost());
            jsonObject.put("pid", cartMd.getPid());
            jsonObject.put("qty", cartMd.getQty());
            jsonObject.put("shippingtype", cartMd.getShippingtype());
            jsonObject.put("uid", uid);
            CommonMethods.printLog(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, AppConstants.UPDATE_CART, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CommonMethods.printLog("response " + response.toString());
                            // response {"status":{"200":"success"},"total":237.5}

                            if (operation.equalsIgnoreCase("plus")) {
                                if (cartMd.getPayType().equalsIgnoreCase("Points")) {
                                    float points = sessionManager.getPoints();
                                    sessionManager.setPoints(points + Float.parseFloat(cartMd.getPrice()));
                                }
                            } else if (operation.equalsIgnoreCase("minus")) {
                                if (cartMd.getPayType().equalsIgnoreCase("Points")) {
                                    float points = sessionManager.getPoints();
                                    sessionManager.setPoints(points - Float.parseFloat(cartMd.getPrice()));
                                }
                            }

                            totalListener.total(0.0);

                            if (!response.getString("status").contains("200")) {
                                showAlert(response.toString());
                            }
                        } catch (Exception iox) {
                            iox.printStackTrace();
                        } finally {
                            baseAppCompatActivity.progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        baseAppCompatActivity.progressDialog.dismiss();
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imgProduct;
        private AppCompatImageView imgRemove;
        private AppCompatTextView txtProductName;
        private AppCompatTextView txtMemberPrice;
        private AppCompatTextView txtRegularPrice;
        private AppCompatImageView txtMinus;
        private AppCompatImageView txtPlus;
        private AppCompatTextView txtCounter;
        private int qty = 0;
        private int position;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtMemberPrice = itemView.findViewById(R.id.txtMemberPrice);
            txtRegularPrice = itemView.findViewById(R.id.txtRegularPrice);
            txtMinus = itemView.findViewById(R.id.txtMinus);
            txtPlus = itemView.findViewById(R.id.txtPlus);
            txtCounter = itemView.findViewById(R.id.txtCounter);
        }

        @SuppressLint("SetTextI18n")
        void setData(final CartMd cartMd) {
            position = getAdapterPosition();
            qty = cartMd.getQty();

            String path = "";
            switch (cartMd.getDisplayImage()) {
                case "Product Image":
                    path = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/product_image/" + cartMd.getCart_product_id() + ".jpg";
                    break;
                case "Merchant Logo":
                    path = "https://s3-ap-southeast-2.amazonaws.com/myrewards-media/webroot/files/merchant_logo/" + cartMd.getMerchant_id() + ".png";
                    break;
            }

            Glide.with(mContext).load(path)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imgProduct);
            txtProductName.setText(cartMd.getItemName());

            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Remove");
                    builder.setMessage("Sure you want to remove?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @SuppressLint("UseValueOf")
                                public void onClick(DialogInterface dialog, int id) {
                                    baseAppCompatActivity.progressDialog.show();
                                    if (outOfStockProducts.size() > 0) {
                                        if (outOfStockProducts.contains(cartMds.get(position).getPid())) {
                                            //Integer productId = iterator.next();
                                            //if (productId == shopModels.get(position).getPid()){
                                            outOfStockProducts.remove(new Integer(cartMds.get(position).getPid()));
                                            //}
                                            if (outOfStockProducts.size() == 0) {
                                                isOutOfStock = false;
                                            }
                                        }
                                    } else {
                                        isOutOfStock = false;
                                    }

                                    if (cartMd.getShippingtype() == 4)
                                        shippingCostListener.updateShipping();
                                    //Toast.makeText(mContext, "Removing item...", Toast.LENGTH_LONG).show();
                                    removeFromCart(cartMds.get(position).getCart_id(), cartMds.get(position).getPid(), cartMds.get(position));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    Button negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    negative.setTextColor(Color.parseColor("#FFFF0400"));
                    positive.setTextColor(Color.parseColor("#FFFF0400"));
                }
            });

            txtMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        qty -= 1;
//                        txtCounter.setText(String.valueOf(qty));
                    int wareHousePdtQnty = Integer.parseInt(cartMds.get(position).getWarehouse_product_quantity());
                    if (outOfStockProducts.size() > 0) {
                        isOutOfStock = true;
                        //for(int productId : outOfStockProducts){
//                            for(Iterator<Integer> iterator = outOfStockProducts.iterator(); iterator.hasNext();){
////                                Integer productId = iterator.next();
                        if (outOfStockProducts.contains(cartMds.get(position).getPid())) {
                            //Integer productId = outOfStockProducts.get(i);
                            //if(productId == cartMds.get(position).getPid()){
                            if (wareHousePdtQnty > 0 && (cartMds.get(position).getQty() - 1) > wareHousePdtQnty) {
                                if (cartMds.get(position).getQty() > 1) {
                                    cartMds.get(position).setQty(cartMds.get(position).getQty() - 1);
                                    notifyItemChanged(position);
//                                    UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
//                                            cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
                                    UpdateCart(cartMd, userId, "minus");

                                    outOfStockProducts.remove(new Integer(cartMds.get(position).getPid()));
                                    //outOfStockProducts.remove(i);
                                    if (outOfStockProducts.size() == 0) {
                                        isOutOfStock = false;
                                    }
                                }
                            } else {
                                isOutOfStock = false;
                                if (cartMds.get(position).getQty() > 1) {
                                    cartMds.get(position).setQty(cartMds.get(position).getQty() - 1);
                                    notifyItemChanged(position);
//                                    UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
//                                            cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
                                    UpdateCart(cartMd, userId, "minus");
                                }
                            }
                            //}
                        } else {
                            if (cartMds.get(position).getQty() > 1) {
                                cartMds.get(position).setQty(cartMds.get(position).getQty() - 1);
                                notifyItemChanged(position);
//                                UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
//                                        cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
                                UpdateCart(cartMd, userId, "minus");
                            }
                        }
                    } else {
                        if (cartMds.get(position).getQty() > 1) {
                            isOutOfStock = false;
                            cartMds.get(position).setQty(cartMds.get(position).getQty() - 1);
                            notifyItemChanged(position);
//                            UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
//                                    cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
                            UpdateCart(cartMd, userId, "minus");
                        }
                    }
                }
            });

            txtCounter.setText(String.valueOf(cartMd.getQty()));

            txtPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    qty += 1;
//                    txtCounter.setText(String.valueOf(qty));

                    int wareHousePdtQnty = Integer.parseInt(cartMds.get(position).getWarehouse_product_quantity());
                    if (wareHousePdtQnty > 0 && (cartMds.get(position).getQty() + 1) <= wareHousePdtQnty) {

                    } else if (wareHousePdtQnty == -1) {

                    } else {
                        outOfStockProducts.add(cartMds.get(position).getPid());
                        isOutOfStock = true;
                    }

                    if (cartMds.get(position).getPayType().equalsIgnoreCase("Points")) {
//                        if (((cartMds.get(position).getQty() + 1) * cartMds.get(position).getPayEqualPoints()) < userPoints) {
//                        if (Math.round(Double.parseDouble(cartMds.get(position).getPrice()) * (cartMds.get(position).getQty() + 1)) < userPoints) {
//                            cartMds.get(position).setQty(cartMds.get(position).getQty() + 1);
//                            notifyItemChanged(position);
////                            UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
////                                    cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
//                            UpdateCart(cartMd, userId, "plus");
//                        } else {
//                            Toast.makeText(mContext, "You have no enough point to add this item", Toast.LENGTH_LONG).show();
//                          }
                        if (Math.round(sessionManager.getPoints() + Double.parseDouble(cartMds.get(position).getPrice())) < userPoints) {
                            cartMds.get(position).setQty(cartMds.get(position).getQty() + 1);
                            notifyItemChanged(position);
//                            UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
//                                    cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
                            UpdateCart(cartMd, userId, "plus");
                        } else {
                            Toast.makeText(mContext, "You have no enough point to add this item", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        cartMds.get(position).setQty(cartMds.get(position).getQty() + 1);
                        notifyItemChanged(position);
//                        UpdateCart(cartMds.get(position).getCart_id(), cartMds.get(position).getCart_item_id(), cartMds.get(position).getMax_group_ship_cost(),
//                                cartMds.get(position).getPid(), cartMds.get(position).getQty(), cartMds.get(position).getShippingtype(), userId);
                        UpdateCart(cartMd, userId, "plus");
                    }
                }
            });

            if (cartMd.getItemName().contains("$")) {
                txtRegularPrice.setVisibility(View.VISIBLE);
                String price = "";
                String[] parts = cartMd.getItemName().split(" ");
                for (String part : parts) {
                    if (part.contains("$")) {
                        price = part;
                        txtRegularPrice.setText("RRP : " + price);
                        break;
                    }
                }
            } else {
                txtRegularPrice.setVisibility(View.GONE);
            }

            if (cartMd.getPayType() != null && !cartMd.getPayType().equals("null")) {
                if (cartMd.getPayType().equals("Points") || cartMd.getPayType().equals("PayPoints")) {
//                    txtMemberPrice.setText("Points : " + Math.round(Double.parseDouble(cartMd.getPrice())
//                            * Double.parseDouble(sessionManager.getPointsConversion())));
                    txtMemberPrice.setText("Points : " + Math.round(Double.parseDouble(cartMd.getProductPoints()) * Double.parseDouble(sessionManager.getPointsConversion())));
                    txtRegularPrice.setVisibility(View.GONE);
                } else {
                    txtMemberPrice.setText("$" + (String.format("%.2f", Double.parseDouble(cartMd.getPrice()) / Double.parseDouble(sessionManager.getPointsConversion()))));
                }
            } else {
//                txtMemberPrice.setText("$" + cartMd.getPrice());
                txtMemberPrice.setText("$" + (String.format("%.2f", Double.parseDouble(cartMd.getPrice()) / Double.parseDouble(sessionManager.getPointsConversion()))));
            }

            int position = getAdapterPosition();
            extra = extra + "\n" + (position + 1) + ". "
                    + "ProductName:" + cartMd.getItemName()
                    + "\nQuantity:" + cartMd.getQty()
                    + "\nId:" + cartMd.getPid()
//                    + "\nPrice:" + cartMd.getPrice();
                    + "\nPrice:" + String.format("%.2f", Double.parseDouble(cartMd.getPrice()) / Double.parseDouble(sessionManager.getPointsConversion()));
        }
    }
}