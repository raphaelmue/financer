package org.financer.server.domain.model.transaction;

import org.financer.server.utils.SpringTest;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class ProductTest extends SpringTest {

    private Product product;
    private VariableTransaction variableTransaction;

    @BeforeEach
    public void setUp() {
        variableTransaction = variableTransaction();
        product = product();
        product.setVariableTransaction(variableTransaction);
    }

    @Test
    public void testGetAmount() {
        assertThat(product.getTotalAmount().getAmount()).isEqualTo(100);
    }

    @Test
    public void testGetTotalAmountOfMonth() {
        assertThat(product.getTotalAmount(new ValueDate(LocalDate.now()))).isEqualTo(new Amount(100));
        assertThat(product.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(1)))).isEqualTo(new Amount());
    }

    @Test
    public void testGetTotalAmountWithTimeRange() {
        assertThat(product.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(1)))).isEqualTo(new Amount(100));
        assertThat(product.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(4),
                LocalDate.now().minusMonths(1)))).isEqualTo(new Amount());
    }

    @Test
    public void testIsFixed() {
        assertThat(product.isFixed()).isEqualTo(product.getVariableTransaction().getCategory().getCategoryClass().isFixed());
        assertThat(product.isFixed()).isFalse();
    }

    @Test
    public void testIsRevenue() {
        assertThat(product.isRevenue()).isEqualTo(product.getVariableTransaction().getCategory().getCategoryClass().isRevenue());
        assertThat(product.isRevenue()).isFalse();
    }

}