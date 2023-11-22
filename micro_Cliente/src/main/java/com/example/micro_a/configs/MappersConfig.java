package com.example.micro_a.configs;

import com.example.micro_a.dtos.ClienteDTO;
import com.example.micro_a.entities.ClienteEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper and ObjectMapper configuration class.
 */
@Configuration
public class MappersConfig {

    /**
     * The ModelMapper bean by default.
     * @return the ModelMapper by default.
     */

    /**
     * The ModelMapper bean to merge objects.
     * @return the ModelMapper to use in updates.
     */
    @Bean("mergerMapper")
    public ModelMapper mergerMapper() {
        ModelMapper mapper =  new ModelMapper();
        mapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull());
        return mapper;
    }

    /**
     * The ObjectMapper bean.
     * @return the ObjectMapper with JavaTimeModule included.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(stringToLongConverter());
        return modelMapper;
    }
    @Bean
    public Converter<String, Long> stringToLongConverter() {
        return new AbstractConverter<String, Long>() {
            @Override
            protected Long convert(String source) {
                try {
                    return Long.valueOf(source);
                } catch (NumberFormatException e) {
                    return null; // Maneja el caso en el que la cadena no pueda convertirse a Long.
                }
            }
        };
}
}
