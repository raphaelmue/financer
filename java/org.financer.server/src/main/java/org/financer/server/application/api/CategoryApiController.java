package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.shared.domain.model.api.CategoryDTO;
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
    public CategoryApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<CategoryDTO> createCategory(@NotNull @Valid CategoryDTO category) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> updateCategory(@NotBlank @Min(1) Long categoryId, @NotNull @Valid CategoryDTO category) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(@NotBlank @Min(1) Long categoryId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
