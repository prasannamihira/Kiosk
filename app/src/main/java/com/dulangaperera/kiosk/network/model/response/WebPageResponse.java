package com.dulangaperera.kiosk.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mihira on 11/3/2017.
 */

public class WebPageResponse implements Serializable {

    @SerializedName("action")
    public String action;

    @SerializedName("url")
    public String newUrl;

    @SerializedName("isTimeCorrect")
    public String isTimeCorrect;
}
