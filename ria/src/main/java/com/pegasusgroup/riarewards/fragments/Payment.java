package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.model.AddressMd;
import com.pegasusgroup.riarewards.stripe.RetrofitFactory;
import com.pegasusgroup.riarewards.stripe.service.SampleStoreEphemeralKeyProvider;
import com.pegasusgroup.riarewards.stripe.service.StripeService;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.pegasusgroup.riarewards.utils.Support;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.model.Customer;
import com.stripe.android.model.CustomerSource;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceCardData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.pegasusgroup.riarewards.interfaces.AppConstants.API_ENDPOINT;

/**
 * A simple {@link Fragment} subclass.
 */
public class Payment extends BaseFragment {
    private static final String PUBLISHABLE_KEY = "pk_test_O25zyXXj74gl3XRbVoXjaHaE";
    //    private final String API_ENDPOINT = "https://www.atwork.com.au/newapp/update_cart_status.php";
    private String cus_id, balance_transaction;
    private Button btn_place_order;
    private TextInputLayout txt_edt_email;
    private TextInputEditText edt_email;
    private Button btn_select_payment;
    private LinearLayout linear_paymentinfo;
    private TextView payment_source, plz_enter_email;
    private CompositeSubscription mCompositeSubscription;
    private StripeService mStripeService;
    private PaymentSession mPaymentSession;
    //    private double price;
//    private float totalAmount;
    private float remainingAmount;
    private float totalUsedPoints;

    private String extra;
    private String total;
    private String payType;
    private boolean cartStatusUpdated = false;
    private String shippingCost;
    private String custom_user_points = "1";

    // for displaying payment Information
    private AppCompatTextView txtTotalAmount;
    private AppCompatTextView txtTotalUsedPoints;
    private AppCompatTextView txtRemainingAmount;

    public Payment() {
        // Required empty public constructor
    }

    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    protected void initComponents(View view) {
        extra = Objects.requireNonNull(getArguments()).getString("extra");
        total = getArguments().getString("total");
//        extra = extra + total;
        payType = getArguments().getString("payType");

        if (!Objects.requireNonNull(getArguments().getString("totalUsedPoints")).equals(""))
            totalUsedPoints = Float.parseFloat(Objects.requireNonNull(getArguments().getString("totalUsedPoints")).trim());
        else
            totalUsedPoints = 0.0f;

        if (!Objects.requireNonNull(getArguments().getString("remainingAmount")).equals(""))
            remainingAmount = Float.parseFloat(Objects.requireNonNull(getArguments().getString("remainingAmount")).trim());
        else
            remainingAmount = 0.0f;

        shippingCost = (getArguments().getString("shippingCost", "0.00"));

        PaymentConfiguration.init(PUBLISHABLE_KEY);

        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtTotalUsedPoints = findViewById(R.id.txtTotalUsedPoints);
        txtRemainingAmount = findViewById(R.id.txtRemainingAmount);

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);
        mCompositeSubscription = new CompositeSubscription();

        txt_edt_email = findViewById(R.id.txt_edt_email);
        edt_email = findViewById(R.id.edt_email);
        btn_select_payment = findViewById(R.id.btn_select_payment);
        linear_paymentinfo = findViewById(R.id.linear_paymentinfo);
        payment_source = findViewById(R.id.payment_source);
        btn_place_order = findViewById(R.id.btn_place_order);
        plz_enter_email = findViewById(R.id.plz_enter_email);

        String email_hint = "Email <font color=\"red\">*</font>";
        Spanned sp_email = Html.fromHtml(email_hint);
        edt_email.setHint(sp_email);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void setListeners() {
        AddressMd addressMd = new Gson().fromJson(sessionManager.getAddressObject(), AddressMd.class);
        sessionManager.setAddressId(addressMd.getId());

        txtTotalAmount.setText("Total $ : " + total);
        txtTotalUsedPoints.setText("Total Points : " + totalUsedPoints);
        txtRemainingAmount.setText("Total $ : " + remainingAmount);

        //total = String.format("%.2f", (Float.parseFloat(total) - Float.parseFloat(shippingCost)));

        if (payType.equalsIgnoreCase("Pay")) {
            txtTotalAmount.setVisibility(View.VISIBLE);
            txtTotalUsedPoints.setVisibility(View.GONE);
            txtRemainingAmount.setVisibility(View.GONE);
        } else if (payType.equalsIgnoreCase("Points")) {
            txtTotalAmount.setVisibility(View.GONE);
            txtTotalUsedPoints.setVisibility(View.VISIBLE);
            txtRemainingAmount.setVisibility(View.GONE);
        } else {
            txtTotalAmount.setVisibility(View.GONE);
            txtTotalUsedPoints.setVisibility(View.VISIBLE);
            txtRemainingAmount.setVisibility(View.VISIBLE);
        }

        edt_email.setText(sessionManager.getClientEmail());

        if (payType.equalsIgnoreCase("Points")) {
            btn_select_payment.setText("Place order by Points");
            payByPointMethodSetup();
        } else if (payType.equalsIgnoreCase("PayPoints")) {
            if (remainingAmount == 0.00 || remainingAmount < 0.00) {
                btn_select_payment.setText("Place order by Points");
                txtRemainingAmount.setVisibility(View.GONE);
                payByPointMethodSetup();
            } else {
                payByDollarMethodSetup();
            }
        } else {
            mPaymentSession = new PaymentSession(Objects.requireNonNull(getActivity()));
            payByDollarMethodSetup();
        }

        Objects.requireNonNull(getView()).setFocusableInTouchMode(true);
        getView().requestFocus();
        Objects.requireNonNull(getView()).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Sure to cancel Payment ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (cartStatusUpdated) {
                                        updateCartOrderFails("");
                                    } else {
                                        fragmentReplacer.replace(new Cart());
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Button negative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    negative.setTextColor(Color.parseColor("#FFFF0400"));
                    positive.setTextColor(Color.parseColor("#FFFF0400"));

                    return true;
                }
                return false;
            }
        });
    }

    private void payByPointMethodSetup() {
        CommonMethods.printLog("Pay by Point setup.....");
        edt_email.setVisibility(View.GONE);
        txt_edt_email.setVisibility(View.GONE);
        plz_enter_email.setVisibility(View.GONE);
        btn_select_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sessionManager.getCustomerId().isEmpty()) {
                    edt_email.setVisibility(View.GONE);
                    txt_edt_email.setVisibility(View.GONE);
                    plz_enter_email.setVisibility(View.GONE);
                    cus_id = sessionManager.getCustomerId();

                    //mPaymentSession.presentPaymentMethodSelection();
                    UpdateCartStatusInPointsItems();
                } else {
                    Support.hideKeyboard(mContext, edt_email);
                    UpdateCartStatusInPointsItems();
                }
            }
        });

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCartStatusInPointsItems();
                btn_place_order.setEnabled(false);
            }
        });
    }

    private void UpdateCartStatusInPointsItems() {
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("custom_user_points", custom_user_points);
        apiParamMap.put("uid", sessionManager.getUserId());
        CommonMethods.printLog("Update cart apiParamMap : " + apiParamMap);

        mCompositeSubscription.add(
                mStripeService.UpdateCardItems(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                progressDialog.dismiss();
                                UpdateCartStatusInPoints();
                                //showAlertPayment("Success");
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                progressDialog.dismiss();
                                showAlertPayment(throwable.getMessage(), 400);
                            }
                        }));
    }

    private void UpdateCartStatusInPoints() {
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        final String cart_id = sessionManager.getCartId();
        String address_id = sessionManager.getAddressId();
        String uid = sessionManager.getUserId();

        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("cart_id", cart_id);
        apiParamMap.put("address_id", address_id);
        apiParamMap.put("uid", uid);

        mCompositeSubscription.add(
                mStripeService.UpdateCardStatus(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                cartStatusUpdated = true;
                                pDialog.dismiss();
                                addInvoicePoints(String.valueOf(totalUsedPoints), "reference_points_" + cart_id);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                pDialog.dismiss();
                                showAlertPayment(throwable.getMessage(), 400);
                                throwable.printStackTrace();
                            }
                        }));
    }

    private void addInvoicePoints(String amount, String txnId) {
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        String cart_id = sessionManager.getCartId();
        String client_id = sessionManager.getClientId();
        String uid = sessionManager.getUserId();

        // check here
        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("amount", amount);
        apiParamMap.put("cart_id", cart_id);
        apiParamMap.put("client_id", client_id);
        apiParamMap.put("currency", "P");
        apiParamMap.put("payment_status", "Completed");
        apiParamMap.put("txn_id", txnId);
        apiParamMap.put("address_id", sessionManager.getAddressId());
        apiParamMap.put("uid", uid);

        String jsonReq = RetrofitFactory.gson.toJson(apiParamMap);

        CommonMethods.printLog("jsonReq: " + jsonReq);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonReq);

        mCompositeSubscription.add(
                mStripeService.addInvoice(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                pDialog.dismiss();
                                showAlertPayment("Success", 200);

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                pDialog.dismiss();
                                throwable.printStackTrace();
                            }
                        }));
    }

    // $$ Setup
    private void payByDollarMethodSetup() {
        CommonMethods.printLog("dollar method setup.....");
        btn_select_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getText(edt_email).isEmpty()) {
                    Toast.makeText(mContext, "Please enter the email id", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(getText(edt_email))) {
                    Toast.makeText(mContext, "Invalid email id", Toast.LENGTH_LONG).show();
                } else {
                    //updateCartStatus();
                    updateCartItems();
                }
            }
        });

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPurchase();
                btn_place_order.setEnabled(false);
            }
        });
    }

    private void attemptPurchase() {
        CustomerSession.getInstance().retrieveCurrentCustomer(new CustomerSession.CustomerRetrievalListener() {
            @Override
            public void onCustomerRetrieved(@NonNull Customer customer) {
                String sourceId = customer.getDefaultSource();
                if (sourceId == null) {
                    displayError("No payment method selected");
                    return;
                }
                CustomerSource source = customer.getSourceById(sourceId);
                proceedWithPurchaseIf3DSCheckIsNotNecessary(Objects.requireNonNull(source).asSource());
            }

            @Override
            public void onError(int errorCode, @Nullable String errorMessage) {
                displayError("Error getting payment method");
            }
        });
    }

    //one correction updatecartstatus call first before payment gateway invoking...
    private void proceedWithPurchaseIf3DSCheckIsNotNecessary(Source source) {

        if (source == null || !Source.CARD.equals(source.getType())) {
            displayError("Something went wrong - this should be rare");
            return;
        }

        SourceCardData cardData = (SourceCardData) source.getSourceTypeModel();
        if (!SourceCardData.REQUIRED.equals(cardData.getThreeDSecureStatus())) {
            completePurchase();
        }
    }

    private void completePurchase() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("CartId", sessionManager.getCartId());
//            jsonObject.put("UserId", sessionManager.getUserId());
//            jsonObject.put("ClientId", sessionManager.getClientId() + "\n" + extra);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        String ee = "CartId:" + sessionManager.getCartId()
                + "\nUserId:" + sessionManager.getUserId()
                + "\nClientId:" + sessionManager.getClientId()
                + "\n" + extra
                + "\n Shipping Cost:" + shippingCost;
        CommonMethods.printLog("ee : " + ee);
        String uu;
        if (payType.equalsIgnoreCase("PayPoints")) {
            float amount = remainingAmount;
            float amount1 = amount * 100;
            String aa = String.valueOf(amount1);

            if (aa.contains(".")) {
                int pp = aa.indexOf('.');
                uu = aa.substring(0, pp);
            } else {
                uu = aa;
            }
        } else {
            float amount = Float.parseFloat(total);
            float amount1 = amount * 100;
            String aa = String.valueOf(amount1);

            if (aa.contains(".")) {
                int pp = aa.indexOf('.');
                uu = aa.substring(0, pp);
            } else {
                uu = aa;
            }
        }

        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        AddressMd addressMd = new Gson().fromJson(sessionManager.getAddressObject(), AddressMd.class);

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL_STRIPE);
        StripeService stripeService = retrofit.create(StripeService.class);
        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("amount", uu);
        apiParamMap.put("currency", "aud");
        apiParamMap.put("customer", sessionManager.getCustomerId());
        apiParamMap.put("description", ee);
        apiParamMap.put("receipt_email", sessionManager.getClientEmail());
        apiParamMap.put("shipping[name]", sessionManager.getFirstName());
        apiParamMap.put("shipping[phone]", addressMd.getMobile());
        apiParamMap.put("shipping[address][country]", addressMd.getCountry());
        apiParamMap.put("shipping[address][state]", addressMd.getState());
        apiParamMap.put("shipping[address][city]", addressMd.getCity());
        apiParamMap.put("shipping[address][postal_code]", addressMd.getZipcode());
        apiParamMap.put("shipping[address][line1]", addressMd.getAddress1());
        apiParamMap.put("shipping[address][line2]", addressMd.getAddress2());

        CommonMethods.printLog("apiParamMap : " + apiParamMap);
        Observable<ResponseBody> stripeResponse = stripeService.createQueryCharge(apiParamMap);

        mCompositeSubscription.add(stripeResponse
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(
                        new Action0() {
                            @Override
                            public void call() {
                                if (!pDialog.isShowing()) {
                                    pDialog.show();
                                }
                            }
                        })
                .doOnUnsubscribe(
                        new Action0() {
                            @Override
                            public void call() {
                                pDialog.dismiss();
                            }
                        })
                .subscribe(
                        new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                pDialog.dismiss();
                                try {
                                    String rawKey = response.string();
                                    if (!Support.isEmptyOrNot(rawKey)) {
                                        JSONObject jsonObject = new JSONObject(rawKey);
                                        balance_transaction = jsonObject.getString("balance_transaction");
                                        //String amount = jsonObject.getString("amount");
                                        int amount = jsonObject.getInt("amount");
//                                        if (payType.equalsIgnoreCase("PayPoints"))
//                                            ;
                                        String currency = jsonObject.getString("currency");
                                        CommonMethods.printLog("currency : " + currency);
                                        CommonMethods.printLog("amount : " + amount);
                                        addInvoice(String.valueOf(amount), balance_transaction, currency.toUpperCase());
                                    }
                                } catch (Exception iox) {
                                    iox.printStackTrace();
                                }
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                //displayError(throwable.getMessage());
                                updateCartOrderFails(throwable.getMessage());
                                btn_place_order.setEnabled(true);
                            }
                        }));
    }

    private void updateCartOrderFails(final String message) {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest postReq = new StringRequest(Request.Method.POST, API_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CommonMethods.printLog("Response:" + response);
                pDialog.hide();
                if (response != null) {
                    try {
                        if (!message.isEmpty())
                            displayError(message);
                        else
                            fragmentReplacer.replace(new com.pegasusgroup.riarewards.fragments.Home());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("move_to_cart", "1");
                params.put("cart_id", sessionManager.getCartId());
                params.put("uid", sessionManager.getUserId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }
        };

        postReq.setShouldCache(false);
        requestQueue.add(postReq);
    }

    @SuppressWarnings("unused")
    private void addInvoice(String amount, String txnId, String currency) {
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        String cart_id = sessionManager.getCartId();
        String client_id = sessionManager.getClientId();
        String uid = sessionManager.getUserId();

        String uu;

        if (payType.equalsIgnoreCase("PayPoints")) {
//            if (String.valueOf(remainingAmount).contains(".")) {
//                int pp = String.valueOf(remainingAmount).indexOf('.');
//                uu = String.valueOf(remainingAmount).substring(0, pp);
//            } else {
//                uu = String.valueOf(remainingAmount);
//            }
            // Changed on 13-04-2020 as confirmed by Tousif in Skype
            uu = String.valueOf(remainingAmount);
        } else {
//            if (total.contains(".")) {
//                int pp = total.indexOf('.');
//                uu = total.substring(0, pp);
//            } else {
//                uu = total;
//            }
            uu = total; // Changed on 13-04-2020 as confirmed by Tousif in Skype
        }

        // check here
        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("amount", uu);
        apiParamMap.put("cart_id", cart_id);
        apiParamMap.put("client_id", client_id);
        apiParamMap.put("currency", currency);
        apiParamMap.put("payment_status", "Completed");
        apiParamMap.put("txn_id", txnId);
        apiParamMap.put("address_id", sessionManager.getAddressId());
        apiParamMap.put("uid", uid);

        String jsonReq = RetrofitFactory.gson.toJson(apiParamMap);

        CommonMethods.printLog("addInvoice " + apiParamMap);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonReq);

        mCompositeSubscription.add(
                mStripeService.addInvoice(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                pDialog.dismiss();
                                //UpdateCartStatus();
                                showAlertPayment("Success", 200);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                pDialog.dismiss();
                                throwable.printStackTrace();
                            }
                        }));
    }

    private void updateCartItems() {
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("custom_user_points", custom_user_points);
        apiParamMap.put("uid", sessionManager.getUserId());
        CommonMethods.printLog("Update cart apiParamMap : " + apiParamMap);

        mCompositeSubscription.add(
                mStripeService.UpdateCardItems(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                progressDialog.dismiss();
                                updateCartStatus();
                                //showAlertPayment("Success");
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                progressDialog.dismiss();
                                showAlertPayment(throwable.getMessage(), 400);
                            }
                        }));
    }

    private void updateCartStatus() {
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        final String cart_id = sessionManager.getCartId();
        String address_id = sessionManager.getAddressId();
        String uid = sessionManager.getUserId();

        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("cart_id", cart_id);
        apiParamMap.put("address_id", address_id);
        apiParamMap.put("uid", uid);
        CommonMethods.printLog("Update cart apiParamMap : " + apiParamMap);

        mCompositeSubscription.add(
                mStripeService.UpdateCardStatus(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                cartStatusUpdated = true;
                                pDialog.dismiss();
                                if (!sessionManager.getCustomerId().isEmpty()) {
                                    //select_payment.setVisibility(View.GONE);
                                    edt_email.setVisibility(View.GONE);
                                    txt_edt_email.setVisibility(View.GONE);
                                    plz_enter_email.setVisibility(View.GONE);
                                    cus_id = sessionManager.getCustomerId();
                                    setupCustomerSession(cus_id);
                                } else {
                                    Support.hideKeyboard(mContext, edt_email);
                                    CreateStripeCustomer(getText(edt_email));
                                }
                                //showAlertPayment("Success");
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                pDialog.dismiss();
                                showAlertPayment(throwable.getMessage(), 400);
                            }
                        }));
    }

    private void CreateStripeCustomer(String email) {
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("email", email);

        mCompositeSubscription.add(
                mStripeService.CreateStripeCustomer(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                CommonMethods.printLog("CreateStripeCustomer response : " + response.toString());
                                pDialog.dismiss();
                                try {
                                    String rawKey = response.string();
                                    CommonMethods.printLog("rawKey : " + rawKey);
                                    if (!Support.isEmptyOrNot(rawKey)) {
                                        JSONObject jsonObject = new JSONObject(rawKey);
                                        cus_id = jsonObject.getString("id");
                                        sessionManager.setCustomerId(cus_id);
                                        if (!Support.isEmptyOrNot(cus_id)) {
                                            setupCustomerSession(cus_id);
                                        }
                                    }
                                } catch (Exception iox) {
                                    CommonMethods.printLog("iox: " + iox);
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                pDialog.dismiss();
                            }
                        }));
    }

    private void setupCustomerSession(String cus_id) {
        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();
        CustomerSession.initCustomerSession(
                new SampleStoreEphemeralKeyProvider(
                        new SampleStoreEphemeralKeyProvider.ProgressListener() {
                            @Override
                            public void onStringResponse(String string) {
                                pDialog.dismiss();
                                if (string.startsWith("Error: ")) {
                                    //Jijo 16/08/18
                                    new AlertDialog.Builder(mContext).setMessage(string).show();
                                } else {
                                    setupPaymentSession();

                                    if (!mPaymentSession.getPaymentSessionData().isPaymentReadyToCharge()) {
                                        btn_place_order.setEnabled(true);
                                        //btn_place_order.setVisibility(View.VISIBLE);
                                        //select_payment.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }, cus_id));
    }

    @SuppressLint("SetTextI18n")
    private void setupPaymentSession() {
        mPaymentSession = new PaymentSession(Objects.requireNonNull(getActivity()));
        mPaymentSession.init(new PaymentSession.PaymentSessionListener() {
            @Override
            public void onCommunicatingStateChanged(boolean isCommunicating) {

            }

            @Override
            public void onError(int errorCode, @Nullable String errorMessage) {
                displayError(errorMessage);
            }

            @Override
            public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
//                if (data.getShippingMethod() != null) {
//
//                }

                if (data.getSelectedPaymentMethodId() != null) {
                    CustomerSession.getInstance().retrieveCurrentCustomer(new CustomerSession.CustomerRetrievalListener() {
                        @SuppressWarnings("ConstantConditions")
                        @Override
                        public void onCustomerRetrieved(@NonNull Customer customer) {
                            String sourceId = customer.getDefaultSource();
                            if (sourceId == null) {

                                mPaymentSession.presentPaymentMethodSelection();
                                btn_place_order.setEnabled(true);
                                btn_place_order.setVisibility(View.VISIBLE);
                                linear_paymentinfo.setVisibility(View.VISIBLE);
                                btn_select_payment.setVisibility(View.GONE);
                                CustomerSource source = customer.getSourceById(sourceId);
                                payment_source.setText(formatSourceDescription(source.asSource()));
                                btn_select_payment.setText("Change your payment option");

                                return;
                            }

                            if (((sessionManager.getCustomerId()) != null)) {
                                mPaymentSession.presentPaymentMethodSelection();

                                btn_place_order.setEnabled(true);
                                btn_place_order.setVisibility(View.VISIBLE);
                                linear_paymentinfo.setVisibility(View.VISIBLE);
                                btn_select_payment.setVisibility(View.GONE);
                                CustomerSource source = customer.getSourceById(sourceId);
                                payment_source.setText(formatSourceDescription(source.asSource()));
                                btn_select_payment.setText("Change your payment option");
                                return;
                            }
                            btn_place_order.setEnabled(true);
                            btn_place_order.setVisibility(View.VISIBLE);
                            linear_paymentinfo.setVisibility(View.VISIBLE);
                            btn_select_payment.setVisibility(View.GONE);
                            CustomerSource source = customer.getSourceById(sourceId);
                            payment_source.setText(formatSourceDescription(Objects.requireNonNull(source.asSource())));
                            btn_select_payment.setText("Change your payment option");
                            //Toast.makeText(getContext(),"jj "+formatSourceDescription(source.asSource()),Toast.LENGTH_LONG).show();
                            //payment_source.setText("Payment method selected");
                        }

                        @Override
                        public void onError(int errorCode, @Nullable String errorMessage) {
                            displayError(errorMessage);
                        }
                    });
                } else {
                    mPaymentSession.presentPaymentMethodSelection();
                }

                if (data.isPaymentReadyToCharge()) {
                    btn_place_order.setEnabled(true);
                    btn_place_order.setVisibility(View.VISIBLE);
                    btn_select_payment.setVisibility(View.GONE);
                }
            }
        }, new PaymentSessionConfig.Builder().build());
    }

    private void displayError(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @SuppressLint("PrivateResource")
    @SuppressWarnings("StringBufferReplaceableByString")
    private String formatSourceDescription(Source source) {
        if (Source.CARD.equals(source.getType())) {
            SourceCardData sourceCardData = (SourceCardData) source.getSourceTypeModel();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sourceCardData.getBrand()).append(getString(R.string.ending_in)).append(sourceCardData.getLast4());
            return stringBuilder.toString();
        }
        return source.getType();
    }

    @SuppressWarnings("deprecation")
    private void showAlertPayment(String msg, int code) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                mContext).create();
        if (code == 200) {
            alertDialog.setTitle("Payment Successful");
            alertDialog.setMessage(msg);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    mContext.startActivity(new Intent(mContext, Home.class));
                    fragmentReplacer.replace(new com.pegasusgroup.riarewards.fragments.Home());
                }
            });
        } else if (code == 400) {
            alertDialog.setTitle("Payment Error");
            alertDialog.setMessage(msg);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        alertDialog.show();
    }
}