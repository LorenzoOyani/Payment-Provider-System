//package org.example.paymentgateway.configuration;
//
//import org.example.paymentgateway.entities.RateLimitException;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
//
//import java.time.Duration;
//
//@Configuration
//@EnableConfigurationProperties
//public class PaymentConfiguration {
//
//
//
//    @Bean
//    public CircuitBreakerConfig circuitBreakerConfig() {
//        return CircuitBreakerConfig.custom()
//                .failureRateThreshold(50)
//                .slowCallRateThreshold(50)
//                .waitDurationInOpenState(Duration.ofMillis(1000))
//                .slowCallDurationThreshold(Duration.ofSeconds(2))
//                .permittedNumberOfCallsInHalfOpenState(3)
//                .minimumNumberOfCalls(10)
//                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
//                .slidingWindowSize(5)
//                .recordException(e -> !(e instanceof RateLimitException)).build();
//
//    }
//
//}
//
//}
