package com.dulangaperera.kiosk.network.model.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mihira on 11/3/2017.
 */

public class WebPageRequest implements Serializable {

    @SerializedName("deviceDate")
    public String deviceDate;

    @SerializedName("deviceTime")
    public String deviceTime;

    @SerializedName("telephoneNo")
    public String telephoneNo;

    @SerializedName("signalStrength")
    public String signalStrength;

    @SerializedName("carrierLength")
    public String carrierLength;
}
