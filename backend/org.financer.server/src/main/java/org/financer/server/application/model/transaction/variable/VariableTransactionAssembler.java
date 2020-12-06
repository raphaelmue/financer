package org.financer.server.application.model.transaction.variable;

import org.financer.server.application.model.ModelAssembler;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
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
public class VariableTransactionAssembler implements ModelAssembler<VariableTransaction, VariableTransactionDTO> {

    private final PagedResourcesAssembler<VariableTransaction> pagedVariableTransactionAssembler =
            new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(),
                    UriComponentsBuilder.fromUriString(PathBuilder.start().variableTransactions().build().getPath()).build());
    @Override
    public VariableTransactionDTO toModel(VariableTransaction entity) {
        return ModelMapperUtils.map(entity, VariableTransactionDTO.class);
    }

    @Override
    public CollectionModel<VariableTransactionDTO> toCollectionModel(Iterable<? extends VariableTransaction> entities) {
        return CollectionModel.of(ModelMapperUtils.mapAll(Iterables.toList(entities), VariableTransactionDTO.class));
    }

    @Override
    public PagedModel<VariableTransactionDTO> toPagedModel(Page<VariableTransaction> page) {
        return pagedVariableTransactionAssembler.toModel(page, this);
    }
}
