package com.dulangaperera.kiosk.network;

import com.dulangaperera.kiosk.network.model.request.SupportRequest;
import com.dulangaperera.kiosk.network.model.request.WebPageRequest;
import com.dulangaperera.kiosk.network.model.response.SupportResponse;
import com.dulangaperera.kiosk.network.model.response.WebPageResponse;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Mihira on 11/3/2017.
 */

public interface KioskApi {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: utf-8"
    })

    /**
     * Web page request
     *
     * @param webPageRequest
     */
    @POST("webPageRequest")
    Observable<WebPageResponse> webPageRequest(@Body WebPageRequest webPageRequest);

    /**
     * Support request with screen shot
     * @param supportRequest
     * @return
     */
    @POST("supportRequest")
    Observable<SupportResponse> supportRequest(@Body SupportRequest supportRequest);
}
