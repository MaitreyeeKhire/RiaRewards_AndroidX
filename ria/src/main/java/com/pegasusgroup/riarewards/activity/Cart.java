package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.adapter.CartAdapter;
import com.pegasusgroup.riarewards.interfaces.AppConstants;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.GET_USER_INFO;

public class Cart extends BaseAppCompatActivity implements TotalListener {

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

    private float totalAmount;
    private float remainingAmount;
    private float totalUsedPoints;
    private String payType;
    //    private LinearLayout ll_shipping_cost_container;
    private boolean isSmartGroupShippingPoints = false;
    private float equalDollarPerPoint;
    private float totalAmout;
    //    private float pointUsed;
    private float totalPayByCashAmont;
    private float totalLeftUserPointss;
    private float requiredPointsTotal;
    private float amountTotal;
    private boolean isPay = false;
    private boolean isPayPoints = false;
    private boolean isPoints = false;
    private List<Integer> payByCashItemList;
    private List<Integer> payByPointsItemList;
    private List<Integer> payByPointsCashItemList;
    private int userPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cart;
    }

    @Override
    protected void initComponents() {
        txtName = findViewById(R.id.txtName);
        master_card_option = findViewById(R.id.master_card_option);
        paypal_card_option = findViewById(R.id.paypal_card_option);

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
        cartAdapter = new CartAdapter(Cart.this, cartMds, groupShippingCost);

        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtShippingCosts = findViewById(R.id.txtShippingCosts);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        cmdPlaceOrder = findViewById(R.id.cmdPlaceOrder);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getShippingAddress().isEmpty()) {
            txtChange.setText("Add");
        } else {
            txtChange.setText("Change");
        }
        txtAddress.setText(sessionManager.getShippingAddress());
        getTotalUserPoints();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
                calculateTotal(totalCost, totalShippingCost);
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
                calculateTotal(totalCost, totalShippingCost);
            }
        });

        llRegularPost.performClick();

        if (sessionManager.getShippingAddress().isEmpty()) {
            txtChange.setText("Add");
        }

        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(Cart.this, AddressList.class);
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
        callGetCart();

        cmdPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartMds.size() > 0) {
                    if (!getText(txtName).isEmpty() && !getText(txtAddress).isEmpty()) {
                        if (master_card_option.isChecked() || paypal_card_option.isChecked()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("extra", cartAdapter.getExtra());
                            bundle.putString("total", String.valueOf(totalCost + totalShippingCost));
                            bundle.putString("totalAmount", String.valueOf(totalAmount));
                            bundle.putString("remainingAmount", String.valueOf(totalPayByCashAmont));
                            bundle.putString("totalUsedPoints", String.valueOf(requiredPointsTotal));
                            bundle.putString("payType", productPayType);
                            startNextActivity(Cart.this, Payment.class, bundle);
                        } else {
                            showToast("Please select a payment method");
                        }
                    } else {
                        showToast("Please add a shipping address");
                    }
                } else {
                    showToast("Please add some product in cart");
                }
            }
        });
    }

    /**
     * @description This method is used to call Api
     */
    private void callGetCart() {
        try {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            String countURL = AppConstants.SHOW_CART + "uid=" + sessionManager.getUserId();
            CommonMethods.printLog("Show Cart URL : " + countURL);
            RequestQueue requestQueue = Volley.newRequestQueue(Cart.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, countURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
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
                                int j = 0;
                                JSONArray cart_List = jsonObject.getJSONArray("cart_List");
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
                                        int qty;
                                        int pid;
                                        int cart_id;
                                        int max_group_ship_cost;
                                        int product_shipping_type;
                                        String cart_product_id;
                                        String discount;
                                        String ware_product;

                                        productPayType = CartItem.getString("pay_type");
                                        String shippingCost;

                                        String productShippingType = cartProduct.getString("product_shipping_type").trim();
                                        if (cartProduct.getString("product_shipping_cost") != null && !cartProduct.getString("product_shipping_cost").equals("") && !cartProduct.getString("product_shipping_cost").equals("null"))
                                            shippingCost = cartProduct.getString("product_shipping_cost");
                                        else
                                            shippingCost = "0.00";

                                        price = cartProduct.getString("product_price");

                                        if (!cartProduct.getString("product_points").equals("null") && !cartProduct.getString("product_points").equals("")) {
                                            productEqualPoints = Float.parseFloat(cartProduct.getString("product_points"));
                                            requiredPoints = productEqualPoints * CartItem.getInt("product_qty");
                                            totalRequiredPoints += requiredPoints;
                                        }

                                        if (cartProduct.getString("product_shipping_type").trim().equals("4")) {
                                            isSmartGroupShipping = true;
                                            if (!CartItem.getString("smart_shipping_cost").equals("null") && CartItem.getString("smart_shipping_cost") != null && !CartItem.getString("smart_shipping_cost").equals("0.00")) {
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
                                                temp = String.valueOf((Float.valueOf(price) * cart_qty_count) + Float.parseFloat(shippingCost));

                                                //Log.e(TAG, "Shipping type: "+ "1: Fixed");

                                                break;
                                            case "2":
                                                temp = String.valueOf(cart_qty_count * (Float.valueOf(price) + Float.parseFloat(shippingCost)));

                                                //Log.e(TAG, "Shipping type: "+ "2: Variable");

                                                break;
                                            case "3":
                                                temp = String.valueOf((Float.valueOf(price) * cart_qty_count));

                                                //Log.e(TAG, "Shipping type: "+ "3: Group");

                                                break;
                                            case "4":
                                                temp = String.valueOf((Float.valueOf(price) * cart_qty_count));

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
                                                productPayType, productEqualPoints, requiredPoints, Float.parseFloat(shippingCost), displayImage, merchant_id, ""));
                                    }

                                    CommonMethods.printLog("getTotal : " + getTotal());

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
                                        @Override
                                        public int compare(CartMd cartMd, CartMd t1) {
                                            if (cartMd.getCategoryName().compareTo(t1.getCategoryName()) > 1) {
                                                return 1;
                                            } else
                                                return -1;
                                        }
                                    });
                                    sessionManager.setCartCount(String.valueOf(cart_qty_count));

                                    cartAdapter.notifyDataSetChanged();
                                    calculateTotal(totalCost, totalShippingCost);
                                } else {
                                    showAlert("cart is empty");
                                    calculateTotal(0.0, 0.0);
                                }
                            } else {
                                showAlert("cart is empty");
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
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    private float getTotal() {
        payByCashItemList = new ArrayList<>();
        payByPointsItemList = new ArrayList<>();
        payByPointsCashItemList = new ArrayList<>();
        payByCashItemList.clear();
        payByPointsItemList.clear();
        payByPointsCashItemList.clear();
        for (int i = 0; i < cartMds.size(); i++) {
            CartMd cartMd = cartMds.get(i);
            if (!cartMd.getPayType().contains("Points") && !cartMd.getPayType().contains("PayPoints")) {
                // true directly go to cash payment ie, normal Pay option
                isPay = true;
                payByCashItemList.add(i);
                payType = "Points";
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
        }

        if (isPay && !isPayPoints && !isPoints) {
            // Directly go to cash payment without considering points.
            totalAmout = Float.parseFloat(String.valueOf(totalCost));
        } else if (!isPay && isPoints && !isPayPoints) {
            // Points only payment option
            //checking all scenarios like quantity of item, total required points and total points of the user
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;

            for (int i = 0; i < cartMds.size(); i++) {
                CartMd cartMd = cartMds.get(i);
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
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

            equalDollarPerPoint = totalAmout / totalRequiredPoints;
            totalAmout = totalRequiredPoints;

        } else if (!isPay && !isPoints && isPayPoints) {
            // Pay plus points is the only option.

            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;

            for (int i = 0; i < cartMds.size(); i++) {
                CartMd cartMd = cartMds.get(i);
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());

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

            equalDollarPerPoint = totalAmout / totalRequiredPoints;

//            float equalPointPerDollar = totalRequiredPoints / totalAmout;
//
            totalAmout = totalRequiredPoints;
            if (totalRequiredPoints > userPoints) {
                totalRequiredPoints = userPoints;
                totalPayByCashAmont = totalAmout - userPoints;
            }
        } else if (isPay && isPoints && !isPayPoints) {
            // Pay plus point option but need use different areas of logic here.
            Log.e("MyShopActivity", "Total Required points:" + totalRequiredPoints);
            Log.e("MyShopActivity", "Pay by dollar :" + totalPayByCashAmont);
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;
            for (int i = 0; i < payByPointsItemList.size(); i++) {
                CartMd cartMd = cartMds.get(payByPointsItemList.get(i));
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());
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

            equalDollarPerPoint = totalAmout / totalRequiredPoints;
            totalPayByCashAmont = Float.parseFloat(String.valueOf(totalCost)) - totalAmout;
            totalAmout = totalPayByCashAmont + totalRequiredPoints;

        } else if (isPay && !isPoints && isPayPoints) {

            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;

            for (int i = 0; i < payByPointsCashItemList.size(); i++) {
                CartMd cartMd = cartMds.get(payByPointsCashItemList.get(i));
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());

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
            equalDollarPerPoint = totalAmout / totalRequiredPoints;
            // total - pointTotal
            totalPayByCashAmont = Float.parseFloat(String.valueOf(totalCost)) - totalAmout;

            totalAmout = totalPayByCashAmont + totalRequiredPoints;

            if (totalRequiredPoints > userPoints) {
                //remaing balance
                float totalRemaingAmount = totalRequiredPoints - userPoints;
                totalPayByCashAmont += totalRemaingAmount;
                totalRequiredPoints = userPoints;
            }
        } else if (!isPay && isPoints && isPayPoints) {
            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;
            requiredPointsTotal = 0.0f;
            amountTotal = 0.0f;

            for (int i = 0; i < payByPointsItemList.size(); i++) {

                CartMd cartMd = cartMds.get(payByPointsItemList.get(i));
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());

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
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());

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

            equalDollarPerPoint = amountTotal / requiredPointsTotal;

            totalAmout = requiredPointsTotal + totalRequiredPoints;

            if (totalAmout > userPoints) {
                if (!(totalRequiredPoints > userPoints)) {
                    totalPayByCashAmont = Float.parseFloat(String.valueOf(totalAmout)) - userPoints;
                }

            }
        } else if (isPay && isPoints && isPayPoints) {

            totalRequiredPoints = 0.0f;
            totalAmout = 0.0f;
            totalPayByCashAmont = 0.0f;
            requiredPointsTotal = 0.0f;
            amountTotal = 0.0f;

            for (int i = 0; i < payByPointsItemList.size(); i++) {

                CartMd cartMd = cartMds.get(payByPointsItemList.get(i));
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());

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

            totalLeftUserPointss = userPoints - totalRequiredPoints;

            for (int j = 0; j < payByPointsCashItemList.size(); j++) {

                CartMd cartMd = cartMds.get(payByPointsCashItemList.get(j));
                float requiredPoints = cartMd.getPayEqualPoints();
                int quantity = cartMd.getQty();
                float price = Float.parseFloat(cartMd.getPrice());

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

            //24/04/19
            totalPayByCashAmont = Float.parseFloat(String.valueOf(totalCost)) - (totalAmout + amountTotal);
            totalAmout = Float.parseFloat(String.valueOf(totalCost)) - (totalAmout + amountTotal);
            totalAmout = totalAmout + (requiredPointsTotal + totalRequiredPoints);

            if ((requiredPointsTotal + totalRequiredPoints) > userPoints) {
                if (!(totalRequiredPoints > userPoints)) {
                    float totalRemaingAmount = (requiredPointsTotal + totalRequiredPoints) - userPoints;
                    totalPayByCashAmont += totalRemaingAmount;
                    totalRequiredPoints = userPoints;
                }
            }
        }

        return totalAmout;
    }

    /*
     * Used for getting total point earned by the user
     * This point is used for Payment
     */
    private void getTotalUserPoints() {
        RequestQueue requestQueue = Volley.newRequestQueue(Cart.this);

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
                progressDialog.dismiss();
                error.printStackTrace();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uname", sessionManager.getUserId());
                params.put("client_id", sessionManager.getClientId());
                params.put("response_type", "json");
                CommonMethods.printLog("point params : " + params);
                return params;
            }
        };
        requestQueue.add(postReq);
    }

    @SuppressWarnings("deprecation")
    private void showAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(Cart.this).create();
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
    }

    @Override
    public void total(Double total) {
        //calculateTotal(total, totalShippingCost);
        callGetCart();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calculateTotal(Double total, Double shippingCost) {
        txtSubTotal.setText("$" + String.format("%.2f", total - shippingCost));
        txtShippingCosts.setText("$" + String.format("%.2f", shippingCost + fixShippingCost));
        txtTotalPrice.setText("$" + String.format("%.2f", total + fixShippingCost));
    }
}