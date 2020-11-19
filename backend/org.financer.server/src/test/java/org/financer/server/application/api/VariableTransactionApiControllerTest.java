package org.financer.server.application.api;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.security.WebSecurityConfiguration;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.Product;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.api.transaction.variable.*;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.shared.path.PathBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest(classes = {FinancerServer.class, WebSecurityConfiguration.class, RestExceptionHandler.class, VariableTransactionApiController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class VariableTransactionApiControllerTest extends ApiTest {

    @Test
    public void testCreateTransaction() throws Exception {
        when(transactionDomainService.createVariableTransaction(any(VariableTransaction.class)))
                .thenAnswer(i -> ((VariableTransaction) i.getArguments()[0]).setId(1));

        CreateVariableTransactionDTO dto = new CreateVariableTransactionDTO()
                .setCategoryId(1)
                .setValueDate(new ValueDate(LocalDate.now()))
                .setDescription("Test Description")
                .setVendor("Test Vendor")
                .setProducts(Set.of(new CreateProductDTO().setName("Test Product").setAmount(new Amount(20)).setQuantity(new Quantity(2))));
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().variableTransactions().build(), dto))
                .andExpect(status().isOk()).andReturn();

        VariableTransactionDTO transaction = objectMapper.readValue(result.getResponse().getContentAsString(), VariableTransactionDTO.class);
        assertThat(transaction.getId()).isEqualTo(1);
        assertThat(transaction.getValueDate()).isEqualTo(dto.getValueDate());
        assertThat(transaction.getDescription()).isEqualTo(dto.getDescription());
        assertThat(transaction.getVendor()).isEqualTo(dto.getVendor());

        verify(transactionDomainService, times(1)).createVariableTransaction(any(VariableTransaction.class));
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        when(transactionDomainService.updateVariableTransaction(anyLong(), anyLong(), any(ValueDate.class), anyString(), anyString()))
                .thenAnswer(i -> new VariableTransaction()
                        .setId((Long) i.getArguments()[0])
                        .setCategory(new Category().setId((Long) i.getArguments()[1]))
                        .setValueDate((ValueDate) i.getArguments()[2])
                        .setDescription((String) i.getArguments()[3])
                        .setVendor((String) i.getArguments()[4]));

        UpdateVariableTransactionDTO dto = new UpdateVariableTransactionDTO()
                .setCategoryId(1)
                .setValueDate(new ValueDate(LocalDate.now()))
                .setDescription("Test Description")
                .setVendor("Test Vendor");
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().variableTransactions().variableTransactionId(1).build(), dto))
                .andExpect(status().isOk()).andReturn();

        VariableTransactionDTO transaction = objectMapper.readValue(result.getResponse().getContentAsString(), VariableTransactionDTO.class);
        assertThat(transaction.getId()).isEqualTo(1);
        assertThat(transaction.getValueDate()).isEqualTo(dto.getValueDate());
        assertThat(transaction.getDescription()).isEqualTo(dto.getDescription());
        assertThat(transaction.getVendor()).isEqualTo(dto.getVendor());

        verify(transactionDomainService, times(1)).updateVariableTransaction(anyLong(), anyLong(), any(ValueDate.class), anyString(), anyString());
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().variableTransactions().variableTransactionId(1).build()))
                .andExpect(status().isOk());

        verify(transactionDomainService, times(1)).deleteVariableTransaction(eq(1L));
    }

    @Test
    public void testCreateProduct() throws Exception {
        when(transactionDomainService.createProduct(anyLong(), any(Product.class)))
                .thenAnswer(i -> ((Product) i.getArguments()[1]).setId(1));


        CreateProductDTO dto = new CreateProductDTO()
                .setAmount(new Amount(50.0))
                .setQuantity(new Quantity(2))
                .setName("Test Product");
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().variableTransactions().variableTransactionId(1).products().build(), dto))
                .andExpect(status().isOk()).andReturn();

        ProductDTO product = objectMapper.readValue(result.getResponse().getContentAsString(), ProductDTO.class);
        assertThat(product.getId()).isEqualTo(1);
        assertThat(product.getTotalAmount().getAmount()).isEqualTo(100);
        assertThat(product.getQuantity()).isEqualTo(dto.getQuantity());
        assertThat(product.getName()).isEqualTo(dto.getName());

        verify(transactionDomainService, times(1)).createProduct(anyLong(), any(Product.class));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().variableTransactions().variableTransactionId(1).products().productId(1).build()))
                .andExpect(status().isOk());
        verify(transactionDomainService, times(1)).deleteProduct(eq(1L), eq(1L));
    }
}