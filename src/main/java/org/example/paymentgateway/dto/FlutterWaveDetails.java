package org.example.paymentgateway.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class FlutterWaveDetails {
    private String tx_ref;
    private String link;
    private String status;
}
