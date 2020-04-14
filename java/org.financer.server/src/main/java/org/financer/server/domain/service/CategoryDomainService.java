package org.financer.server.domain.service;

import org.financer.server.application.api.error.IllegalUpdateCategoryClassException;
import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.repository.CategoryRepository;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryDomainService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDomainService.class);

    private final CategoryRepository categoryRepository;
    private final AuthenticationService authenticationService;

    public CategoryDomainService(CategoryRepository categoryRepository, AuthenticationService authenticationService) {
        this.categoryRepository = categoryRepository;
        this.authenticationService = authenticationService;
    }

    /**
     * Inserts a new category into database.
     *
     * @param category category to be inserted
     * @return inserted category
     */
    public Category createCategory(Category category) {
        logger.info("Creating new category.");
        if (category.getParent() != null && category.getParent().getId() > 0) {
            category.setParent(getCategoryById(category.getParent().getId()));
            category.throwIfParentCategoryClassIsInvalid();
        }
        return categoryRepository.save(category);
    }

    /**
     * Updates the category with given values.
     *
     * <p> The values are validated, before updating the category. If the given parameters are null or equal to the
     * category that will be updated, they will be ignored in the updating process. If no changes are applied to the
     * category, the category is returned.</p>
     *
     * @param categoryId    id of the category to update
     * @param parentId      updated parent id
     * @param categoryClass updated category class
     * @param name          updated category name
     * @return updated category object
     */
    public Category updateCategory(long categoryId, long parentId, CategoryClass.Values categoryClass, String name) {
        logger.info("Updating category with id {}.", categoryId);
        Category category = getCategoryById(categoryId);
        category.isPropertyOfUser(authenticationService.getUserId());

        boolean categoryChanged = changeCategoryParent(category, parentId)
                | changeCategoryClass(category, categoryClass)
                | changeCategoryName(category, name);

        if (categoryChanged) {
            return categoryRepository.save(category);
        }
        return category;
    }

    /**
     * Changes the category parent, if the parent is valid. Returns whether the changes took place or not.
     *
     * @param category category to be updated
     * @param parentId id of parent
     * @return true if category is changed, false otherwise
     */
    private boolean changeCategoryParent(Category category, long parentId) {
        if (parentId > 0 && (category.getParent() == null || parentId != category.getParent().getId())) {
            Category parentCategory = getCategoryById(parentId);
            category.setParent(parentCategory);
            category.throwIfParentCategoryClassIsInvalid();
            return true;
        }
        return false;
    }

    /**
     * Changes the category's class, if the given category class is valid. The category class of each child is
     * recursively changed as well. Only root categories can update the category class. Returns whether the changes took
     * place or not.
     *
     * @param category      category to be updated
     * @param categoryClass category class
     * @return true if category is changed, false otherwise
     */
    private boolean changeCategoryClass(Category category, CategoryClass.Values categoryClass) {
        if (categoryClass != null && !categoryClass.equals(category.getCategoryClass().getCategoryClass())) {
            // check if both category classes are either fixed or variable
            CategoryClass categoryClassObject = new CategoryClass(categoryClass);
            if (category.isFixed() != categoryClassObject.isFixed() || !category.isRoot()) {
                throw new IllegalUpdateCategoryClassException(category);
            }

            changeCategoryClassChildren(category, categoryClassObject);
            return true;
        }
        return false;
    }

    private void changeCategoryClassChildren(Category category, CategoryClass categoryClass) {
        category.setCategoryClass(categoryClass);
        if (!category.isLeaf()) {
            for (Category child : category.getChildren()) {
                changeCategoryClassChildren(child, categoryClass);
            }
        }
    }

    /**
     * Changes the category's name, if the given name is valid. Returns whether the changes took place or not.
     *
     * @param category category to be updated
     * @param name     category name
     * @return true if category is changed, false otherwise
     */
    private boolean changeCategoryName(Category category, String name) {
        if (name != null && !name.equals(category.getName())) {
            category.setName(name);
            return true;
        }
        return false;
    }

    /**
     * Fetches the category by id. Throws {@link NotFoundException} when the category id does not exist.
     *
     * @param categoryId category id
     * @return category object
     */
    Category getCategoryById(long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new NotFoundException(Category.class, categoryId);
        }
        return categoryOptional.get();
    }


    /**
     * Deletes a category and all its children as well as all transactions.
     *
     * @param categoryId id of the category that will be deleted
     */
    public void deleteCategory(long categoryId) {
        logger.info("Deleting category by id {}.", categoryId);
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            categoryOptional.get().throwIfNotUsersProperty(authenticationService.getUserId());
            categoryRepository.delete(categoryOptional.get());
        }
    }

}
