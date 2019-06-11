package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import org.junit.jupiter.api.Assertions;
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
    void getAmount() {
        Assertions.assertEquals(50.0, variableTransaction.getAmount());
    }

    @Test
    void testGetAmountOfMonth() {
        Assertions.assertEquals(50.0, variableTransaction.getAmount(LocalDate.now()));
        Assertions.assertEquals(0.0, variableTransaction.getAmount(LocalDate.now().minusMonths(1)));
    }

    @Test
    void testGetAmountWithTimeRange() {
        Assertions.assertEquals(50.0, variableTransaction.getAmount(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1)));
        Assertions.assertEquals(00.0, variableTransaction.getAmount(LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(1)));
    }
}