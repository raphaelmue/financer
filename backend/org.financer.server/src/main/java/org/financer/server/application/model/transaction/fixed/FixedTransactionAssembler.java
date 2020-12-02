package org.financer.server.application.model.transaction.fixed;

import org.financer.server.application.model.ModelAssembler;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.shared.domain.model.api.transaction.fixed.FixedTransactionDTO;
import org.financer.shared.path.PathBuilder;
import org.financer.util.collections.Iterables;
import org.financer.util.mapping.ModelMapperUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FixedTransactionAssembler implements ModelAssembler<FixedTransaction, FixedTransactionDTO> {

    private final PagedResourcesAssembler<FixedTransaction> pagedVariableTransactionAssembler =
            new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(),
                    UriComponentsBuilder.fromUriString(PathBuilder.start().variableTransactions().build().getPath()).build());

    @Override
    public FixedTransactionDTO toModel(FixedTransaction entity) {
        return ModelMapperUtils.map(entity, FixedTransactionDTO.class);
    }

    @Override
    public CollectionModel<FixedTransactionDTO> toCollectionModel(Iterable<? extends FixedTransaction> entities) {
        return CollectionModel.of(ModelMapperUtils.mapAll(Iterables.toList(entities), FixedTransactionDTO.class));
    }

    @Override
    public PagedModel<FixedTransactionDTO> toPagedModel(Page<FixedTransaction> page) {
        return pagedVariableTransactionAssembler.toModel(page, this);
    }
}