package org.financer.server.application.api;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.security.WebSecurityConfiguration;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.FixedTransactionAmount;
import org.financer.shared.domain.model.api.transaction.fixed.*;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.shared.path.PathBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest(classes = {FinancerServer.class, AdminConfigurationService.class, WebSecurityConfiguration.class, RestExceptionHandler.class, FixedTransactionApiController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class FixedTransactionApiControllerTest extends ApiTest {

    @Test
    public void testCreateFixedTransaction() throws Exception {
        when(transactionDomainService.createFixedTransaction(any(FixedTransaction.class)))
                .thenAnswer(i -> ((FixedTransaction) i.getArguments()[0]).setId(1L));

        CreateFixedTransactionDTO dto = new CreateFixedTransactionDTO()
                .setCategoryId(1)
                .setHasVariableAmounts(true)
                .setAmount(new Amount(50.0))
                .setTimeRange(new TimeRange())
                .setDescription("Test Description")
                .setVendor("Test Vendor")
                .setTransactionAmounts(Set.of(new CreateFixedTransactionAmountDTO().setValueDate(new ValueDate()).setAmount(new Amount(50))));
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().fixedTransactions().build(), dto))
                .andExpect(status().isOk()).andReturn();

        FixedTransactionDTO transaction = objectMapper.readValue(result.getResponse().getContentAsString(), FixedTransactionDTO.class);
        assertThat(transaction.getId()).isEqualTo(1);
        assertThat(transaction.getHasVariableAmounts()).isEqualTo(dto.getHasVariableAmounts());
        assertThat(transaction.getAmount()).isEqualTo(dto.getAmount());
        assertThat(transaction.getTimeRange()).isEqualTo(dto.getTimeRange());
        assertThat(transaction.getDescription()).isEqualTo(dto.getDescription());
        assertThat(transaction.getVendor()).isEqualTo(dto.getVendor());

        verify(transactionDomainService, times(1)).createFixedTransaction(any(FixedTransaction.class));
    }

    @Test
    public void testUpdateFixedTransaction() throws Exception {
        when(transactionDomainService.updateFixedTransaction(anyLong(), anyLong(), any(Amount.class), any(TimeRange.class),
                anyString(), anyString(), anyString(), anyBoolean(), anyInt(), anySet())).thenAnswer(i -> new FixedTransaction()
                .setId(i.getArgument(0))
                .setCategory(new Category().setId(i.getArgument(1)))
                .setAmount(i.getArgument(2))
                .setTimeRange(i.getArgument(3))
                .setProduct(i.getArgument(4))
                .setDescription(i.getArgument(5))
                .setVendor(i.getArgument(6))
                .setHasVariableAmounts(i.getArgument(7)));

        UpdateFixedTransactionDTO dto = new UpdateFixedTransactionDTO()
                .setCategoryId(1)
                .setHasVariableAmounts(false)
                .setAmount(new Amount(50.0))
                .setTimeRange(new TimeRange())
                .setProduct("Test Product")
                .setDescription("Test Description")
                .setVendor("Test Vendor")
                .setDay(1);
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().fixedTransactions().fixedTransactionId(1).build(), dto))
                .andExpect(status().isOk()).andReturn();

        FixedTransactionDTO transaction = objectMapper.readValue(result.getResponse().getContentAsString(), FixedTransactionDTO.class);
        assertThat(transaction.getId()).isEqualTo(1L);
        assertThat(transaction.getHasVariableAmounts()).isEqualTo(dto.getHasVariableAmounts());
        assertThat(transaction.getAmount()).isEqualTo(dto.getAmount());
        assertThat(transaction.getTimeRange()).isEqualTo(dto.getTimeRange());
        assertThat(transaction.getProduct()).isEqualTo(dto.getProduct());
        assertThat(transaction.getDescription()).isEqualTo(dto.getDescription());
        assertThat(transaction.getVendor()).isEqualTo(dto.getVendor());

        verify(transactionDomainService, times(1)).updateFixedTransaction(anyLong(), anyLong(),
                any(Amount.class), any(TimeRange.class), anyString(), anyString(), anyString(), anyBoolean(), anyInt(), anySet());
    }

    @Test
    public void testDeleteFixedTransaction() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().fixedTransactions().fixedTransactionId(1).build()))
                .andExpect(status().isOk());

        verify(transactionDomainService, times(1)).deleteFixedTransaction(eq(1L));
    }

    @Test
    public void testCreateTransactionAmount() throws Exception {
        when(transactionDomainService.createFixedTransactionAmount(anyLong(), any(FixedTransactionAmount.class)))
                .thenAnswer(i -> ((FixedTransactionAmount) i.getArguments()[1]).setId(1L));

        CreateFixedTransactionAmountDTO dto = new CreateFixedTransactionAmountDTO()
                .setAmount(new Amount(50.0))
                .setValueDate(new ValueDate());
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().fixedTransactions().fixedTransactionId(1).transactionAmounts().build(), dto))
                .andExpect(status().isOk()).andReturn();

        FixedTransactionAmountDTO product = objectMapper.readValue(result.getResponse().getContentAsString(), FixedTransactionAmountDTO.class);
        assertThat(product.getId()).isEqualTo(1);
        assertThat(product.getAmount()).isEqualTo(dto.getAmount());
        assertThat(product.getValueDate()).isEqualTo(dto.getValueDate());

        verify(transactionDomainService, times(1)).createFixedTransactionAmount(eq(1L), any(FixedTransactionAmount.class));
    }

    @Test
    public void testUpdateTransactionAmount() throws Exception {
        when(transactionDomainService.updateFixedTransactionAmount(anyLong(), anyLong(), any(Amount.class), any(ValueDate.class)))
                .thenAnswer(i -> new FixedTransactionAmount()
                        .setId((Long) i.getArguments()[1])
                        .setAmount((Amount) i.getArguments()[2])
                        .setValueDate((ValueDate) i.getArguments()[3]));

        UpdateFixedTransactionAmountDTO dto = new UpdateFixedTransactionAmountDTO()
                .setAmount(new Amount(50.0))
                .setValueDate(new ValueDate());
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().fixedTransactions().fixedTransactionId(1).transactionAmounts().transactionAmountId(1).build(), dto))
                .andExpect(status().isOk()).andReturn();

        FixedTransactionAmountDTO transaction = objectMapper.readValue(result.getResponse().getContentAsString(), FixedTransactionAmountDTO.class);
        assertThat(transaction.getId()).isEqualTo(1);
        assertThat(transaction.getAmount()).isEqualTo(dto.getAmount());
        assertThat(transaction.getValueDate()).isEqualTo(dto.getValueDate());

        verify(transactionDomainService, times(1)).updateFixedTransactionAmount(eq(1L), eq(1L),
                any(Amount.class), any(ValueDate.class));
    }

    @Test
    public void testDeleteTransactionAmount() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().fixedTransactions().fixedTransactionId(1).transactionAmounts().transactionAmountId(1).build()))
                .andExpect(status().isOk());

        verify(transactionDomainService, times(1)).deleteFixedTransactionAmount(eq(1L), eq(1L));
    }
}