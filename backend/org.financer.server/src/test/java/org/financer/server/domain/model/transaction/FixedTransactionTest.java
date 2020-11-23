package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@Tag("unit")
class FixedTransactionTest {

    private FixedTransaction fixedTransaction;

    @BeforeEach
    public void setup() {
        fixedTransaction = new FixedTransaction()
                .setAmount(new Amount(50.0))
                .setTimeRange(new TimeRange(LocalDate.now().minusMonths(6)))
                .setCategory(new Category()
                        .setName("Test Category")
                        .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES)))
                .setDescription("Test Purpose")
                .setDay(1)
                .setHasVariableAmounts(false);

        for (int i = 0; i < 6; i++) {
            fixedTransaction.addFixedTransactionAmount(new FixedTransactionAmount()
                    .setId(i + 1L)
                    .setFixedTransaction(fixedTransaction)
                    .setAmount(new Amount(Math.pow(2, i)))
                    .setValueDate(new ValueDate(LocalDate.now().minusMonths(i))));
        }
    }

    @Test
    public void testGetAmount() {
        Assertions.assertEquals(50.0, fixedTransaction.getTotalAmount().getAmount());
    }

    @Test
    public void testGetAmountOfMonth() {
        Assertions.assertEquals(50.0,
                fixedTransaction.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());

        // set isVariable true
        fixedTransaction.setHasVariableAmounts(true);
        Assertions.assertEquals(2.0,
                fixedTransaction.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());

        // set end date of transaction
        fixedTransaction.setTimeRange(fixedTransaction.getTimeRange().setEndDate(LocalDate.now().minusMonths(2)));
        Assertions.assertEquals(0.0,
                fixedTransaction.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());
    }

    @Test
    public void testGetAmountWithTimeRange() {
        Assertions.assertEquals(6 * 50.0,
                fixedTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now())).getAmount());

        // set isVariable true
        fixedTransaction.setHasVariableAmounts(true);
        Assertions.assertEquals(Math.pow(2, 6) - 1,
                fixedTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now())).getAmount());
        Assertions.assertEquals(Math.pow(2, 6) - 1,
                fixedTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(1))).getAmount());

        fixedTransaction.setTimeRange(fixedTransaction.getTimeRange().setEndDate(LocalDate.now().minusMonths(1)));
        Assertions.assertEquals((Math.pow(2, 6) - 1) - (Math.pow(2, 1) - 1),
                fixedTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now().minusMonths(1))).getAmount());
        Assertions.assertEquals(0,
                fixedTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(24), LocalDate.now().minusMonths(12))).getAmount());
    }

    @Test
    public void testIsActive() {
        Assertions.assertTrue(fixedTransaction.isActive());

        fixedTransaction.setTimeRange(fixedTransaction.getTimeRange().setEndDate(LocalDate.now().minusMonths(1)));
        Assertions.assertFalse(fixedTransaction.isActive());
    }

    @Test
    public void testAdjustAmountSign() {
        fixedTransaction.adjustAmountSign();
        Assertions.assertTrue(fixedTransaction.getTotalAmount().getAmount() < 0);

        // set isVariable true
        fixedTransaction.setHasVariableAmounts(true);
        fixedTransaction.adjustAmountSign();
        Assertions.assertTrue(fixedTransaction.getTotalAmount().getAmount() < 0);
    }

}