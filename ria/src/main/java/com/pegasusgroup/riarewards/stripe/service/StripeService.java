package com.pegasusgroup.riarewards.stripe.service;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * The {@link retrofit2.Retrofit} interface that creates our API service.
 */
public interface StripeService {

    //    @Headers("Authorization:Bearer sk_live_u6fl8chooaDHi6asS0NhceJi")
//    @Headers("Authorization:Bearer sk_test_yb1n5THFv5CncjmECieJmzPa")
    @FormUrlEncoded
    @POST("charges")
    Observable<ResponseBody> createQueryCharge(@FieldMap Map<String, String> params);

    /* //@Headers("Authorization:Bearer sk_live_u6fl8chooaDHi6asS0NhceJi")
     @Headers("Authorization:Bearer sk_test_yb1n5THFv5CncjmECieJmzPa")
     @GET("charges")
     Observable<ResponseBody> createQueryCharge(@Query("amount") String amount, @Query("currency") String currency, @Query("customer") String customer, @Query("description") String description, @Query("receipt_email") String receipt_email, @Query("shipping[name]") String shippingname, @Query("shipping[phone]") String shippingphone, @Query("shipping[address][country]") String shippingaddresscountry, @Query("shipping[address][state]") String shippingaddressstate, @Query("shipping[address][city]") String shippingaddresscity, @Query("shipping[address][postal_code]") String shippingaddresspostal, @Query("shipping[address][line1]") String shippingaddressline1, @Query("shipping[address][line2]") String shippingaddressline2);
 */
    @FormUrlEncoded
    @POST("test_stripe_payment.php")
    Observable<ResponseBody> createEphemeralKey(@FieldMap Map<String, String> apiVersionMap);

    @FormUrlEncoded
    @POST("test_create_stripe_customer.php")
    Observable<ResponseBody> CreateStripeCustomer(@FieldMap Map<String, String> apiVersionMap);

    // transactionId= reference_points_cartid
    //@Headers("Content-Type: application/json; charset=utf-8")
    @PUT("add_invoice.php")
    Observable<ResponseBody> addInvoice(@Body RequestBody apiVersionMap);

    //fist,  amout=paypoints used by the user
    @FormUrlEncoded
    @POST("update_cart_status.php")
    Observable<ResponseBody> UpdateCardStatus(@FieldMap Map<String, String> apiVersionMap);

    @FormUrlEncoded
    @POST("update_cart_items.php")
    Observable<ResponseBody> UpdateCardItems(@FieldMap Map<String, String> apiVersionMap);

}
