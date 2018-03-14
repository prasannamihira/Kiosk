package com.dulangaperera.kiosk.network;

import android.util.Log;

import com.dulangaperera.kiosk.network.model.request.SupportRequest;
import com.dulangaperera.kiosk.network.model.request.WebPageRequest;
import com.dulangaperera.kiosk.network.model.response.SupportResponse;
import com.dulangaperera.kiosk.network.model.response.WebPageResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Mihira on 11/3/2017.
 */

public class KioskApiService {

    private KioskApi kioskApi;
    private static KioskApiService kioskApiService;

    public static KioskApiService getInstance() {
        if (kioskApiService == null)
            kioskApiService = new KioskApiService();
        return kioskApiService;
    }

    /**
     * Constructor
     */
    private KioskApiService() {
        Retrofit retrofit = null;
        try {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(interceptor)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build();

            RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();


            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(rxAdapter)
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            this.kioskApi = retrofit.create(KioskApi.class);
        } catch (Exception ex) {
            Log.d("TradeFinanceApi", ex.toString());
        }
    }

    /**
     * Web page request
     * @param webPageRequest
     * @return
     */
    public Observable<WebPageResponse> webPageRequest(WebPageRequest webPageRequest){
        return this.kioskApi.webPageRequest(webPageRequest);
    }

    /**
     * Support request
     * @param supportRequest
     * @return
     */
    public Observable<SupportResponse> supportRequest(SupportRequest supportRequest){
        return this.kioskApi.supportRequest(supportRequest);
    }
}
