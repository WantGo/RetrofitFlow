package com.wentgo.retrofit.flow.lib;


import com.google.gson.annotations.SerializedName;


public class BaseEntity<T> {

    @SerializedName("success")
    private boolean success;
    @SerializedName("errorCode")
    private String code;
    @SerializedName("errorMsg")
    private String msg;
    @SerializedName("data")
    private T data;

    public boolean getSuccess() {
        return success || "0".equals(code);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        if (code == null) {
            code = "";
        }
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        if (msg == null) {
            msg = "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
