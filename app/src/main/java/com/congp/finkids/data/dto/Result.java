package com.congp.finkids.data.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ominext on 10/2/2017.
 */

public class Result {


    @SerializedName("result")
    @Expose
    private String result;

    /**
     * No args constructor for use in serialization
     */
    public Result() {
    }

    /**
     * @param result
     */
    public Result(String result) {
        super();
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
