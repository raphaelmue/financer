package org.financer.server.domain.service;

import org.financer.server.application.api.error.IllegalCategoryParentStateException;
import org.financer.server.application.api.error.IllegalUpdateCategoryClassException;
import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.domain.model.category.Category;
import org.financer.server.utils.ServiceTest;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CategoryDomainService.class})
public class CategoryDomainServiceTest extends ServiceTest {

    @MockBean
    private UserDomainService userDomainService;

    @MockBean
    private TransactionDomainService transactionDomainService;

    @Autowired
    private CategoryDomainService categoryDomainService;

    private Category category;
    private Category parent;

    @BeforeEach
    public void setUp() {
        category = variableCategory();

        parent = variableCategoryParent();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testCreateCategory() {
        assertThat(categoryDomainService.createCategory(category)).isEqualTo(category);

        category.setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_REVENUE));
        category.setParent(parent);
        assertThatExceptionOfType(IllegalCategoryParentStateException.class).isThrownBy(
                () -> categoryDomainService.createCategory(category));
    }

    @Test
    public void testCreateCategoryParentNotFound() {
        category.setParent(new Category().setId(3L));
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
                categoryDomainService.createCategory(category));
    }

    @Test
    public void testUpdateCategoryNameAndParentSuccessfully() {
        final String name = "Updated name";
        Category categoryToAssert = categoryDomainService.updateCategory(category.getId(), parent.getId(), null, name);

        assertThat(categoryToAssert).isNotNull();
        assertThat(categoryToAssert.getParent()).isNotNull();
        assertThat(categoryToAssert.getName()).isEqualTo(name);
    }

    @Test
    public void testUpdateCategoryNameAndCategoryClassSuccessfully() {
        final String name = "Updated name";
        category.setParent(parent);
        parent.setChildren(new HashSet<>(Collections.singletonList(category)));
        Category categoryToAssert = categoryDomainService.updateCategory(parent.getId(), -1, CategoryClass.Values.VARIABLE_REVENUE, name);

        assertThat(categoryToAssert).isNotNull();
        assertThat(categoryToAssert.getCategoryClass().getCategoryClass()).isEqualTo(CategoryClass.Values.VARIABLE_REVENUE);
        assertThat(categoryToAssert.getName()).isEqualTo(name);
        assertThat(categoryToAssert.getChildren()).first()
                .matches(child -> child.getCategoryClass().getCategoryClass().equals(CategoryClass.Values.VARIABLE_REVENUE));
    }

    @Test
    public void testUpdateCategoryWithoutChanges() {
        Category categoryToAssert = categoryDomainService.updateCategory(category.getId(), -1, null, null);
        assertThat(categoryToAssert).isNotNull().isEqualToComparingFieldByField(category);
    }

    @Test
    public void testUpdateCategoryParentNotFound() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
                categoryDomainService.updateCategory(category.getId(), 3, null, null));
    }

    @Test
    public void testUpdateCategoryParentInvalidCategoryClass() {
        parent.setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES));
        assertThatExceptionOfType(IllegalCategoryParentStateException.class).isThrownBy(() ->
                categoryDomainService.updateCategory(category.getId(), parent.getId(), null, null));
    }

    @Test
    public void testUpdateCategoryIllegalUpdateCategoryClass() {
        assertThatExceptionOfType(IllegalUpdateCategoryClassException.class).isThrownBy(() ->
                categoryDomainService.updateCategory(category.getId(), -1, CategoryClass.Values.FIXED_EXPENSES, null));

        assertThatExceptionOfType(IllegalUpdateCategoryClassException.class).isThrownBy(() ->
                categoryDomainService.updateCategory(category.getId(), parent.getId(), CategoryClass.Values.FIXED_EXPENSES, null));
    }

    @Test
    public void testDeleteCategoryUnauthorizedOperation() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(() ->
                categoryDomainService.deleteCategory(1));
    }
}