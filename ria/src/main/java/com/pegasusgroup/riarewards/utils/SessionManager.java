package com.pegasusgroup.riarewards.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.pegasusgroup.riarewards.interfaces.AppConstants;


public class SessionManager {
    private static final String IS_LOGIN = "IsUserLoggedIn";
    private static final String DEVICE_ID = "deviceId";
    private static final String USER_ID = "userId";
    private static final String CART_ID = "cart_id";
    private static final String CLIENT_ID = "clientId";
    private static final String USER_NAME = "userName";
    private static final String EMAIL = "Email";
    private static final String BEARER = "bearer";
    private static final String PASSWORD = "password";
    private static final String MOBILE = "mobile";
    private static final String CLIENT_NAME = "CLIENT_NAME";
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String CLIENT_EMAIL = "CLIENT_EMAIL";
    private static final String BIRTH_DATE = "BIRTH_DATE";
    private static final String GENDER = "GENDER";
    private static final String NEWS_LETTER = "NEWS_LETTER";
    private static final String PROFILE_IMAGE = "PROFILE_IMAGE";
    private static final String ADDRESS_COUNTER = "ADDRESS_COUNTER";
    private static final String SHIPPING_ADDRESS = "SHIPPING_ADDRESS";
    private static final String CART_COUNT = "CART_COUNT";
    private static final String USER_POINT = "USER_POINT";
    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String ADDRESS_ID = "ADDRESS_ID";
    private static final String ADDRESS_OBJECT = "ADDRESS_OBJECT";
    private static final String POINTS_CONVERSION = "POINTS_CONVERSION";//points_conversion
    private static final String POINTS = "POINTS";
    private static final String MERCHANT_FEE = "MERCHANT_FEE";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(
                AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getPassword() {
        return preferences.getString(PASSWORD, "");
    }

    public void setPassword(String password) {
        editor.putString(PASSWORD, password).commit();
    }

    public String getEmail() {
        return preferences.getString(EMAIL, "");
    }

    public void setEmail(String email) {
        editor.putString(EMAIL, email).commit();
    }

    public String getMobile() {
        return preferences.getString(MOBILE, "");
    }

    public void setMobile(String mobile) {
        editor.putString(MOBILE, mobile).commit();
    }

    public String getUserName() {
        return preferences.getString(USER_NAME, "");
    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName).commit();
    }

    public String getUserId() {
        return preferences.getString(USER_ID, "");
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId).commit();
    }

    public String getClientId() {
        return preferences.getString(CLIENT_ID, AppConstants.CLIENT_ID);
    }

    public void setClientId(String clientId) {
        editor.putString(CLIENT_ID, clientId).commit();
    }

    public String getClientName() {
        return preferences.getString(CLIENT_NAME, "");
    }

    public void setClientName(String clientName) {
        editor.putString(CLIENT_NAME, clientName).commit();
    }

    public String getFirstName() {
        return preferences.getString(FIRST_NAME, "");
    }

    public void setFirstName(String firstName) {
        editor.putString(FIRST_NAME, firstName).commit();
    }

    public String getLastName() {
        return preferences.getString(LAST_NAME, "");
    }

    public void setLastName(String lastName) {
        editor.putString(LAST_NAME, lastName).commit();
    }

    public String getClientEmail() {
        return preferences.getString(CLIENT_EMAIL, "");
    }

    public void setClientEmail(String clientEmail) {
        editor.putString(CLIENT_EMAIL, clientEmail).commit();
    }

    public String getBirthDate() {
        return preferences.getString(BIRTH_DATE, "");
    }

    public void setBirthDate(String birthDate) {
        editor.putString(BIRTH_DATE, birthDate).commit();
    }

    public String getGender() {
        return preferences.getString(GENDER, "");
    }

    public void setGender(String gender) {
        editor.putString(GENDER, gender).commit();
    }

    public String getNewsLetter() {
        return preferences.getString(NEWS_LETTER, "");
    }

    public void setNewsLetter(String newsLetter) {
        editor.putString(NEWS_LETTER, newsLetter).commit();
    }

    public String getProfileImage() {
        return preferences.getString(PROFILE_IMAGE, "");
    }

    public void setProfileImage(String profileImage) {
        editor.putString(PROFILE_IMAGE, profileImage).commit();
    }

    public String getAddressCounter() {
        return preferences.getString(ADDRESS_COUNTER, "0");
    }

    public void setAddressCounter(String addressCounter) {
        editor.putString(ADDRESS_COUNTER, addressCounter).commit();
    }

    public String getShippingAddress() {
        return preferences.getString(SHIPPING_ADDRESS, "");
    }

    public void setShippingAddress(String shippingAddress) {
        editor.putString(SHIPPING_ADDRESS, shippingAddress).commit();
    }

    public String getCartId() {
        return preferences.getString(CART_ID, "");
    }

    public void setCartId(String cartId) {
        editor.putString(CART_ID, cartId).commit();
    }

    public String getCartCount() {
        return preferences.getString(CART_COUNT, "0");
    }

    public void setCartCount(String cartCount) {
        editor.putString(CART_COUNT, cartCount).commit();
    }

    public String getUserPoint() {
        return preferences.getString(USER_POINT, "0");
    }

    public void setUserPoint(String userPoint) {
        editor.putString(USER_POINT, userPoint).commit();
    }

    public String getCustomerId() {
        return preferences.getString(CUSTOMER_ID, "");
    }

    public void setCustomerId(String customerId) {
        editor.putString(CUSTOMER_ID, customerId).commit();
    }

    public String getAddressId() {
        return preferences.getString(ADDRESS_ID, "");
    }

    public void setAddressId(String addressId) {
        editor.putString(ADDRESS_ID, addressId).commit();
    }

    public String getAddressObject() {
        return preferences.getString(ADDRESS_OBJECT, "");
    }

    public void setAddressObject(String addressObject) {
        editor.putString(ADDRESS_OBJECT, addressObject).commit();
    }

    public String getPointsConversion() {
        return preferences.getString(POINTS_CONVERSION, "1");
    }

    public void setPointsConversion(String pointsConversion) {
        editor.putString(POINTS_CONVERSION, pointsConversion).commit();
    }

    public Float getPoints() {
        return preferences.getFloat(POINTS, 0.0F);
    }

    public void setPoints(Float points) {
        editor.putFloat(POINTS, points).commit();
    }

    public String getMerchantFee() {
        return preferences.getString(MERCHANT_FEE, "0.0");
    }

    public void setMerchantFee(String merchantFee) {
        editor.putString(MERCHANT_FEE, merchantFee).commit();
    }

    public void clearSharedPreferenceData() {
        editor.clear().commit();
    }
}