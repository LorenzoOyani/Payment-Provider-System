package org.example.paymentgateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

@Primary
@ConfigurationProperties(prefix = "paystack")
public class PayStackProperties {
    private String url;
    private String secretKey;

    public String getPublicKey() {
        return publicKey;
    }


    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    private String publicKey;


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
