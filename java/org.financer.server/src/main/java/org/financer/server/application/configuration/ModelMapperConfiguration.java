package org.financer.server.application.configuration;

import org.financer.server.domain.model.user.TokenEntity;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.shared.domain.model.api.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        /* modelMapper.typeMap(UserEntity.class, UserDTO.class).addMappings(mapper ->
                mapper.map(src -> {
                    Iterator<TokenEntity> iterator = src.getTokens().iterator();
                    if (iterator.hasNext()) {
                        return iterator.next().getToken();
                    }
                    return null;
                }, UserDTO::setToken));*/
        return modelMapper;
    }

}
