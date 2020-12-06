package org.financer.server.application.model.user;

import org.financer.server.application.model.ModelAssembler;
import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.financer.shared.path.PathBuilder;
import org.financer.util.collections.Iterables;
import org.financer.util.mapping.ModelMapperUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UserAssembler implements ModelAssembler<User, UserDTO> {

    private final PagedResourcesAssembler<User> pagedUserAssembler =
            new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(),
                    UriComponentsBuilder.fromUriString(PathBuilder.start().users().build().getPath()).build());

    @Override
    public UserDTO toModel(User entity) {
        return ModelMapperUtils.map(entity, UserDTO.class);
    }

    @Override
    public CollectionModel<UserDTO> toCollectionModel(Iterable<? extends User> entities) {
        return CollectionModel.of(ModelMapperUtils.mapAll(Iterables.toList(entities), UserDTO.class));
    }

    @Override
    public PagedModel<UserDTO> toPagedModel(Page<User> page) {
        return pagedUserAssembler.toModel(page, this);
    }
}
