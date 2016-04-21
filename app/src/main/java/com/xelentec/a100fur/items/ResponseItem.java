package com.xelentec.a100fur.items;


/**
 * Created by Artco on 19.01.2016.
 */
public class ResponseItem {
    String response;
    int responseCode;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseItem(){
        response = "";
        responseCode = 0;
    }
    public ResponseItem(String response, int responseCode){
        this.response = response;
        this.responseCode = responseCode;
    }
}
