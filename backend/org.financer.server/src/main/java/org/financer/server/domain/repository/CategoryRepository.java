package org.financer.server.domain.repository;

import org.financer.server.domain.model.category.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query("select c from Category c where c.parent is null and c.user.id = :userId")
    List<Category> findAllByUserId(@Param("userId") Long userId);

}
