package com.tohsoft.app.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Phong on 11/9/2016.
 */

public class User implements Serializable {
    @SerializedName("user_id")
    @Expose
    public String user_id = "";

}
