package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
public class VariableTransactionTest {

    private VariableTransaction variableTransaction;
    private Product product;

    @BeforeEach
    public void setup() {
        product = new Product()
                .setQuantity(new Quantity())
                .setAmount(new Amount(50));
        variableTransaction = new VariableTransaction()
                .setValueDate(new ValueDate(LocalDate.now()))
                .setCategory(new Category()
                        .setName("Test Category")
                        .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES)))
                .setDescription("Test Purpose")
                .setVendor("Test Shop")
                .addProduct(product);
        product.setTransaction(variableTransaction);
    }

    @Test
    public void testGetAmount() {
        assertEquals(50.0, variableTransaction.getTotalAmount().getAmount());
    }

    @Test
    public void testGetAmountOfMonth() {
        assertEquals(50.0, variableTransaction.getTotalAmount(new ValueDate(LocalDate.now())).getAmount());
        assertEquals(0.0, variableTransaction.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(1))).getAmount());
    }

    @Test
    public void testGetAmountWithTimeRange() {
        assertEquals(50.0, variableTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1))).getAmount());
        assertEquals(00.0, variableTransaction.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(4), LocalDate.now().minusMonths(1))).getAmount());
    }

}