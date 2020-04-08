package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepositoryCustom {

    Iterable<Category> findAllByUserId(long userId);

}
