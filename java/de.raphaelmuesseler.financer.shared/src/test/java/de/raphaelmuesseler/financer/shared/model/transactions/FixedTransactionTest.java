package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Tag("unit")
public class FixedTransactionTest {

    private FixedTransaction fixedTransaction;

    @BeforeEach
    public void setup() {
        final Set<TransactionAmount> transactionAmounts = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            transactionAmounts.add(new TransactionAmount(0, Math.pow(2, i), LocalDate.now().minusMonths(i)));
        }

        fixedTransaction = new FixedTransaction(0,
                50.0,
                new CategoryTreeImpl(new Category("Test Category", BaseCategory.CategoryClass.FIXED_EXPENSES)),
                LocalDate.now().minusMonths(6),
                null,
                "Test Purpose",
                "Test Product",
                false,
                1,
                transactionAmounts);
    }

    @Test
    public void testGetAmount() {
        Assertions.assertEquals(50.0, fixedTransaction.getAmount());

        // set isVariable true
        fixedTransaction.setIsVariable(true);
        Assertions.assertEquals(1.0, fixedTransaction.getAmount());

        // set end date of transaction
        fixedTransaction.setEndDate(LocalDate.now().minusMonths(1));
        Assertions.assertEquals(0.0, fixedTransaction.getAmount());
    }

    @Test
    public void testGetAmountOfMonth() {
        Assertions.assertEquals(50.0, fixedTransaction.getAmount(LocalDate.now().minusMonths(1)));

        // set isVariable true
        fixedTransaction.setIsVariable(true);
        Assertions.assertEquals(2.0, fixedTransaction.getAmount(LocalDate.now().minusMonths(1)));

        // set end date of transaction
        fixedTransaction.setEndDate(LocalDate.now().minusMonths(2));
        Assertions.assertEquals(0.0, fixedTransaction.getAmount(LocalDate.now().minusMonths(1)));
    }

    @Test
    public void testGetAmountWithTimeRange() {
        Assertions.assertEquals(6 * 50.0, fixedTransaction.getAmount(LocalDate.now().minusMonths(12), LocalDate.now()));

        // set isVariable true
        fixedTransaction.setIsVariable(true);
        Assertions.assertEquals(Math.pow(2, 6) - 1, fixedTransaction.getAmount(LocalDate.now().minusMonths(12), LocalDate.now()));
        Assertions.assertEquals(Math.pow(2, 6) - 1, fixedTransaction.getAmount(LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(1)));

        fixedTransaction.setEndDate(LocalDate.now().minusMonths(1));
        Assertions.assertEquals((Math.pow(2, 6) - 1) - (Math.pow(2, 1) - 1), fixedTransaction.getAmount(LocalDate.now().minusMonths(12), LocalDate.now()));

        Assertions.assertEquals(0, fixedTransaction.getAmount(LocalDate.now().minusMonths(24), LocalDate.now().minusMonths(12)));
    }

    @Test
    public void testIsActive() {
        Assertions.assertTrue(fixedTransaction.isActive());

        fixedTransaction.setEndDate(LocalDate.now().minusMonths(1));
        Assertions.assertFalse(fixedTransaction.isActive());
    }

    @Test
    public void testAdjustAmountSign() {
        fixedTransaction.adjustAmountSign();
        Assertions.assertTrue(fixedTransaction.getAmount() < 0);

        // set isVariable true
        fixedTransaction.setIsVariable(true);
        fixedTransaction.adjustAmountSign();
        Assertions.assertTrue(fixedTransaction.getAmount() < 0);

    }
}
