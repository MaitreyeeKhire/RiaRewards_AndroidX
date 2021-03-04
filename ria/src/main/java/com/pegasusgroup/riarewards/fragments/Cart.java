package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.CartAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
import com.pegasusgroup.riarewards.interfaces.ShippingCostListener;
import com.pegasusgroup.riarewards.interfaces.TotalListener;
import com.pegasusgroup.riarewards.model.CartMd;
import com.pegasusgroup.riarewards.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.GET_USER_INFO;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends BaseFragment implements TotalListener, ShippingCostListener {

    private Cart cart;
    private LinearLayout llRegularPost;
    private AppCompatTextView txtRegularPost;
    private AppCompatTextView txtRegularPrice;

    private LinearLayout llRegisteredPost;
    private AppCompatTextView txtRegisteredPost;
    private AppCompatTextView txtRegisteredPostPrice;

    private AppCompatTextView txtName;
    private AppCompatTextView txtAddress;
    private AppCompatTextView txtChange;

    private AppCompatRadioButton master_card_option;
    private AppCompatRadioButton paypal_card_option;

    private RecyclerView recycler_view;
    private CartAdapter cartAdapter;
    private ArrayList<CartMd> cartMds;
    private float groupShippingCost = 0.0f;
    private float totalRequiredPoints = 0.0f;
    private String productPayType;
    private float productEqualPoints;
    private float requiredPoints = 0.0f;
    private boolean isSmartGroupShipping = false;
    private int cart_qty_count = 0;
    private String smartShippingCost = "0.00";
    private double totalCost = 0;
    private double totalShippingCost = 0;
    private double fixShippingCost = 0;

    // Total
    private AppCompatTextView txtSubTotal;
    private AppCompatTextView txtShippingCosts;
    private AppCompatTextView txtTotalPrice;
    private AppCompatButton cmdPlaceOrder;

    //    private float totalAmount;
//    private float remainingAmount;
//    private float totalUsedPoints;
    private String payType;
    private ArrayList<String> payTypes;

    //    private LinearLayout ll_shipping_cost_container;
    private boolean isSmartGroupShippingPoints = false;
    //    private float equalDollarPerPoint;
    private float totalAmout;
    //    private float pointUsed;
    private float totalPayByCashAmont;
    private float totalLeftUserPoints;
    private float requiredPointsTotal;
    private float totalPoints;
    private float amountTotal;
    private boolean isPay = false;
    private boolean isPayPoints = false;
    private boolean isPoints = false;
    private int userPoints;

    private RelativeLayout pointsLayout;
    private RelativeLayout usedPointLayout;
    private RelativeLayout priceLayout;
    private AppCompatTextView txtTotalRequiredPoints;
    private AppCompatTextView txtUsedPoints;
//    private AppCompatTextView txtTitlePrice;

    private int caseNumber = 0;

    private LinearLayout llShippingCost;

    private RelativeLayout merchantLayout;
    private double merchantFee = 0.0f;
    private AppCompatTextView txtMerchantFee;

//    private NestedScrollView nestedScrollview;
//    private int scrollPosition = 0;

    private RequestQueue requestQueue;

    public Cart() {
        // Required empty public constructor
    }

    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    protected void initComponents(View view) {
        cart = this;
        requestQueue = Volley.newRequestQueue(mContext);
//        nestedScrollview = findViewById(R.id.nestedScrollview);
        txtName = findViewById(R.id.txtName);
        master_card_option = findViewById(R.id.master_card_option);
        paypal_card_option = findViewById(R.id.paypal_card_option);
        TotalListener totalListener = this;
        ShippingCostListener shippingCostListener = this;

        llRegularPost = findViewById(R.id.llRegularPost);
        txtRegularPost = findViewById(R.id.txtRegularPost);
        txtRegularPrice = findViewById(R.id.txtRegularPrice);

        llRegisteredPost = findViewById(R.id.llRegisteredPost);
        txtRegisteredPost = findViewById(R.id.txtRegisteredPost);
        txtRegisteredPostPrice = findViewById(R.id.txtRegisteredPostPrice);

        txtAddress = findViewById(R.id.txtAddress);
        txtChange = findViewById(R.id.txtChange);

        cartMds = new ArrayList<>();
        recycler_view = findViewById(R.id.recycler_view);
        cartAdapter = new CartAdapter(baseAppCompatActivity, mContext, cartMds, groupShippingCost,
                totalListener, shippingCostListener);

        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtShippingCosts = findViewById(R.id.txtShippingCosts);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        cmdPlaceOrder = findViewById(R.id.cmdPlaceOrder);
        pointsLayout = findViewById(R.id.pointLayout);
        usedPointLayout = findViewById(R.id.usedPointLayout);
        priceLayout = findViewById(R.id.priceLayout);
        txtUsedPoints = findViewById(R.id.txtUsedPoints);
        txtTotalRequiredPoints = findViewById(R.id.txtTotalRequiredPoints);

        llShippingCost = findViewById(R.id.llShippingCost);
//        txtTitlePrice = findViewById(R.id.txtTitlePrice);

//        try {
//            scrollPosition = getArguments().getInt("scroll", 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        merchantLayout = findViewById(R.id.merchantLayout);
        txtMerchantFee = findViewById(R.id.txtMerchantFee);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setListeners() {
        recycler_view.setAdapter(cartAdapter);
        txtName.setText(sessionManager.getFirstName() + " " + sessionManager.getLastName());

        llRegularPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llRegularPost.setBackgroundResource(R.drawable.registered_post_border);
                txtRegularPost.setTextColor(getResources().getColor(R.color.white));
                txtRegularPrice.setTextColor(getResources().getColor(R.color.white));

                llRegisteredPost.setBackgroundResource(R.drawable.verification_border);
                txtRegisteredPost.setTextColor(getResources().getColor(R.color.dark_gray));
                txtRegisteredPostPrice.setTextColor(getResources().getColor(R.color.dark_gray));
                fixShippingCost = 0;
//                calculateTotal(totalCost, totalShippingCost);
                updateShippingCost(fixShippingCost);
            }
        });

        llRegisteredPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llRegisteredPost.setBackgroundResource(R.drawable.registered_post_border);
                txtRegisteredPost.setTextColor(getResources().getColor(R.color.white));
                txtRegisteredPostPrice.setTextColor(getResources().getColor(R.color.white));

                llRegularPost.setBackgroundResource(R.drawable.verification_border);
                txtRegularPost.setTextColor(getResources().getColor(R.color.dark_gray));
                txtRegularPrice.setTextColor(getResources().getColor(R.color.dark_gray));
                fixShippingCost = 5.50;
//                calculateTotal(totalCost, totalShippingCost);
                updateShippingCost(fixShippingCost);
            }
        });

        //llRegularPost.performClick();

        if (sessionManager.getShippingAddress().isEmpty()) {
            txtChange.setText("Add");
        }

        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startNextActivity(com.pegasusgroup.riarewards.activity.Cart.this, AddressList.class);
                fragmentChanger.change(new AddressList());
            }
        });

        master_card_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paypal_card_option.setChecked(false);
            }
        });

        paypal_card_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                master_card_option.setChecked(false);
            }
        });

        cmdPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartMds.size() > 0) {
                    if (!getText(txtName).isEmpty() && !getText(txtAddress).isEmpty()) {
//                        if (master_card_option.isChecked() || paypal_card_option.isChecked()) {
                        if (caseNumber == 2) {
                            int reqPoints = Math.round(Float.parseFloat(getText(txtTotalRequiredPoints).replace("P ", "")));
                            if (reqPoints <= userPoints) {
                                placeOrder();
                            } else {
                                showLongToast("You do not have enough points to place this order");
                            }
                        } else {
                            placeOrder();
                        }
//                        } else {
//                            showToast("Please select a payment method");
//                        }
                    } else {
                        showToast("Please add a shipping address");
                    }
                } else {
                    showToast("Please add some product in cart");
                }
            }
        });

        if (sessionManager.getMerchantFee() != null) {
            merchantFee = Double.parseDouble(sessionManager.getMerchantFee());
            if (merchantFee > 0.0) {
                merchantLayout.setVisibility(View.VISIBLE);
                txtMerchantFee.setText("$" + sessionManager.getMerchantFee());
            } else {
                merchantLayout.setVisibility(View.GONE);
            }
        }

        getTotalUserPoints();
        callGetCart();
    }

    @SuppressLint("DefaultLocale")
    private void placeOrder() {
        CommonMethods.printLog("payTypes : " + payTypes);
        Bundle bundle = new Bundle();
        bundle.putString("extra", cartAdapter.getExtra());
        bundle.putString("total", String.format("%.2f", (totalCost / Double.parseDouble(sessionManager.getPointsConversion()) + fixShippingCost)));
        bundle.putString("totalAmount", String.valueOf(totalAmout));
        //bundle.putString("remainingAmount", String.format("%.2f", totalPayByCashAmont / Double.parseDouble(sessionManager.getPointsConversion()) + fixShippingCost));
        bundle.putString("remainingAmount", getText(txtTotalPrice).replace("$ ", ""));
        bundle.putString("totalUsedPoints", getText(txtUsedPoints).replace("P ", "").replace("$", ""));
        bundle.putString("shippingCost", String.format("%.2f", fixShippingCost));

        if (payTypes.size() > 1) {
            payType = "PayPoints";
        } else {
            payType = payTypes.get(0);
        }

        bundle.putString("payType", payType);
//                            startNextActivity(com.pegasusgroup.riarewards.activity.Cart.this, Payment.class, bundle);
        //Payment payment = new Payment();
        BrainTreePay payment = new BrainTreePay();
        payment.setArguments(bundle);
        fragmentChanger.change(payment);

        for (String key : bundle.keySet()) {
            CommonMethods.printLog(key + " => " + bundle.get(key));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.getShippingAddress().isEmpty()) {
            txtChange.setText("Add");
        } else {
            txtChange.setText("Change");
        }
        txtAddress.setText(sessionManager.getShippingAddress());
    }

    private void updateShippingCost(double shippingCost) {
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", sessionManager.getUserId());
            jsonObject.put("cart_id", sessionManager.getCartId());
            jsonObject.put("shipcost", shippingCost);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonMethods.printLog("Json request to update shipping cost::" + jsonObject.toString());
        CommonMethods.printLog("Json request URL::" + AppConstants.UPDATE_SHIPPING_COST);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, AppConstants.UPDATE_SHIPPING_COST, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            CommonMethods.printLog("update shipping cost " + response.toString());
                            calculateTotal(totalCost, totalShippingCost);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                });

        jsObjRequest.setShouldCache(false);
        jsObjRequest.setTag(this);
        requestQueue.add(jsObjRequest);
    }

    /**
     * @description This method is used to call Api
     */
    @SuppressWarnings("ConstantConditions")
    private void callGetCart() {
        try {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            String countURL = AppConstants.SHOW_CART + "uid=" + sessionManager.getUserId();
            CommonMethods.printLog("Show Cart URL : " + countURL);
            requestQueue = Volley.newRequestQueue(mContext);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, countURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    sessionManager.setPoints(0.0F);
                    CommonMethods.printLog("show cart response : " + response);
                    //{"status":{"200":"success"},"cart_items":"1"}
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.contains("200")) {
                                cartMds.clear();
                                totalCost = 0;
                                totalRequiredPoints = 0.0f;
                                groupShippingCost = 0.0f;
                                totalShippingCost = 0;
                                int j = 0;
                                JSONArray cart_List;
                                try {
                                    cart_List = jsonObject.getJSONArray("cart_List");
                                } catch (JSONException je) {
                                    JSONObject cartObject = jsonObject.getJSONObject("cart_List");
                                    Iterator<String> iterator = cartObject.keys();
                                    cart_List = new JSONArray();
                                    while (iterator.hasNext()) {
                                        cart_List.put(new JSONObject(cartObject.getString(iterator.next())));
                                    }
                                }
                                if (cart_List.length() > 0) {
                                    for (int k = 0; k < cart_List.length(); k++) {
                                        JSONObject base = cart_List.getJSONObject(k);
                                        JSONObject cartProduct = base.getJSONObject("CartProduct");
                                        if (cartProduct.getString("product_shipping_type").trim().equals("3")) {
                                            if (cartProduct.getString("product_shipping_cost") != null &&
                                                    !cartProduct.getString("product_shipping_cost").equals("null")) {
                                                if (groupShippingCost < Double.parseDouble(cartProduct.getString("product_shipping_cost")))
                                                    groupShippingCost = Float.parseFloat(cartProduct.getString("product_shipping_cost"));
                                            }
                                        }
                                    }
                                    for (int i = 0; i < cart_List.length(); i++) {
                                        JSONObject base = cart_List.getJSONObject(i);
                                        JSONObject CartItem = base.getJSONObject("CartItem");
                                        JSONObject cart = base.getJSONObject("Cart");
                                        JSONObject product = base.getJSONObject("Product");
                                        JSONObject cartProduct = base.getJSONObject("CartProduct");

                                        int cart_item_id;
                                        String categoryName;
                                        String itemName;
                                        String price;
//                                        int qty;
                                        int pid;
                                        int cart_id;
                                        int max_group_ship_cost;
                                        int product_shipping_type;
                                        String cart_product_id;
                                        String discount;
                                        String ware_product;
                                        String productPoints;

                                        productPayType = CartItem.getString("pay_type");

                                        String shippingCost;

                                        String productShippingType = cartProduct.getString("product_shipping_type").trim();
                                        if (cartProduct.getString("product_shipping_cost") != null &&
                                                !cartProduct.getString("product_shipping_cost").equals("") &&
                                                !cartProduct.getString("product_shipping_cost").equals("null")) {
//                                            shippingCost = cartProduct.getString("product_shipping_cost");
                                            shippingCost = String.valueOf(Math.round(
                                                    Double.parseDouble(cartProduct.getString("product_shipping_cost")) *
                                                            Double.parseDouble(sessionManager.getPointsConversion())));
                                        } else
                                            shippingCost = "0.00";

//                                        if (productPayType.equals("Pay")) {
//                                            price = cartProduct.getString("product_price");
//                                        } else {

                                        productPoints = cartProduct.getString("product_points");

                                        if (productPayType.equals("Pay")) {
                                            price = String.valueOf(Math.round(Double.parseDouble(cartProduct.getString("product_price"))
                                                    * Double.parseDouble(sessionManager.getPointsConversion())));
                                        } else {
                                            price = String.valueOf(Math.round(Double.parseDouble(cartProduct.getString("product_points"))
                                                    * Double.parseDouble(sessionManager.getPointsConversion())));
                                        }

                                        if (productPayType.equals("Points")) {
                                            float tmp = sessionManager.getPoints();
                                            tmp = tmp + (Float.parseFloat(price) * (CartItem.getInt("product_qty")));
                                            sessionManager.setPoints(tmp);
                                        }

//                                        CommonMethods.printLog("price : " + price);

                                        if (!cartProduct.getString("product_points").equals("null") && !cartProduct.getString("product_points").equals("")) {
                                            productEqualPoints = Float.parseFloat(cartProduct.getString("product_points"));
                                            requiredPoints = productEqualPoints * CartItem.getInt("product_qty");
                                            totalRequiredPoints += requiredPoints;
                                        }

                                        if (cartProduct.getString("product_shipping_type").trim().equals("4")) {
                                            isSmartGroupShipping = true;
                                            if (!CartItem.getString("smart_shipping_cost").equals("null") &&
                                                    CartItem.getString("smart_shipping_cost") != null &&
                                                    !CartItem.getString("smart_shipping_cost").equals("0.00")) {
                                                smartShippingCost = CartItem.getString("smart_shipping_cost");
                                            }
                                        }

                                        pid = product.getInt("id");
                                        //price = cartProduct.getString("product_price");
                                        cart_product_id = cartProduct.getString("product_id");
                                        cart_id = cart.getInt("id");
                                        String displayImage = product.getString("display_image");
                                        String merchant_id = product.getString("merchant_id");
                                        cart_item_id = CartItem.getInt("id");
                                        categoryName = CartItem.getString("name");
                                        discount = product.getString("highlight");
                                        itemName = CartItem.getString("name");
                                        product_shipping_type = cartProduct.getInt("product_shipping_type");
                                        ware_product = cartProduct.getString("product_quantity");
                                        cart_qty_count = CartItem.getInt("product_qty");
                                        //txt_total.setText("$" +(Float.valueOf(price)*cart_qty_count)+totalShippingCost);
                                        //String temp = String.valueOf((Float.valueOf(price) * cart_qty_count) + totalShippingCost);
                                        String temp = "";

                                        switch (productShippingType) {
                                            case "1":
                                                j++;
                                                temp = String.valueOf((Float.parseFloat(price) * cart_qty_count) + Float.parseFloat(shippingCost));
                                                //Log.e(TAG, "Shipping type: "+ "1: Fixed");
                                                break;
                                            case "2":
                                                temp = String.valueOf(cart_qty_count * (Float.parseFloat(price) + Float.parseFloat(shippingCost)));
                                                //Log.e(TAG, "Shipping type: "+ "2: Variable");p
                                                break;
                                            case "3":
                                                temp = String.valueOf((Float.parseFloat(price) * cart_qty_count));
                                                //Log.e(TAG, "Shipping type: "+ "3: Group");
                                                break;
                                            case "4":
                                                temp = String.valueOf((Float.parseFloat(price) * cart_qty_count));
                                                //Log.e(TAG, "Shipping type: "+ "4: Smart Group");
                                                break;
                                        }

                                        max_group_ship_cost = 0;

//                                        if (!TextUtils.isEmpty(price))
                                        totalCost = totalCost + Double.parseDouble(temp);
                                        totalShippingCost += Double.parseDouble(shippingCost);

                                        //totalCost = totalCost + groupShippingCost + totalShippingCost;

                                        //Log.e(TAG, "Total cost after new handling charges:"+totalCost);

                                        cartMds.add(new CartMd(cart_item_id, categoryName, itemName, price, cart_id, product_shipping_type,
                                                max_group_ship_cost, pid, cart_qty_count, discount, cart_product_id, ware_product,
                                                productPayType, productEqualPoints, requiredPoints, Float.parseFloat(shippingCost),
                                                displayImage, merchant_id, productPoints));
                                    }

                                    CommonMethods.printLog("getTotal : " + getTotal());
//                                    CommonMethods.printLog("Total Required Points : " + totalRequiredPoints);

                                    Collections.sort(cartMds, new Comparator<CartMd>() {
                                        @Override
                                        public int compare(CartMd cartMd, CartMd t1) {
                                            if (cartMd.getShippingtype() > t1.getShippingtype()) {
                                                return 1;
                                            } else if (cartMd.getShippingtype() < t1.getShippingtype()) {
                                                return -1;
                                            }
                                            return 0;
                                        }
                                    });

                                    Collections.sort(cartMds, new Comparator<CartMd>() {
                                        @SuppressWarnings("ComparatorMethodParameterNotUsed")
                                        @Override
                                        public int compare(CartMd cartMd, CartMd t1) {
                                            if (cartMd.getCategoryName().compareTo(t1.getCategoryName()) > 1) {
                                                return 1;
                                            } else
                                                return -1;
                                        }
                                    });
//                                    sessionManager.setCartCount(String.valueOf(cart_qty_count));

                                    cartAdapter.notifyDataSetChanged();

                                    if (isSmartGroupShipping) {
                                        isSmartGroupShipping = false;
                                        llShippingCost.setVisibility(View.VISIBLE);
                                        if (isAdded())
                                            llRegularPost.performClick();
                                    } else {
                                        llShippingCost.setVisibility(View.GONE);
                                        calculateTotal(totalCost, totalShippingCost);
                                    }
                                } else {
                                    showToastAlert();
                                    calculateTotal(0.0, 0.0);
                                }
                            } else {
                                showToastAlert();
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
                    error.printStackTrace();
                    VolleyLog.d("", "Error: " + error.getMessage());
                    progressDialog.dismiss();
                }

            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 20,
                    2,
                    2));
            stringRequest.setTag(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private float getTotal() {
        totalPoints = 0;
//        List<Integer> payByCashItemList = new ArrayList<>();
        List<Integer> payByPointsItemList = new ArrayList<>();
        List<Integer> payByPointsCashItemList = new ArrayList<>();
        payTypes = new ArrayList<>();
//        payByCashItemList.clear();
        payByPointsItemList.clear();
        payByPointsCashItemList.clear();
        payTypes.clear();

//        double totalShippingCost = 0.0;

        for (int i = 0; i < cartMds.size(); i++) {
            CartMd cartMd = cartMds.get(i);
            if (!cartMd.getPayType().contains("Points") && !cartMd.getPayType().contains("PayPoints")) {
                // true directly go to cash payment ie, normal Pay option
                isPay = true;
//                payByCashItemList.add(i);
                payType = "Pay";
            } else {
                // Here are different scenarios for to check all possibilities.
                if (cartMd.getPayType().equalsIgnoreCase("Points")) {
                    isPoints = true;
                    payByPointsItemList.add(i);
                    payType = "Points";
                }
                if (cartMd.getPayType().equalsIgnoreCase("PayPoints")) {
                    isPayPoints = true;
                    payByPointsCashItemList.add(i);
                    payType = "PayPoints";
                }
            }
            if (!payTypes.contains(payType)) {
                payTypes.add(payType);
            }
        }

        if (isPay && !isPayPoints && !isPoints) {
            caseNumber = 1;
            // Directly go to cash payment without considering points.
//            totalAmout = Float.parseFloat(String.valueOf(totalCost));
            totalAmout = Float.parseFloat(String.valueOf(totalCost / Float.parseFloat(sessionManager.getPointsConversion())));
            CommonMethods.printLog("Case #1 : isPay && !isPayPoints && !isPoints");
        } else if (!isPay && isPoints && !isPayPoints) {
            caseNumber = 2;
            CommonMethods.printLog("Case #2 : !isPay && isPoints && !isPayPoints");
            // Points only payment option
            //checking all scenarios like quantity of item, total required points and total points of the user
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;

            for (int i = 0; i < cartMds.size(); i++) {
                CartMd cartMd = cartMds.get(i);
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion
                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    totalRequiredPoints += requiredPoints;
                    price = quantity * price + productShippingCost;
                    totalAmout += price;
                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    totalRequiredPoints += requiredPoints;
                    price = quantity * (price + productShippingCost);
                    totalAmout += price;
                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + productShippingCost;
                        totalAmout += price;
                    } else if (productShippingCost < groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + 0.0f;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + 0.0f;
                        totalAmout += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price;
                    totalAmout += price;

                    isSmartGroupShippingPoints = true;
                }
            }

            if (isSmartGroupShipping) {
                if (isSmartGroupShippingPoints) {
                    totalRequiredPoints += totalShippingCost;
                    totalAmout += totalShippingCost;

                    // tv_regular_post_value.setText("P0.00");
                    // tv_speed_post_value.setText("P5.50");

                    //Log.e(TAG, "Smart Shipping cost::::"+totalShippingCost);
                } else {
                    totalAmout += totalShippingCost;
                    totalRequiredPoints += totalShippingCost;
                    // tv_regular_post_value.setText("$0.00");
                    // tv_speed_post_value.setText("$5.50");
                }
            }

//            equalDollarPerPoint = totalAmout / totalRequiredPoints;
            totalAmout = totalRequiredPoints;
            totalPoints = totalRequiredPoints;
        } else if (!isPay && !isPoints && isPayPoints) {
            caseNumber = 3;
            CommonMethods.printLog("Case #3 : !isPay && !isPay && !isPoints && isPayPoints");
            // Pay plus points is the only option.
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;

            for (int i = 0; i < cartMds.size(); i++) {
                CartMd cartMd = cartMds.get(i);
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion

                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price + productShippingCost;
                    totalAmout += price;

                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    totalRequiredPoints += requiredPoints;

                    price = quantity * (price + productShippingCost);
                    totalAmout += price;

                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + productShippingCost;
                        totalAmout += price;
                    } else if (productShippingCost < groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + 0.0f;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + 0.0f;
                        totalAmout += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price;
                    totalAmout += price;

                    isSmartGroupShippingPoints = true;
                }
            }

            if (isSmartGroupShipping) {
                if (isSmartGroupShippingPoints) {
                    totalRequiredPoints += totalShippingCost;
                    totalAmout += totalShippingCost;

                    // tv_regular_post_value.setText("P0.00");
                    // tv_speed_post_value.setText("P5.50");

                    //  Log.e(TAG, "Smart Shipping cost::::" + totalShippingCost);
                } else {
                    totalAmout += totalShippingCost;
                    totalRequiredPoints += totalShippingCost;
                    // tv_regular_post_value.setText("$0.00");
                    // tv_speed_post_value.setText("$5.50");
                }
            }

//        equalDollarPerPoint = totalAmout / totalRequiredPoints;
//            float equalPointPerDollar = totalRequiredPoints / totalAmout;
//
            totalAmout = totalRequiredPoints;
            if (totalRequiredPoints > userPoints) {
                totalPoints = totalRequiredPoints;
                totalRequiredPoints = userPoints;
                totalPayByCashAmont = totalAmout - userPoints;
            }
//            totalPoints = totalRequiredPoints;
        } else if (isPay && isPoints && !isPayPoints) {
            caseNumber = 4;
            CommonMethods.printLog("Case #4 : isPay && isPoints && !isPayPoints");
            // Pay plus point option but need use different areas of logic here.
            // Pay plus point option but need use different areas of logic here.
//            Log.e("MyShopActivity", "Total Required points:" + totalRequiredPoints);
//            Log.e("MyShopActivity", "Pay by dollar :" + totalPayByCashAmont);
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;
            for (int i = 0; i < payByPointsItemList.size(); i++) {
                CartMd cartMd = cartMds.get(payByPointsItemList.get(i));
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion
                float productShippingCost = cartMd.getProductShippingCost();

                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price + productShippingCost;
                    totalAmout += price;

                    //  Log.e(TAG, "Total: 1:" + totalRequiredPoints);

                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    totalRequiredPoints += requiredPoints;

                    price = quantity * (price + productShippingCost);
                    totalAmout += price;

                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + productShippingCost;
                        totalAmout += price;
                    } else if (productShippingCost < groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + 0.0f;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + 0.0f;
                        totalAmout += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price;
                    totalAmout += price;

                    isSmartGroupShippingPoints = true;
                }
            }

            if (isSmartGroupShipping) {
                if (isSmartGroupShippingPoints) {
                    totalRequiredPoints += totalShippingCost;
                    totalAmout += totalShippingCost;
                    // tv_regular_post_value.setText("P0.00");
                    // tv_speed_post_value.setText("P5.50");

                    //  Log.e(TAG, "Smart Shipping cost::::" + totalShippingCost);
                } else {
                    totalAmout += totalShippingCost;
                    totalRequiredPoints += totalShippingCost;
                    // tv_regular_post_value.setText("$0.00");
                    // tv_speed_post_value.setText("$5.50");
                }
            }

            totalAmout = totalAmout / Float.parseFloat(sessionManager.getPointsConversion());
            totalCost = totalCost / Float.parseFloat(sessionManager.getPointsConversion());
//            equalDollarPerPoint = totalAmout / totalRequiredPoints;
            totalPayByCashAmont = Float.parseFloat(String.valueOf(totalCost)) - totalAmout;
            totalAmout = totalPayByCashAmont + totalRequiredPoints;
            totalPoints = totalRequiredPoints;
        } else if (isPay && !isPoints && isPayPoints) {
            caseNumber = 5;
            CommonMethods.printLog("Case #5 : isPay && !isPoints && isPayPoints");
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;

            for (int i = 0; i < payByPointsCashItemList.size(); i++) {
                CartMd cartMd = cartMds.get(payByPointsCashItemList.get(i));
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion

                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price + productShippingCost;
                    totalAmout += price;

                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    totalRequiredPoints += requiredPoints;

                    price = quantity * (price + productShippingCost);
                    totalAmout += price;

                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + productShippingCost;
                        totalAmout += price;
                    } else if (productShippingCost < groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + 0.0f;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + 0.0f;
                        totalAmout += price;
                    }

                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price;
                    totalAmout += price;

                    isSmartGroupShippingPoints = true;
                }
            }

            if (isSmartGroupShipping) {
                if (isSmartGroupShippingPoints) {
                    totalRequiredPoints += totalShippingCost;
                    totalAmout += totalShippingCost;

                    // tv_regular_post_value.setText("P0.00");
                    // tv_speed_post_value.setText("P5.50");

                    //  Log.e(TAG, "Smart Shipping cost::::" + totalShippingCost);
                } else {
                    totalAmout += totalShippingCost;
                    totalRequiredPoints += totalShippingCost;
                    // tv_regular_post_value.setText("$0.00");
                    // tv_speed_post_value.setText("$5.50");
                }
            }

            totalAmout = totalAmout / Float.parseFloat(sessionManager.getPointsConversion());
            totalCost = totalCost / Float.parseFloat(sessionManager.getPointsConversion());

            //  equalDollarPerPoint = totalAmout / totalRequiredPoints;
            // total - pointTotal
            totalPayByCashAmont = Float.parseFloat(String.valueOf(totalCost)) - totalAmout;

//            totalAmout = totalPayByCashAmont + totalRequiredPoints;

            if (totalRequiredPoints >= userPoints) {
                totalPayByCashAmont = totalPayByCashAmont * Float.parseFloat(sessionManager.getPointsConversion());
                //remaining balance
                float totalRemainingAmount = (totalRequiredPoints - userPoints);
                totalPayByCashAmont += totalRemainingAmount;
//                totalRequiredPoints = userPoints;
                totalAmout = totalPayByCashAmont;
            }
            totalPoints = totalRequiredPoints;
        } else if (!isPay && isPoints && isPayPoints) {
            caseNumber = 6;
            CommonMethods.printLog("Case #6 : !isPay && isPoints && isPayPoints");
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;
            requiredPointsTotal = 0.0f;
            amountTotal = 0.0f;

            for (int i = 0; i < payByPointsItemList.size(); i++) {
                CartMd cartMd = cartMds.get(payByPointsItemList.get(i));
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion

                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price + productShippingCost;
                    totalAmout += price;
                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    totalRequiredPoints += requiredPoints;

                    price = quantity * (price + productShippingCost);
                    totalAmout += price;
                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + productShippingCost;
                        totalAmout += price;
                    } else if (productShippingCost < groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + 0.0f;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + 0.0f;
                        totalAmout += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price;
                    totalAmout += price;

                    isSmartGroupShippingPoints = true;
                }
            }

            for (int j = 0; j < payByPointsCashItemList.size(); j++) {
                CartMd cartMd = cartMds.get(payByPointsCashItemList.get(j));
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion

                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    requiredPointsTotal += requiredPoints;

                    price = quantity * price + productShippingCost;
                    amountTotal += price;

                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    requiredPointsTotal += requiredPoints;

                    price = quantity * (price + productShippingCost);
                    amountTotal += price;

                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        requiredPointsTotal += requiredPoints;

                        price = quantity * price + productShippingCost;
                        amountTotal += price;
                    } else if (productShippingCost < groupShippingCost) {

                        requiredPoints = quantity * requiredPoints + 0.0f;
                        requiredPointsTotal += requiredPoints;

                        price = quantity * price + 0.0f;
                        amountTotal += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    requiredPointsTotal += requiredPoints;

                    price = quantity * price;
                    amountTotal += price;
                    isSmartGroupShippingPoints = true;
                }
            }

            if (isSmartGroupShipping) {
                if (isSmartGroupShippingPoints) {
                    totalRequiredPoints += totalShippingCost;
                    totalAmout += totalShippingCost;
                    // tv_regular_post_value.setText("P0.00");
                    // tv_speed_post_value.setText("P5.50");

                    //  Log.e(TAG, "Smart Shipping cost::::" + totalShippingCost);
                } else {
                    totalAmout += totalShippingCost;
                    totalRequiredPoints += totalShippingCost;
                    // tv_regular_post_value.setText("$0.00");
                    // tv_speed_post_value.setText("$5.50");
                }
            }

//            equalDollarPerPoint = amountTotal / requiredPointsTotal;
            totalAmout = requiredPointsTotal + totalRequiredPoints;

            if (totalAmout > userPoints) {
                if (!(totalRequiredPoints > userPoints)) {
                    totalPayByCashAmont = Float.parseFloat(String.valueOf(totalAmout)) - userPoints;
                    totalRequiredPoints = userPoints;
                }
            }
//            totalPoints = requiredPointsTotal + totalRequiredPoints;
        } else if (isPay && isPoints && isPayPoints) {
            caseNumber = 7;
            CommonMethods.printLog("Case #7 : isPay && isPoints && isPayPoints");
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;
            requiredPointsTotal = 0.0f;
            amountTotal = 0.0f;

            for (int i = 0; i < payByPointsItemList.size(); i++) {
                CartMd cartMd = cartMds.get(payByPointsItemList.get(i));
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion

                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price + productShippingCost;
                    totalAmout += price;
                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    totalRequiredPoints += requiredPoints;

                    price = quantity * (price + productShippingCost);
                    totalAmout += price;
                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + productShippingCost;
                        totalAmout += price;
                    } else if (productShippingCost < groupShippingCost) {

                        requiredPoints = quantity * requiredPoints + 0.0f;
                        totalRequiredPoints += requiredPoints;

                        price = quantity * price + 0.0f;
                        totalAmout += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    totalRequiredPoints += requiredPoints;

                    price = quantity * price;
                    totalAmout += price;
                    isSmartGroupShippingPoints = true;
                }
            }

            totalLeftUserPoints = userPoints - totalRequiredPoints;

            for (int j = 0; j < payByPointsCashItemList.size(); j++) {
                CartMd cartMd = cartMds.get(payByPointsCashItemList.get(j));
//                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
                float requiredPoints = price; // Added this line for point conversion

                float productShippingCost = cartMd.getProductShippingCost();
                int shippingType = cartMd.getShippingtype();

                if (shippingType == 1) {
                    requiredPoints = quantity * requiredPoints + productShippingCost;
                    requiredPointsTotal += requiredPoints;

                    price = quantity * price + productShippingCost;
                    amountTotal += price;
                } else if (shippingType == 2) {
                    requiredPoints = quantity * (requiredPoints + productShippingCost);
                    requiredPointsTotal += requiredPoints;
                    price = quantity * (price + productShippingCost);
                    amountTotal += price;
                } else if (shippingType == 3) {
                    if (productShippingCost == groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + productShippingCost;
                        requiredPointsTotal += requiredPoints;
                        price = quantity * price + productShippingCost;
                        amountTotal += price;
                    } else if (productShippingCost < groupShippingCost) {
                        requiredPoints = quantity * requiredPoints + 0.0f;
                        requiredPointsTotal += requiredPoints;
                        price = quantity * price + 0.0f;
                        amountTotal += price;
                    }
                } else if (shippingType == 4) {
                    requiredPoints = quantity * requiredPoints;
                    requiredPointsTotal += requiredPoints;
                    price = quantity * price;
                    amountTotal += price;
                    isSmartGroupShippingPoints = true;
                }
            }

            if (isSmartGroupShipping) {
                if (isSmartGroupShippingPoints) {
                    totalRequiredPoints += totalShippingCost;
                    totalAmout += totalShippingCost;

                    // tv_regular_post_value.setText("P0.00");
                    // tv_speed_post_value.setText("P5.50");
                } else {
                    totalAmout += totalShippingCost;
                    totalRequiredPoints += totalShippingCost;
                    // tv_regular_post_value.setText("$0.00");
                    // tv_speed_post_value.setText("$5.50");
                }
            }

//            totalAmout = totalAmout / Float.parseFloat(sessionManager.getPointsConversion());
//            totalCost = totalCost / Float.parseFloat(sessionManager.getPointsConversion());

            //24/04/19
            totalPayByCashAmont = Float.parseFloat(String.valueOf(totalCost)) - (totalAmout + amountTotal);
//            totalPayByCashAmont = totalPayByCashAmont / Float.parseFloat(sessionManager.getPointsConversion());
            totalAmout = Float.parseFloat(String.valueOf(totalCost)) - (totalAmout + amountTotal);
            totalAmout = totalAmout + (requiredPointsTotal + totalRequiredPoints);
            totalPoints = requiredPointsTotal + totalRequiredPoints;

            if ((requiredPointsTotal + totalRequiredPoints) > userPoints) {
//                totalPoints = requiredPointsTotal + totalRequiredPoints;
                if (!(totalRequiredPoints > userPoints)) {
                    float totalRemainingAmount = (requiredPointsTotal + totalRequiredPoints) - userPoints;
//                    totalRemainingAmount = totalRemainingAmount / Float.parseFloat(sessionManager.getPointsConversion());
                    totalPayByCashAmont += totalRemainingAmount;
                    totalRequiredPoints = userPoints;
                }
            } else {
                totalPayByCashAmont = totalPayByCashAmont / Float.parseFloat(sessionManager.getPointsConversion());
            }
        }

        CommonMethods.printLog("totalPoints : " + totalPoints);
        CommonMethods.printLog("requiredPointsTotal : " + requiredPointsTotal);
        CommonMethods.printLog("totalPayByCashAmount : " + totalPayByCashAmont);
        CommonMethods.printLog("totalAmount : " + totalAmout);
        CommonMethods.printLog("totalRequiredPoints  : " + totalRequiredPoints);
        CommonMethods.printLog("amountTotal : " + amountTotal);
        CommonMethods.printLog("userPoints : " + userPoints);
        CommonMethods.printLog("Session Points : " + sessionManager.getPoints());
//        calculateTotal(0.0, 0.0);

        return totalAmout;
    }

    /*
     * Used for getting total point earned by the user
     * This point is used for Payment
     */
    private void getTotalUserPoints() {
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        CommonMethods.printLog(GET_USER_INFO);
        StringRequest postReq = new StringRequest(Request.Method.POST, GET_USER_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethods.printLog("Response::" + response);
                        try {
                            if (response.contains("cart_id")) {
                                JSONArray array = new JSONArray(response);
                                JSONObject user = array.getJSONObject(0);
                                CommonMethods.printLog("User data" + user);
                                sessionManager.setUserPoint(user.getString("points"));
                                userPoints = Integer.parseInt(user.getString("points"));
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
//                progressDialog.dismiss();
                error.printStackTrace();
            }
        }) {

            @SuppressWarnings("SpellCheckingInspection")
            @Override
            protected java.util.Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uname", sessionManager.getUserId());
                params.put("client_id", sessionManager.getClientId());
                params.put("response_type", "json");
                CommonMethods.printLog("point params : " + params);
                return params;
            }
        };
        postReq.setTag(this);
        requestQueue.add(postReq);
    }

    private void showToastAlert() {
        requestQueue.cancelAll(this);
        showLongToast("cart is empty");
//        txtTotalRequiredPoints.setText("");
//        txtUsedPoints.setText("");
//        txtSubTotal.setText("");
//        txtTotalPrice.setText("");
        sessionManager.setCartCount("0");
        sessionManager.setPoints(0.0F);
//                baseAppCompatActivity.getSupportFragmentManager().popBackStack();
        fragmentReplacer.replace(new Home());
    }

    @Override
    public void total(Double total) {
        //calculateTotal(total, totalShippingCost);
//        callGetCart();

//        Bundle bundle = new Bundle();
//        bundle.putInt("scroll", nestedScrollview.getScrollY());
        baseAppCompatActivity.progressDialog.setMessage("Loading...");
//        cart.setArguments(bundle);
        try {
//            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
//            fragmentReplacer.replace(cart);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().detach(cart);
            getActivity().getSupportFragmentManager().beginTransaction().attach(cart);
            callGetCart();
        } catch (Exception e) {
            e.printStackTrace();
//            fragmentReplacer.replace(cart);
        }
    }

    @SuppressWarnings("unused")
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calculateTotal(Double total, Double shippingCost) {
        shippingCost = shippingCost / Double.parseDouble(sessionManager.getPointsConversion());
        if (caseNumber == 1) {  // Cash Only
            priceLayout.setVisibility(View.VISIBLE);
            pointsLayout.setVisibility(View.GONE);
            usedPointLayout.setVisibility(View.GONE);

            txtSubTotal.setText("$ " + String.format("%.2f", totalAmout - shippingCost));
            txtShippingCosts.setText("$ " + String.format("%.2f", shippingCost + fixShippingCost));
            if (merchantFee > 0.0) {
                double rr = totalAmout + fixShippingCost;
                double temp = (rr * merchantFee) / 100;
                double temp1 = rr + temp;
                txtTotalPrice.setText("$ " + String.format("%.2f", temp1));
            } else {
                txtTotalPrice.setText("$ " + String.format("%.2f", totalAmout + fixShippingCost));
            }
        } else { // Mix Cases
            priceLayout.setVisibility(View.GONE);
            pointsLayout.setVisibility(View.VISIBLE);
            usedPointLayout.setVisibility(View.VISIBLE);

            if (totalPoints > 0) {
                txtTotalRequiredPoints.setText("P " + String.format("%.2f", totalPoints));
            } else {
                totalPoints = totalRequiredPoints + requiredPointsTotal;
                if (caseNumber == 5) {
                    txtTotalRequiredPoints.setText("P " + String.format("%.2f", totalPoints));
                } else {
                    if (totalPoints < totalAmout) {
                        txtTotalRequiredPoints.setText("P " + String.format("%.2f", totalPoints));
                    } else {
                        txtTotalRequiredPoints.setText("P " + String.format("%.2f", totalAmout));
                    }
                }
            }

            double remainingPoints = userPoints - totalPoints;
            if (remainingPoints > fixShippingCost) {
                txtShippingCosts.setText("P " + String.format("%.2f", shippingCost + fixShippingCost));
                txtSubTotal.setText("$ " + String.format("%.2f", totalPayByCashAmont));
                if (merchantFee > 0.0) {
                    double rr = totalPayByCashAmont;
                    double temp = (rr * merchantFee) / 100;
                    double temp1 = rr + temp;
                    txtTotalPrice.setText("$ " + String.format("%.2f", temp1));
                } else {
                    txtTotalPrice.setText("$ " + String.format("%.2f", totalPayByCashAmont));
                }
            } else {
                txtShippingCosts.setText("$ " + String.format("%.2f", shippingCost + fixShippingCost));
                txtSubTotal.setText("$ " + String.format("%.2f", totalPayByCashAmont + fixShippingCost));
                if (merchantFee > 0.0) {
                    double rr = totalPayByCashAmont / Double.parseDouble(sessionManager.getPointsConversion()) + fixShippingCost;
                    double temp = (rr * merchantFee) / 100;
                    double temp1 = rr + temp;
                    txtTotalPrice.setText("$ " + String.format("%.2f", temp1));
                } else {
                    txtTotalPrice.setText("$ " + String.format("%.2f", (totalPayByCashAmont / Double.parseDouble(sessionManager.getPointsConversion()) + fixShippingCost)));
                }
            }

            if ((Double.parseDouble(getText(txtTotalRequiredPoints).replace("P ", "")) + +fixShippingCost) > userPoints) {
                if (totalRequiredPoints > userPoints) {
                    txtUsedPoints.setText("P " + userPoints + ".00");
                } else {
                    txtUsedPoints.setText("P " + String.format("%.2f", totalRequiredPoints));
                }
            } else {
                double totalPoints = Double.parseDouble(getText(txtTotalRequiredPoints).replace("P ", "").trim()) + fixShippingCost;
                txtUsedPoints.setText("P " + String.format("%.2f", totalPoints));
            }
//            totalPoints = 0;
        }
//        nestedScrollview.setScrollY(scrollPosition);
        //nestedScrollview.smoothScrollTo(0, scrollPosition);
    }

    @Override
    public void updateShipping() {
        fixShippingCost = 0;
        updateShippingCost(fixShippingCost);
    }


//    @SuppressWarnings("deprecation")
//    private void showAlert() {
//        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//        alertDialog.setTitle("Ria Rewards");
//        alertDialog.setMessage("cart is empty");
//        alertDialog.setCancelable(false);
//        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                txtTotalRequiredPoints.setText("");
//                txtUsedPoints.setText("");
//                txtSubTotal.setText("");
//                txtTotalPrice.setText("");
//                sessionManager.setCartCount("0");
//                sessionManager.setPoints(0.0F);
////                baseAppCompatActivity.getSupportFragmentManager().popBackStack();
//                fragmentReplacer.replace(new Home());
//                dialog.dismiss();
//            }
//        });
//        // Showing Alert Message
//        alertDialog.show();
//        Button negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//        Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//        negative.setTextColor(Color.parseColor("#FFFF0400"));
//        positive.setTextColor(Color.parseColor("#FFFF0400"));
//    }
}