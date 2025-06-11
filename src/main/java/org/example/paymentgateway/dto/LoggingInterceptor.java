package org.example.paymentgateway.dto;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;


public class LoggingInterceptor implements Interceptor {

    private static final Logger log = LogManager.getLogger(LoggingInterceptor.class);
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        var request = chain.request();

        log.info("sending request to {}, with headers {}", request.url(), request.headers());

        Response response = chain.proceed(request);
        log.info("receiving response with code {} and body {}", response.code(), response.peekBody(1024).string());
        return response;

    }
}
