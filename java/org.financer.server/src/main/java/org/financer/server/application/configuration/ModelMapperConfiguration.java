package org.financer.server.application.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

//        modelMapper.typeMap(CategoryDTO.class, CategoryEntity.class).<CategoryClass.Values>addMapping(
//                CategoryDTO::getCategoryClass,
//                (destination, value) -> destination.setCategoryClass(new CategoryClass(value))
//        );
        return modelMapper;
    }

}
