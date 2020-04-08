package org.financer.server.domain.service;

import org.financer.server.application.api.error.IllegalCategoryParentStateException;
import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        logger.info("Creating new category.");
        if (category.getParent() != null && category.getParent().getId() > 0) {
            Optional<Category> parentOptional = categoryRepository.findById(category.getParent().getId());
            if (parentOptional.isEmpty()) {
                throw new NotFoundException(Category.class, category.getParent().getId());
            }
            if (!parentOptional.get().getCategoryClass().equals(category.getCategoryClass())) {
                throw new IllegalCategoryParentStateException(category, parentOptional.get());
            }
            category.setParent(parentOptional.get());
        }
        return categoryRepository.save(category);
    }

}
