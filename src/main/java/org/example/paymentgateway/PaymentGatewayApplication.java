package org.example.paymentgateway;

import org.example.paymentgateway.configuration.FlutterProperties;
import org.example.paymentgateway.configuration.PayStackProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({PayStackProperties.class, FlutterProperties.class})
public class PaymentGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentGatewayApplication.class, args);
    }

}
