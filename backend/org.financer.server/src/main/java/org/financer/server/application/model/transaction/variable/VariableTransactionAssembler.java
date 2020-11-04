package org.financer.server.application.model.transaction.variable;

import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
import org.financer.util.collections.Iterables;
import org.financer.util.mapping.ModelMapperUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class VariableTransactionAssembler implements RepresentationModelAssembler<VariableTransaction, VariableTransactionDTO> {
    @Override
    public VariableTransactionDTO toModel(VariableTransaction entity) {
        return ModelMapperUtils.map(entity, VariableTransactionDTO.class);
    }

    @Override
    public CollectionModel<VariableTransactionDTO> toCollectionModel(Iterable<? extends VariableTransaction> entities) {
        return new CollectionModel<>(ModelMapperUtils.mapAll(Iterables.toList(entities), VariableTransactionDTO.class));
    }
}
