package org.financer.server.application.model;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;

public interface ModelAssembler<E, D> {

    D toModel(E entity);

    CollectionModel<D> toCollectionModel(Iterable<E> entities);

    PagedModel<D> toPagedModel(Page<E> page);

}
