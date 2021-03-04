package com.pegasusgroup.riarewards.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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

import org.json.JSONException;
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

public class Payment extends BaseAppCompatActivity {

    //    private static final String TAG = com.pegasusgroup.riarewards.fragment.Payment.class.getSimpleName();
    //    private static final String PUBLISHABLE_KEY = "pk_live_1Sx0DwDFuLMYO5Gc8NusmU2p";
    private static final String PUBLISHABLE_KEY = "pk_test_O25zyXXj74gl3XRbVoXjaHaE";
    private final String API_ENDPOINT = "https://www.atwork.com.au/newapp/update_cart_status.php";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_payment;
    }

    @Override
    protected void initComponents() {
        toolbar = findViewById(R.id.toolbar);

        extra = Objects.requireNonNull(getIntent().getExtras()).getString("extra");
        total = getIntent().getExtras().getString("total");
        payType = getIntent().getExtras().getString("payType");
        totalUsedPoints = Float.parseFloat(Objects.requireNonNull(getIntent().getExtras().getString("totalUsedPoints")));
        remainingAmount = Float.parseFloat(Objects.requireNonNull(getIntent().getExtras().getString("remainingAmount")));

        PaymentConfiguration.init(PUBLISHABLE_KEY);

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void setListeners() {
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        AddressMd addressMd = new Gson().fromJson(sessionManager.getAddressObject(), AddressMd.class);
        sessionManager.setAddressId(addressMd.getId());

        edt_email.setText(sessionManager.getClientEmail());

        if (payType.equalsIgnoreCase("Points")) {
            btn_select_payment.setText("Place order by Points");
            payByPointMethodSetup();
        } else if (payType.equalsIgnoreCase("PayPoints")) {
            if (remainingAmount == 0.00 || remainingAmount < 0.00) {
                btn_select_payment.setText("Place order by Points");
                payByPointMethodSetup();
            } else {
                payByDollarMethodSetup();
            }
        } else {
            mPaymentSession = new PaymentSession(Payment.this);
            payByDollarMethodSetup();
        }
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
                    UpdateCartStatusInPoints();
                } else {
                    Support.hideKeyboard(Payment.this, edt_email);
                    UpdateCartStatusInPoints();
                }
            }
        });

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCartStatusInPoints();
                btn_place_order.setEnabled(false);
            }
        });
    }

    private void UpdateCartStatusInPoints() {
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
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
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
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
                    Toast.makeText(Payment.this, "Please enter the email id", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(getText(edt_email))) {
                    Toast.makeText(Payment.this, "Invalid email id", Toast.LENGTH_LONG).show();
                } else {
                    UpdateCartStatus();
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CartId", sessionManager.getCartId());
            jsonObject.put("UserId", sessionManager.getUserId());
            jsonObject.put("ClientId", sessionManager.getClientId() + "\n" + extra);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String ee = "CartId:" + sessionManager.getCartId()
                + "\nUserId:" + sessionManager.getUserId()
                + "\nClientId:" + sessionManager.getClientId()
                + "\n" + extra
                + "\n Shipping Cost:" + total;
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
            float amount = Float.valueOf(total);
            float amount1 = amount * 100;
            String aa = String.valueOf(amount1);

            if (aa.contains(".")) {
                int pp = aa.indexOf('.');
                uu = aa.substring(0, pp);
            } else {
                uu = aa;
            }
        }

        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(Payment.this);
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
        pDialog.setMessage("updating...");
        pDialog.show();

        StringRequest postReq = new StringRequest(Request.Method.POST, API_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CommonMethods.printLog("Response:" + response);
                pDialog.hide();
                if (response != null) {
                    try {
                        displayError(message);
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

    private void addInvoice(String amount, String txnId, String currency) {
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);

        String cart_id = sessionManager.getCartId();
        String client_id = sessionManager.getClientId();
        String uid = sessionManager.getUserId();

        String uu = "0";

        if (payType.equalsIgnoreCase("PayPoints")) {
            if (String.valueOf(remainingAmount).contains(".")) {
                int pp = String.valueOf(remainingAmount).indexOf('.');
                uu = String.valueOf(remainingAmount).substring(0, pp);
            } else {
                uu = String.valueOf(remainingAmount);
            }
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

    private void UpdateCartStatus() {
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
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
                                pDialog.dismiss();
                                if (!sessionManager.getCustomerId().isEmpty()) {
                                    //select_payment.setVisibility(View.GONE);
                                    edt_email.setVisibility(View.GONE);
                                    txt_edt_email.setVisibility(View.GONE);
                                    plz_enter_email.setVisibility(View.GONE);
                                    cus_id = sessionManager.getCustomerId();
                                    setupCustomerSession(cus_id);
                                } else {
                                    Support.hideKeyboard(Payment.this, edt_email);
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
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
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
        final ProgressDialog pDialog = new ProgressDialog(Payment.this);
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
                                    new AlertDialog.Builder(Payment.this).setMessage(string).show();
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
        mPaymentSession = new PaymentSession(Payment.this);
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
        AlertDialog alertDialog = new AlertDialog.Builder(Payment.this).create();
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
                Payment.this).create();
        if (code == 200) {
            alertDialog.setTitle("Payment Successful");
            alertDialog.setMessage(msg);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Payment.this.startActivity(new Intent(Payment.this, Home.class));
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