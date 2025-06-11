package org.example.paymentgateway.dto;

public class PayStackResponse {
    private boolean status;
    private String message;
    private Object metaData;


    public PayStackResponse(boolean status, String message, Object metaData) {
        this.status = status;
        this.message = message;
        this.metaData = metaData;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getMetaData() {
        return metaData;
    }

    public void setMetaData(Object metaData) {
        this.metaData = metaData;
    }
}
