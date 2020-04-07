package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
}
