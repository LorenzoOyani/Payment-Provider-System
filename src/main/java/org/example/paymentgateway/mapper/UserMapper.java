package org.example.paymentgateway.mapper;


import org.example.paymentgateway.dto.UserRegistration;
import org.example.paymentgateway.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Mapper(componentModel = "spring", imports = {LocalDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)


)
public interface UserMapper {
    User toUserEntity(UserRegistration userRegistration);
}
