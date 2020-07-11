package org.financer.server.application.configuration;

import org.financer.util.mapping.ModelMapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        ModelMapperUtils.setModelMapper(modelMapper);
        return modelMapper;
    }

}
