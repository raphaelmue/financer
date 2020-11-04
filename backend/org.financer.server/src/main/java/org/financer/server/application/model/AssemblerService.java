package org.financer.server.application.model;

import org.financer.server.application.model.transaction.variable.VariableTransactionAssembler;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class AssemblerService {

    @Autowired
    private VariableTransactionAssembler variableTransactionAssembler;

    private final PagedResourcesAssembler<VariableTransaction> pagedVariableTransactionAssembler =
            new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(),
                    UriComponentsBuilder.fromUriString("http://localhost:3001/api/1.0-SNAPSHOT/").build());

    public VariableTransactionDTO toModel(VariableTransaction entity) {
        return variableTransactionAssembler.toModel(entity);
    }

    public CollectionModel<VariableTransactionDTO> toCollectionModel(List<VariableTransaction> entities) {
        return variableTransactionAssembler.toCollectionModel(entities);
    }

    public PagedModel<VariableTransactionDTO> toPagedModel(Page<VariableTransaction> page) {
        return pagedVariableTransactionAssembler.toModel(page, variableTransactionAssembler);
    }
}
