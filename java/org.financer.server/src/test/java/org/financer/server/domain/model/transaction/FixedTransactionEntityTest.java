package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.CategoryEntity;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Tag("unit")
class FixedTransactionEntityTest {

    private FixedTransactionEntity fixedTransaction;

    @BeforeEach
    public void setup() {
        fixedTransaction = new FixedTransactionEntity()
                .setAmount(new Amount(50.0))
                .setTimeRange(new TimeRange(LocalDate.now().minusMonths(6)))
                .setCategory(new CategoryEntity()
                        .setName("Test Category")
                        .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES)))
                .setProduct("Test Product")
                .setPurpose("Test Purpose")
                .setDay(1)
                .setVariable(false);

        final Set<FixedTransactionAmountEntity> transactionAmounts = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            transactionAmounts.add(new FixedTransactionAmountEntity()
                    .setFixedTransaction(fixedTransaction)
                    .setAmount(new Amount(Math.pow(2, i)))
                    .setValueDate(new ValueDate(LocalDate.now().minusMonths(i))));
        }

        fixedTransaction.setTransactionAmounts(transactionAmounts);
    }

    @Test
    public void testGetAmount() {
        Assertions.assertEquals(50.0, fixedTransaction.getAmount().getAmount());
    }

    @Test
    public void testGetAmountOfMonth() {
        Assertions.assertEquals(50.0,
                fixedTransaction.getAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());

        // set isVariable true
        fixedTransaction.setVariable(true);
        Assertions.assertEquals(2.0,
                fixedTransaction.getAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());

        // set end date of transaction
        fixedTransaction.setTimeRange(fixedTransaction.getTimeRange().setEndDate(LocalDate.now().minusMonths(2)));
        Assertions.assertEquals(0.0,
                fixedTransaction.getAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());
    }

    @Test
    public void testGetAmountWithTimeRange() {
        Assertions.assertEquals(6 * 50.0,
                fixedTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now())).getAmount());

        // set isVariable true
        fixedTransaction.setVariable(true);
        Assertions.assertEquals(Math.pow(2, 6) - 1,
                fixedTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now())).getAmount());
        Assertions.assertEquals(Math.pow(2, 6) - 1,
                fixedTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now().plusMonths(1))).getAmount());

        fixedTransaction.setTimeRange(fixedTransaction.getTimeRange().setEndDate(LocalDate.now().minusMonths(1)));
        Assertions.assertEquals((Math.pow(2, 6) - 1) - (Math.pow(2, 1) - 1),
                fixedTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(12), LocalDate.now().minusMonths(1))).getAmount());
        Assertions.assertEquals(0,
                fixedTransaction.getAmount(new TimeRange(LocalDate.now().minusMonths(24), LocalDate.now().minusMonths(12))).getAmount());
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
        Assertions.assertTrue(fixedTransaction.getAmount().getAmount() < 0);

        // set isVariable true
        fixedTransaction.setVariable(true);
        fixedTransaction.adjustAmountSign();
        Assertions.assertTrue(fixedTransaction.getAmount().getAmount() < 0);
    }

}