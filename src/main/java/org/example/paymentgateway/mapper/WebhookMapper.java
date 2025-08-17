package org.example.paymentgateway.mapper;


import org.example.paymentgateway.dto.WebhookEventDto;
import org.example.paymentgateway.entities.WebhookEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Mapper(componentModel = "spring", imports = {LocalDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface WebhookMapper {


    WebhookEvent WebhookEventMapper(WebhookEventDto webhookEventDto);

    WebhookEventDto WebhookEventMapper(WebhookEvent webhookEvent);
}
