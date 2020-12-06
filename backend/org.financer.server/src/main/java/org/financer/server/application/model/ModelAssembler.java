package org.financer.server.application.model;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public interface ModelAssembler<E, D extends RepresentationModel<?>> extends RepresentationModelAssembler<E, D> {

    PagedModel<D> toPagedModel(Page<E> page);

}
