package org.financer.server.domain.model.transaction;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class ProductTest {

    private Product product;
    private VariableTransaction variableTransaction;

    @BeforeEach
    public void setUp() {
        product = new Product()
                .setAmount(new Amount(50))
                .setQuantity(new Quantity(5))
                .setName("Test Product");

        variableTransaction = new VariableTransaction()
                .setValueDate(new ValueDate(LocalDate.now()))
                .addProduct(product);

        product.setTransaction(variableTransaction);
    }

    @Test
    public void testGetAmount() {
        assertThat(product.getAmount().getAmount()).isEqualTo(50 * 5);
    }

    @Test
    public void testGetAmountOfMonth() {
        assertThat(variableTransaction.getAmount(new ValueDate(LocalDate.now())).getAmount()).isEqualTo(50 * 5);
        assertThat(variableTransaction.getAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount()).isEqualTo(0);
    }

    @Test
    public void testGetAmountWithTimeRange() {
        assertThat(variableTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(1))).getAmount()).isEqualTo(50 * 5);
        assertThat(variableTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(4),
                LocalDate.now().minusMonths(1))).getAmount()).isEqualTo(0);
    }
}