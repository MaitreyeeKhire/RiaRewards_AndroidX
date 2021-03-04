package com.pegasusgroup.riarewards.stripe.service;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.pegasusgroup.riarewards.stripe.RetrofitFactory;
import com.pegasusgroup.riarewards.utils.CommonMethods;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SampleStoreEphemeralKeyProvider implements EphemeralKeyProvider {

    private @NonNull
    CompositeSubscription mCompositeSubscription;
    private @NonNull
    StripeService mStripeService;
    private @NonNull
    ProgressListener mProgressListener;
    private @NonNull
    String cus_id;

    public SampleStoreEphemeralKeyProvider(@NonNull ProgressListener progressListener, String cus_id) {
        this.cus_id = cus_id;
        Retrofit retrofit = RetrofitFactory.getInstance(RetrofitFactory.BASE_URL);
        mStripeService = retrofit.create(StripeService.class);
        mCompositeSubscription = new CompositeSubscription();
        mProgressListener = progressListener;
    }

    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion,
                                   @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", apiVersion);
        apiParamMap.put("cus_id", cus_id);

        CommonMethods.printLog("apiParamMap : " + apiParamMap);

        mCompositeSubscription.add(
                mStripeService.createEphemeralKey(apiParamMap)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody response) {
                                try {
                                    String rawKey = response.string();
                                    keyUpdateListener.onKeyUpdate(rawKey);
                                    mProgressListener.onStringResponse(rawKey);
                                } catch (IOException iox) {
                                    iox.printStackTrace();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                // Jijo 16/08/18
                                mProgressListener.onStringResponse(throwable.getCause().getMessage());
//                                mProgressListener.onStringResponse(throwable.getMessage());
                            }
                        }));
    }

    public interface ProgressListener {
        void onStringResponse(String string);
    }
}
