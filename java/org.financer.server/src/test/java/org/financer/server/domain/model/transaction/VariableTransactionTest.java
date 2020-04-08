package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
public class VariableTransactionTest {

    private VariableTransaction variableTransaction;

    @BeforeEach
    public void setup() {
        variableTransaction = new VariableTransaction()
                .setAmount(new Amount(50))
                .setValueDate(new ValueDate(LocalDate.now()))
                .setCategory(new Category()
                        .setName("Test Category")
                        .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES)))
                .setProduct("Test Product")
                .setPurpose("Test Purpose")
                .setVendor("Test Shop");
    }

    @Test
    public void testGetAmount() {
        assertEquals(50.0, variableTransaction.getAmount().getAmount());
    }

    @Test
    public void testGetAmountOfMonth() {
        assertEquals(50.0, variableTransaction.getAmount(new ValueDate(LocalDate.now())).getAmount());
        assertEquals(0.0, variableTransaction.getAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());
    }

    @Test
    public void testGetAmountWithTimeRange() {
        assertEquals(50.0, variableTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1))).getAmount());
        assertEquals(00.0, variableTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(1))).getAmount());
    }

}