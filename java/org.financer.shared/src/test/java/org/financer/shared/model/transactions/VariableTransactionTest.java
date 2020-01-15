package org.financer.shared.model.transactions;

import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.categories.Category;
import org.financer.shared.model.categories.CategoryTreeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
@Tag("unit")
public class VariableTransactionTest {

    private VariableTransaction variableTransaction;

    @BeforeEach
    public void setup() {
        variableTransaction = new VariableTransaction(0,
                50.0,
                LocalDate.now(),
                new CategoryTreeImpl(new Category("Test Category", BaseCategory.CategoryClass.FIXED_EXPENSES)),
                "Test Product",
                "Test Purpose",
                "Test Shop");
    }

    @Test
    public void getAmount() {
        assertEquals(50.0, variableTransaction.getAmount());
    }

    @Test
    public void testGetAmountOfMonth() {
        assertEquals(50.0, variableTransaction.getAmount(LocalDate.now()));
        assertEquals(0.0, variableTransaction.getAmount(LocalDate.now().minusMonths(1)));
    }

    @Test
    public void testGetAmountWithTimeRange() {
        assertEquals(50.0, variableTransaction.getAmount(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1)));
        assertEquals(00.0, variableTransaction.getAmount(LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(1)));
    }
}