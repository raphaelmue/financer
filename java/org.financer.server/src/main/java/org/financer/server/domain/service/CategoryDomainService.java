package org.financer.server.domain.service;

import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryDomainService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDomainService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Inserts a new category into database.
     *
     * @param category category to be inserted
     * @return inserted category
     */
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

}
