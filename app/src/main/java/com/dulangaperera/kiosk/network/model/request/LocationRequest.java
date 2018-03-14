package com.dulangaperera.kiosk.network.model.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lkfaswuser2 on 11/20/2017.
 */

public class LocationRequest implements Serializable {

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;
}
