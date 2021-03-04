package com.pegasusgroup.riarewards.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.pegasusgroup.riarewards.R;
import com.pegasusgroup.riarewards.stripe.RetrofitFactory;
import com.pegasusgroup.riarewards.stripe.service.StripeService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrainTreePay extends BaseFragment implements PaymentMethodNonceCreatedListener,
        BraintreeCancelListener,
        BraintreeErrorListener {

    //    private String get_token = "http://192.168.43.138:4000/get_token";
    private final String send_payment_details = "https://www.atwork.com.au/carts/bt_app_transaction";
    private final String API_ENDPOINT = "https://www.atwork.com.au/newapp/update_cart_status.php";
    private String payType;
    private float totalAmount;
    private float remainingAmount;
    private float totalUsedPoints;
    private String token;
    private int CARD_REQUEST_CODE = 3;
    private ProgressDialog progress;
    //    private AppCompatButton cmdPay;
    private AppCompatButton cmdPoint;
    private HashMap<String, String> paramHash;
    private AppCompatTextView txtMessage;
    private String userId;
    private String orderId;
    private CompositeSubscription mCompositeSubscription;
    private StripeService mStripeService;

    private String amount;
    private DropInRequest dropInRequest;

    private String extra;
    private String total;
    private String shippingCost;

    public BrainTreePay() {
        // Required empty public constructor
    }

    public static BrainTreePay newInstance(String payType, float totalAmount, float remainingAmount,
                                           float totalUsedPoints) {
        BrainTreePay brainTreePay = new BrainTreePay();
        brainTreePay.payType = payType;
        brainTreePay.totalAmount = totalAmount;
        brainTreePay.remainingAmount = remainingAmount;
        brainTreePay.totalUsedPoints = totalUsedPoints;
        return brainTreePay;
    }

    @Override
    protected View getLayoutResource() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_brain_tree_pay, container, false);
    }

    @Override
    protected void initComponents(View view) {
        cmdPoint = findViewById(R.id.cmdPoint);
        mCompositeSubscription = new CompositeSubscription();

        txtMessage = findViewById(R.id.txtMessage);

//        cmdPay.setOnClickListener(view -> {
//            DropInRequest dropInRequest = new DropInRequest().clientToken(token);
//            dropInRequest.collectDeviceData(true);
//            dropInRequest.vaultManager(true);
//            startActivityForResult(dropInRequest.getIntent(getActivity()), CARD_REQUEST_CODE);
//        });

        userId = sessionManager.getUserId();
        orderId = sessionManager.getCartId();

        extra = Objects.requireNonNull(getArguments()).getString("extra");
        amount = getArguments().getString("total");
        totalAmount = Float.parseFloat(amount);
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
    }

    @Override
    protected void setListeners() {
        if (payType != null) {
            if (payType.equalsIgnoreCase("Points")) {
                cmdPoint.setVisibility(View.VISIBLE);
                payByPointMethodSetup();
            } else if (payType.equalsIgnoreCase("PayPoints")) {
                if (remainingAmount == 0.00 || remainingAmount < 0.00) {
                    payByPointMethodSetup();
                } else {
                    payByDollarMethodSetup();
                }
            } else {
                payByDollarMethodSetup();
            }
        }
    }

    private void payByPointMethodSetup() {
        System.out.println("Pay by Point setup.....");
        cmdPoint.setOnClickListener(view ->
                updateCartStatusInPoints()
        );
    }

    private void updateCartStatusInPoints() {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Processing...");
        pDialog.setCancelable(false);
        pDialog.show();

        mCompositeSubscription = new CompositeSubscription();
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
                        .subscribe(response -> {
                            pDialog.dismiss();
                            System.out.println("update cart " + response);
                            addInvoicePoints(String.valueOf(totalUsedPoints), "reference_points_" + cart_id);
                        }, throwable -> {
                            pDialog.dismiss();
                            throwable.printStackTrace();
                            showAlertPayment(throwable.getMessage());
                        }));
    }

    private void addInvoicePoints(String amount, String txnId) {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
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
        System.out.println("jsonReq: " + jsonReq);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonReq);

        mCompositeSubscription.add(
                mStripeService.addInvoice(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            pDialog.dismiss();
                            System.out.println("add invoice " + response);
                            showAlertPayment("Success");
                        }, throwable -> {
                            pDialog.dismiss();
                            throwable.printStackTrace();
                        }));
    }

    private void payByDollarMethodSetup() {
        System.out.println("dollar method setup.....");
        getToken();
//        showSuccess("Order places successfully");
    }

    private void getToken() {
        try {
            String get_token = "https://www.myrewards.com.au/carts/get_bt_token/" + orderId;
            System.out.println(get_token);
            progress = new ProgressDialog(getActivity());
            progress.setCancelable(false);
            progress.setMessage("Please wait");
            progress.show();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, get_token, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 200) {
                        token = jsonObject.getString("token");
                        System.out.println("token : " + token);
                        progress.dismiss();
//                        cmdPay.setVisibility(View.VISIBLE);
                        txtMessage.setVisibility(View.GONE);

                        dropInRequest = new DropInRequest().clientToken(token).amount(amount);
                        dropInRequest.collectDeviceData(true);
                        dropInRequest.vaultManager(true);
//                        dropInRequest.disablePayPal();
                        startActivityForResult(dropInRequest.getIntent(mContext), CARD_REQUEST_CODE);
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                System.out.println("Error " + error.getMessage());
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText("Server Error");
                error.printStackTrace();
                progress.dismiss();
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CARD_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                    String deviceData;
                    deviceData = result.getDeviceData();
                    PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                    String stringNonce = Objects.requireNonNull(nonce).getNonce();
                    // Send payment price with the nonce
                    // use the result to update your UI and send the payment method nonce to your server
//                    String description = "CartId:" + sessionManager.getParticularField(SessionManager.CART_ID)
//                            + "\nUserId:" + sessionManager.getParticularField(SessionManager.USER_NAME)
//                            + "\nClientId:" + sessionManager.getParticularField(SessionManager.CLIENT_NAME)
//                            + "\n" + MycartFragment.extra + "\n Shipping Cost:" + MycartFragment.pp;

                    paramHash = new HashMap<>();
                    if (payType.equals("PayPoints")) {
                        if (remainingAmount > 0.0) {
                            paramHash.put("amount", String.valueOf(remainingAmount).trim());
                            amount = String.valueOf(remainingAmount).trim();
                        } else {
                            paramHash.put("amount", String.valueOf(totalAmount).trim());
                            amount = String.valueOf(totalAmount).trim();
                        }
                    } else {
                        paramHash.put("amount", String.valueOf(totalAmount).trim());
                        amount = String.valueOf(totalAmount).trim();
                    }

                    paramHash.put("cartid", orderId.trim());
                    paramHash.put("userid", userId.trim());
                    paramHash.put("payment_method_nonce", String.valueOf(stringNonce).trim());
                    paramHash.put("device_data", Objects.requireNonNull(deviceData).trim());
                    //paramHash.put("description", description);
                    paramHash.put("address_id", sessionManager.getAddressId().trim());
                    sendPaymentDetails();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void updateCartStatus(String message) {
//        final ProgressDialog pDialog = new ProgressDialog(getActivity());
//        pDialog.setMessage("Processing...");
//        pDialog.setCancelable(false);
//        pDialog.show();
//
//        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
//        StripeService mStripeService = retrofit.create(StripeService.class);
//
//        final String cart_id = sessionManager.getParticularField(SessionManager.CART_ID);
//        String address_id = sessionManager.getParticularField(SessionManager.ADDRESS_ID);
//        String uid = sessionManager.getParticularField(SessionManager.USER_ID);
//
//        Map<String, String> apiParamMap = new HashMap<>();
//        apiParamMap.put("cart_id", cart_id);
//        apiParamMap.put("address_id", address_id);
//        apiParamMap.put("uid", uid);
//
//        mCompositeSubscription.add(
//                mStripeService.UpdateCardStatus(apiParamMap)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(response -> {
//                            pDialog.dismiss();
//                            sendPaymentDetails();
//                        }, throwable -> {
//                            pDialog.dismiss();
//                            showAlertPayment(throwable.getMessage());
//                            System.out.println(throwable.getMessage());
//                            System.out.println(throwable.getCause().getMessage());
//                        }));
//    }

    private void showSuccess(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.success_dialog, null);
        AppCompatTextView txtSuccess = dialogLayout.findViewById(R.id.txtSuccess);
        AppCompatTextView txtOK = dialogLayout.findViewById(R.id.txtOK);
        txtSuccess.setText(msg);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        txtOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), com.pegasusgroup.riarewards.activity.Home.class));
            }
        });

        dialog.show();
    }

    @SuppressWarnings("deprecation")
    private void showAlertPayment(String msg) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                getActivity()).create();
        alertDialog.setTitle("Message");
        alertDialog.setMessage(msg);
        alertDialog.setButton("OK", (dialog, which) -> {
            dialog.dismiss();
            Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), com.pegasusgroup.riarewards.activity.Home.class));
        });
        alertDialog.show();
    }

    private void sendPaymentDetails() {
        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setMessage("Please wait...");
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_payment_details,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response : " + response);
                        System.out.println("length : " + response.length());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 200) {
                                showSuccess(jsonObject.getString("message"));
                            } else {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                updateCartOrderFails("Transaction failed");
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                            updateCartOrderFails("Transaction failed");
                        } finally {
                            progress.dismiss();
                        }
//                        Log.d("mylog", "Final Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley error : " + error.toString());
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, Objects.requireNonNull(Objects.requireNonNull(paramHash.get(key)).trim()));
                }

                System.out.println("param : " + params.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

//    private void addInvoice(String txnId) {
//        final ProgressDialog pDialog = new ProgressDialog(getActivity());
//        pDialog.setMessage("Processing...");
//        pDialog.setCancelable(false);
//        pDialog.show();
//
//        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
//        mStripeService = retrofit.create(StripeService.class);
//
//        String cart_id = sessionManager.getParticularField(SessionManager.CART_ID);
//        String client_id = sessionManager.getParticularField(SessionManager.CLIENT_ID);
//        String uid = sessionManager.getParticularField(SessionManager.USER_ID);
//
//        String uu;
//
//        if (payType.equalsIgnoreCase("PayPoints")) {
//            uu = String.valueOf(remainingAmount);
//        } else {
//            uu = MycartFragment.tt;
//        }
//
//        // check here
//        Map<String, String> apiParamMap = new HashMap<>();
//        apiParamMap.put("amount", uu);
//        apiParamMap.put("cart_id", cart_id);
//        apiParamMap.put("client_id", client_id);
//        apiParamMap.put("currency", "AUD");
//        apiParamMap.put("payment_status", "Completed");
//        apiParamMap.put("txn_id", txnId);
//        apiParamMap.put("address_id", sessionManager.getParticularField(SessionManager.ADDRESS_ID));
//        apiParamMap.put("uid", uid);
//
//        String jsonReq = RetrofitFactory.gson.toJson(apiParamMap);
//
//        System.out.println("jsonReq: " + jsonReq);
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonReq);
//
//        mCompositeSubscription.add(
//                mStripeService.addInvoice(body)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(response -> {
//                            pDialog.dismiss();
//                            System.out.println("addinv " + response);
//                            showAlertPayment("Success");
//                        }, throwable -> {
//                            pDialog.dismiss();
//                            throwable.printStackTrace();
//                        }));
//    }

    @Override
    public void onCancel(int requestCode) {
        System.out.println("onCancel : " + requestCode);
    }

    @Override
    public void onError(Exception error) {
        updateCartOrderFails(error.getMessage());
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        System.out.println("onPaymentMethodNonceCreated");
    }

    private void updateCartOrderFails(final String message) {
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("updating...");
        pDialog.show();

        StringRequest postReq = new StringRequest(Request.Method.POST, API_ENDPOINT, response -> {
            pDialog.hide();
            if (response != null) {
                try {
                    displayError(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, error -> pDialog.hide()) {
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

    private void displayError(String errorMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity())).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(errorMessage);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}