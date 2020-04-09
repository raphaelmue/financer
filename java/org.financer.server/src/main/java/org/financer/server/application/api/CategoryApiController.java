package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Controller
public class CategoryApiController implements CategoryApi {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CategoryDomainService categoryDomainService;

    @Autowired
    public CategoryApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<CategoryDTO> createCategory(@NotNull @Valid CreateCategoryDTO category) {
        Category categoryEntity = new Category()
                .setUser(authenticationService.getAuthenticatedUser())
                .setCategoryClass(new CategoryClass(category.getCategoryClass()))
                .setParent(new Category().setId(category.getParentId()))
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
