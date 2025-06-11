package org.example.paymentgateway.dto;

public class FlutterWaveResponse {
    private String status;
    private String message;
    private FlutterWavePaymentDetails details;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public FlutterWavePaymentDetails getDetails() {
        return details;
    }

    public void setDetails(FlutterWavePaymentDetails details) {
        this.details = details;
    }

//    public FlutterWaveResponseData getData() {
//        return data;
//    }
}
