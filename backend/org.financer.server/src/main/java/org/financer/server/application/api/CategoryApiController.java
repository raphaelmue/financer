package org.financer.server.application.api;

import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.service.CategoryDomainService;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.category.CreateCategoryDTO;
import org.financer.shared.domain.model.api.category.UpdateCategoryDTO;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
public class CategoryApiController implements CategoryApi {

    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    private final CategoryDomainService categoryDomainService;

    @Autowired
    public CategoryApiController(ModelMapper modelMapper, AuthenticationService authenticationService, CategoryDomainService categoryDomainService) {
        this.modelMapper = modelMapper;
        this.authenticationService = authenticationService;
        this.categoryDomainService = categoryDomainService;
    }

    @Override
    public ResponseEntity<CategoryDTO> createCategory(@NotNull @Valid CreateCategoryDTO category) {
        Category categoryEntity = new Category()
                .setUser(authenticationService.getAuthenticatedUser())
                .setCategoryClass(new CategoryClass(category.getCategoryClass()))
                .setParent(category.getParentId() != null && category.getParentId() > 0 ? new Category().setId(category.getParentId()) : null)
                .setName(category.getName());
        categoryEntity = categoryDomainService.createCategory(categoryEntity);
        return new ResponseEntity<>(modelMapper.map(categoryEntity, CategoryDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CategoryDTO> updateCategory(@NotBlank @Min(1) Long categoryId, @NotNull @Valid UpdateCategoryDTO category) {
        Category updatedCategory = categoryDomainService.updateCategory(categoryId,
                category.getParentId(), category.getCategoryClass(), category.getName());
        return new ResponseEntity<>(modelMapper.map(updatedCategory, CategoryDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(@NotBlank @Min(1) Long categoryId) {
        categoryDomainService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
