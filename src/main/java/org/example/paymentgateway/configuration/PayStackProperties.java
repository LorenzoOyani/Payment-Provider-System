package org.example.paymentgateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

@Primary
@ConfigurationProperties(prefix = "paystack")
public class PayStackProperties {
    private String url;
    private String secretKey;


    public String getUrl() {
        return url;
    }

    public String getSecretKey() {
        return secretKey;
    }

    ///  FOR DATA-BINDING.
    public void setUrl(String url) {
        this.url = url;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
