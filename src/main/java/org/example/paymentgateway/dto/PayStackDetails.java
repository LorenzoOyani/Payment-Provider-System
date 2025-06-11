package org.example.paymentgateway.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PayStackDetails {
    private String paystackReference;
    private String accessCode;
    private String status;
}
