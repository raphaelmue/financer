package org.financer.server.domain.model.transaction;

import org.financer.server.application.api.error.IllegalTransactionCategoryClassException;
import org.financer.server.domain.model.category.Category;
import org.financer.server.utils.SpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
public class TransactionTest extends SpringTest {

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        transaction = mock(Transaction.class, Mockito.CALLS_REAL_METHODS);
        when(transaction.isCategoryClassValid(any(Category.class))).thenReturn(true);
        transaction.setId(1L)
                .setCategory(variableCategory())
                .setDescription("Test Description")
                .setVendor("Test Vendor");
    }

    @Test
    public void testIsPropertyOfUser() {
        assertThat(transaction.isPropertyOfUser(1)).isTrue();
        assertThat(transaction.isPropertyOfUser(2)).isFalse();
    }

    @Test
    public void testIsFixed() {
        assertThat(transaction.isFixed()).isEqualTo(transaction.getCategory().getCategoryClass().isFixed());
        assertThat(transaction.isFixed()).isFalse();
    }

    @Test
    public void testIsRevenue() {
        assertThat(transaction.isRevenue()).isEqualTo(transaction.getCategory().getCategoryClass().isRevenue());
        assertThat(transaction.isRevenue()).isFalse();
    }

    @Test
    public void testThrowIfInvalidCategoryClass() {
        transaction.throwIfInvalidCategoryClass();
        FixedTransaction fixedTransaction = fixedTransaction()
                .setCategory(variableCategory());
        assertThatExceptionOfType(IllegalTransactionCategoryClassException.class).isThrownBy(fixedTransaction::throwIfInvalidCategoryClass);
    }
}
