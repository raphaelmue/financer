package org.financer.server.domain.model.category;

import org.financer.server.SpringTest;
import org.financer.server.application.api.error.IllegalCategoryParentStateException;
import org.financer.server.domain.model.transaction.Product;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
public class CategoryTest extends SpringTest {

    private Category category;
    private Product product;
    private VariableTransaction variableTransaction;

    @BeforeEach
    public void setup() {
        variableTransaction = variableTransaction();
        product = product().setTransaction(variableTransaction);
        variableTransaction.setProducts(Set.of(product));

        category = new Category()
                .setId(1L)
                .setUser(user())
                .setName("Parent Category")
                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setChildren(Set.of(
                        new Category()
                                .setId(2L)
                                .setUser(user())
                                .setName("Child Category 1")
                                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                                .setTransactions(Set.of(variableTransaction)),
                        new Category()
                                .setId(3L)
                                .setUser(user())
                                .setName("Child Category 2")
                                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                ));
    }

    @Test
    public void testIsPropertyOfUser() {
        assertThat(category.isPropertyOfUser(1)).isTrue();
        assertThat(category.isPropertyOfUser(2)).isFalse();
    }

    @Test
    public void testGetTotalAmount() {
        assertThat(category.getTotalAmount()).isEqualTo(new Amount(100));
    }

    @Test
    public void testGetTotalAmountByValueDate() {
        assertThat(category.getTotalAmount(new ValueDate())).isEqualTo(new Amount(100));
        assertThat(category.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(1)))).isEqualTo(new Amount(0));
    }

    @Test
    public void testIsFixed() {
        assertThat(category.isFixed()).isEqualTo(category.getCategoryClass().isFixed());
        assertThat(category.isFixed()).isFalse();
    }

    @Test
    public void testIsRevenue() {
        assertThat(category.isRevenue()).isEqualTo(category.getCategoryClass().isRevenue());
        assertThat(category.isRevenue()).isFalse();
    }

    @Test
    public void testThrowIfParentCategoryClassIsInvalid() {
        for (Category child : category.getChildren()) {
            child.throwIfParentCategoryClassIsInvalid();
        }
        category.setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_REVENUE));
        for (Category child : category.getChildren()) {
            child.setParent(category);
            assertThatExceptionOfType(IllegalCategoryParentStateException.class).isThrownBy(child::throwIfParentCategoryClassIsInvalid);
        }
    }

    @Test
    public void testAdjustAmountSign() {
        category.adjustAmountSign();
        assertThat(category.getTotalAmount()).isEqualTo(new Amount(-100));
    }

}