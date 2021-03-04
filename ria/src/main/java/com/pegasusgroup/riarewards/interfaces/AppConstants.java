package com.pegasusgroup.riarewards.interfaces;

public interface AppConstants {
    String PREFERENCE_NAME = "RiaRewards";
    String CLIENT_ID = "2207";
    int TIME_OUT = 50000;
    boolean ALLOW_LOG = true;
    String BASE_FOLDER_PATH = "Peberry";
    String AUTHORIZATION = "authorization";
    String CDN_IMG_URL = "http://cdn.peberry.co:80/file/";
    String MESSAGE = "message";
    String FONT_AVENIR_MEDIUM = "fonts/Avenir-Medium.ttf";
    String TERMS_CONDITIONS = "https://ria.myrewards.com.au/display/terms_and_conditions";

    String SERVER_ROOT = "https://www.atwork.com.au/newapp/";

    String USER_REGISTER = SERVER_ROOT + "ria_registerUser.php";
    String USER_LOGIN = "https://ria.myrewards.com.au/newapp/ria_login.php";
    String CONTACT_US = "https://www.atwork.com.au/ria/contact_us";

    String VERIFY_OTP = SERVER_ROOT + "ria_verifyOTP.php";
    String GENERATE_OTP = SERVER_ROOT + "ria_sendVerificationCode.php";
    String RESEND_OTP = SERVER_ROOT + "ria_resendOTP.php";

    String FORGOT_PASSWORD = SERVER_ROOT + "sm_forgotPassword.php";
    String SET_FAV = SERVER_ROOT + "/set_favourites.php";
    String POINT_BRIEF = "https://www.myrewards.com.au/newapp/ria_points_brief.php?user_id=";

    String CATEGORY = "https://www.myrewards.com.au/newapp/" + "get_cat.php?cid=" + CLIENT_ID + "&country=" + "Australia" + "&response_type=json&images=new_cat";
    String ALL_REVIEW = "https://ria.myrewards.com.au/newapp/ria_get_all_reviews.php";
    String ADD_REVIEW = "https://ria.myrewards.com.au/newapp/ria_add_review.php";
    String CAN_REVIEW = "https://ria.myrewards.com.au/newapp/ria_can_user_review.php";

    String GET_LAST_TRANSACTION = "https://ria.myrewards.com.au/newapp/ria_get_last_transaction.php";
    String LAST_TRANSACTION_FEEDBACK_HISTORY = "https://ria.myrewards.com.au/newapp/ria_get_transaction_feedback_history.php";
    String LAST_TRANSACTION_REVIEW = "https://ria.myrewards.com.au/newapp/ria_review_last_transaction.php";

    String UPDATE_SHIPPING_COST = "https://www.myrewards.com.au/newapp/update_smart_shippingcost.php";
    String UPLOAD_IMAGE = SERVER_ROOT + "ria_profile_image_upload.php";
    String GET_IMAGE = SERVER_ROOT + "ria_get_profile_image.php";
    String UPDATE_USER_INFO = SERVER_ROOT + "ria_updateUserInfo.php";
    String GET_USER_INFO = "https://www.myrewards.com.au/newapp/get_user1.php";
    String GET_PROMOTION = "https://ria.myrewards.com.au/newapp/ria_get_promotions.php";
    String PROMOTION_CLAIM = "https://ria.myrewards.com.au/newapp/ria_promotion_claim.php";

    String ADD_TO_CART = "https://www.myrewards.com.au/newapp/add_cart.php";
    String CART_ITEMS = "https://www.myrewards.com.au/newapp/cart_items.php?";
    String SHOW_CART = "https://www.myrewards.com.au/newapp/show_cart.php?";
    String UPDATE_CART = "https://www.myrewards.com.au/newapp/update_cart.php";

    String REDEEM = "https://www.myrewards.com.au/newapp/redeemed.php";
    String REDEEM_ACTION = "https://www.myrewards.com.au/newapp/redeem_tracker.php";
    String USER_ACTION = "https://www.myrewards.com.au/newapp/put_user_actions.php";

    String API_ENDPOINT = "https://www.atwork.com.au/newapp/update_cart_status.php";
}