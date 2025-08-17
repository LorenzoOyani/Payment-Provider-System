package org.example.paymentgateway.mapper;

import org.example.paymentgateway.dto.PaymentResponse;
import org.example.paymentgateway.dto.PaymentVerificationResponse;
import org.example.paymentgateway.enums.Currency;
import org.example.paymentgateway.entities.Payment;
import org.example.paymentgateway.entities.PaymentDto;
import org.example.paymentgateway.enums.PaymentStatus;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Mapper(componentModel = "spring", imports = {LocalDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)


)
public interface PaymentMapper {


//    @Mapping(source = "metaData", target = "metaData", qualifiedByName = "mapMetaData")
    Payment toPayment(PaymentDto paymentDto);


//    @Mapping(source = "metaData", target = "metaData", qualifiedByName = "mapMetaData")
//    @Mapping(target = "created_At", expression = "java(LocalDateTime.now())") // Match actual field name
//    @Mapping(target = "updated_At", expression = "java(LocalDateTime.now())")
//    @Mapping(target = "failureReason", ignore = true)
    PaymentDto toPaymentDto(Payment payment);

    // Optional: Update an existing Payment entity from DTO

    @Mapping(source = "metaData", target = "metaData", qualifiedByName = "mapMetaData")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentFromDto(PaymentDto dto, @MappingTarget Payment entity);



    PaymentResponse toPaymentResponse(PaymentVerificationResponse.PaymentVerificationData payment);

    @Named("mapMetaData")
    @SuppressWarnings("unchecked")
    default Map<String, Object> mapMetaData(Object value){
        if(value instanceof Map){
            return (Map<String, Object>) value;
        }
        return null;
    }

    default PaymentStatus mapStatus(String status){
        if(status == null){
            return null;
        }
        return PaymentStatus.valueOf(status.toUpperCase());
    }

    default Currency mapCurrency(String currency){
        if(currency == null){
            return null;
        }
        return Currency.valueOf(currency.toUpperCase());
    }
}


