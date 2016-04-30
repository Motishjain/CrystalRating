package com.admin.webservice.response_objects;

/**
 * Created by Admin on 3/15/2016.
 */
public class SaveServiceReponse {
    private boolean success;
    private Object data;
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
