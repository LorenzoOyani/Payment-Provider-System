package org.example.paymentgateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Converter(autoApply = true)
@Component
public class DataBaseConverters implements AttributeConverter<Map<String, Object>, String> {

    private final Logger log = LoggerFactory.getLogger(DataBaseConverters.class);
    private final ObjectMapper objectMapper;

    @Autowired
    public DataBaseConverters(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to convert attribute to json", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Collections.emptyMap();
        }
        try{
            return objectMapper.readValue(dbData, Map.class);

        }catch (JsonProcessingException e){
            log.error(e.getMessage());
            throw new RuntimeException("Failed to convert data to json");
        }
    }
}
