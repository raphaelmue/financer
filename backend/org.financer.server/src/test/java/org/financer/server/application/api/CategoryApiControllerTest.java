package org.financer.server.application.api;

import org.financer.server.application.configuration.ModelMapperConfiguration;
import org.financer.server.application.configuration.security.WebSecurityConfiguration;
import org.financer.server.application.model.transaction.variable.VariableTransactionAssembler;
import org.financer.server.application.model.user.UserAssembler;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.category.CreateCategoryDTO;
import org.financer.shared.domain.model.api.category.UpdateCategoryDTO;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.path.PathBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest(classes = {CategoryApiController.class, WebSecurityConfiguration.class, ModelMapperConfiguration.class, VariableTransactionAssembler.class, UserAssembler.class, AdminConfigurationService.class})
public class CategoryApiControllerTest extends ApiTest {

    @Test
    public void testCreateCategory() throws Exception {
        when(categoryDomainService.createCategory(any(Category.class))).thenAnswer(i -> ((Category) i.getArguments()[0]).setId(1L));

        DataTransferObject dto = new CreateCategoryDTO()
                .setName("Test Category")
                .setCategoryClass(CategoryClass.Values.VARIABLE_EXPENSES)
                .setParentId(-1L);
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().categories().build(), dto))
                .andExpect(status().isOk()).andReturn();

        CategoryDTO categoryToAssert = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDTO.class);
        assertThat(categoryToAssert.getId()).isEqualTo(1);
        assertThat(categoryToAssert.getName()).isEqualTo("Test Category");
        assertThat(categoryToAssert.getCategoryClass()).isEqualTo(CategoryClass.Values.VARIABLE_EXPENSES);
    }

    @Test
    public void testUpdateCategory() throws Exception {
        when(categoryDomainService.updateCategory(anyLong(), anyLong(), any(), anyString())).thenAnswer(i -> new Category()
                .setId(1L)
                .setParent(new Category().setId((Long) i.getArguments()[1]))
                .setCategoryClass(new CategoryClass((CategoryClass.Values) i.getArguments()[2]))
                .setName((String) i.getArguments()[3]));

        DataTransferObject dto = new UpdateCategoryDTO()
                .setName("Test Category")
                .setCategoryClass(CategoryClass.Values.VARIABLE_EXPENSES)
                .setParentId(-1);
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().categories().categoryId(1).build(), dto))
                .andExpect(status().isOk()).andReturn();

        CategoryDTO categoryToAssert = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDTO.class);
        assertThat(categoryToAssert.getId()).isEqualTo(1L);
        assertThat(categoryToAssert.getName()).isEqualTo("Test Category");
        assertThat(categoryToAssert.getCategoryClass()).isEqualTo(CategoryClass.Values.VARIABLE_EXPENSES);
    }

    @Test
    public void testDeleteCategory() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().categories().categoryId(1).build()))
                .andExpect(status().isOk());
    }
}