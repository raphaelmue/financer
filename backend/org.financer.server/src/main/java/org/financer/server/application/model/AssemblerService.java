package org.financer.server.application.model;

import org.financer.server.application.model.transaction.variable.VariableTransactionAssembler;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
import org.financer.shared.path.PathBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AssemblerService implements ModelAssembler<VariableTransaction, VariableTransactionDTO> {

    @Autowired
    private VariableTransactionAssembler variableTransactionAssembler;

    private final PagedResourcesAssembler<VariableTransaction> pagedVariableTransactionAssembler =
            new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(),
                    UriComponentsBuilder.fromUriString(PathBuilder.start().variableTransactions().build().getPath()).build());

    @Override
    public VariableTransactionDTO toModel(VariableTransaction entity) {
        return variableTransactionAssembler.toModel(entity);
    }

    @Override
    public CollectionModel<VariableTransactionDTO> toCollectionModel(Iterable<VariableTransaction> entities) {
        return variableTransactionAssembler.toCollectionModel(entities);
    }

    @Override
    public PagedModel<VariableTransactionDTO> toPagedModel(Page<VariableTransaction> page) {
        return pagedVariableTransactionAssembler.toModel(page, variableTransactionAssembler);
    }
}
