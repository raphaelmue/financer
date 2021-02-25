package org.financer.server.domain.service;

import org.financer.server.application.api.error.IllegalTransactionCategoryClassException;
import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.*;
import org.financer.server.domain.model.user.User;
import org.financer.server.utils.ServiceTest;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TransactionDomainService.class, CategoryDomainService.class})
public class TransactionDomainServiceTest extends ServiceTest {

    @MockBean
    private UserDomainService userDomainService;

    @MockBean
    private StatisticsDomainService statisticsDomainService;

    @Autowired
    private CategoryDomainService categoryDomainService;

    @Autowired
    private TransactionDomainService transactionDomainService;

    private Category variableCategory;
    private Category fixedCategory;
    private VariableTransaction variableTransaction;
    private Attachment attachment;
    private Product product;
    private FixedTransaction fixedTransaction;
    private FixedTransactionAmount fixedTransactionAmount;

    @BeforeEach
    public void setUp() {
        User user = user();
        variableCategory = variableCategory();
        fixedCategory = fixedCategory();
        product = product();
        variableTransaction = variableTransaction();
        attachment = attachment();
        product.setVariableTransaction(variableTransaction);
        fixedTransactionAmount = fixedTransactionAmount();
        fixedTransaction = fixedTransaction();
        fixedTransactionAmount.setFixedTransaction(fixedTransaction);

        when(authenticationService.getUserId()).thenReturn(user.getId());
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        when(categoryRepository.existsById(any())).thenReturn(false);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(2L)).thenReturn(true);
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(variableCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(fixedCategory));
        when(variableTransactionRepository.findById(any())).thenReturn(Optional.empty());
        when(variableTransactionRepository.findById(1L)).thenReturn(Optional.of(variableTransaction));
        when(variableTransactionRepository.save(any(VariableTransaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(i -> i.getArguments()[0]);
        when(attachmentRepository.findById(any())).thenReturn(Optional.empty());
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        when(fixedTransactionRepository.findById(any())).thenReturn(Optional.empty());
        when(fixedTransactionRepository.findById(2L)).thenReturn(Optional.of(fixedTransaction));
        when(fixedTransactionRepository.save(any(FixedTransaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(fixedTransactionRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fixedTransaction)));
        when(fixedTransactionRepository.findAllActiveByUserId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fixedTransaction)));
        when(fixedTransactionRepository.findAllByCategoryId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(fixedTransaction)));
        when(fixedTransactionRepository.findActiveByCategoryId(anyLong())).thenReturn(Optional.empty());
        when(fixedTransactionRepository.findActiveByCategoryId(fixedCategory.getId())).thenReturn(Optional.of(fixedTransaction));
        when(fixedTransactionAmountRepository.findById(any())).thenReturn(Optional.empty());
        when(fixedTransactionAmountRepository.findById(1L)).thenReturn(Optional.of(fixedTransactionAmount));
        when(fixedTransactionAmountRepository.save(any(FixedTransactionAmount.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testFetchFixedTransactions() {
        assertThat(transactionDomainService.fetchFixedTransactions(user().getId(), false, null, Pageable.unpaged())).hasSize(1);
        assertThat(transactionDomainService.fetchFixedTransactions(user().getId(), true, null, Pageable.unpaged())).hasSize(1);
        assertThat(transactionDomainService.fetchFixedTransactions(user().getId(), false, fixedCategory.getId(), Pageable.unpaged())).hasSize(1);
        assertThat(transactionDomainService.fetchFixedTransactions(user().getId(), true, fixedCategory.getId(), Pageable.unpaged())).hasSize(1);
    }

    @Test
    public void testCreateVariableTransaction() {
        VariableTransaction transactionToAssert = transactionDomainService.createVariableTransaction(variableTransaction);
        assertThat(transactionToAssert)
                .isEqualTo(variableTransaction);
        assertThat(variableTransactionRepository.findById(1L)).isPresent().get()
                .isEqualTo(variableTransaction);
    }

    @Test
    public void testCreateVariableTransactionIllegalTransactionCategoryClass() {
        variableTransaction.setCategory(fixedCategory);
        assertThatExceptionOfType(IllegalTransactionCategoryClassException.class).isThrownBy(
                () -> transactionDomainService.createVariableTransaction(variableTransaction));
    }

    @Test
    public void testCreateVariableTransactionUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.createVariableTransaction(variableTransaction));
    }

    @Test
    public void testUpdateVariableTransaction() {
        final String description = "Updated description";
        final String vendor = "Updated vendor";
        fixedCategory.setCategoryClass(variableCategory.getCategoryClass());
        variableTransaction.getProducts().forEach(product1 -> product1.setVariableTransaction(variableTransaction));
        VariableTransaction transactionToAssert = transactionDomainService.updateVariableTransaction(
                variableTransaction.getId(), fixedCategory.getId(), new ValueDate(LocalDate.now().plusMonths(1)), description, vendor);

        assertThat(transactionToAssert).isNotNull();
        assertThat(transactionToAssert.getCategory().getId()).isEqualTo(fixedCategory.getId());
        assertThat(transactionToAssert.getValueDate().getDate()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(transactionToAssert.getDescription()).isEqualTo(description);
        assertThat(transactionToAssert.getVendor()).isEqualTo(vendor);
    }

    @Test
    public void testUpdateVariableTransactionWithoutChanges() {
        VariableTransaction transactionToAssert = transactionDomainService.updateVariableTransaction(
                variableTransaction.getId(), -1, null, null, null);

        assertThat(transactionToAssert).isEqualToComparingFieldByField(variableTransaction);
    }

    @Test
    public void testUpdateVariableTransactionCategoryNotFound() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
                () -> transactionDomainService.updateVariableTransaction(variableTransaction.getId(), 3, null, null, null));
    }

    @Test
    public void testDeleteVariableTransaction() {
        transactionDomainService.deleteVariableTransaction(variableTransaction.getId());
    }

    @Test
    public void testDeleteVariableTransactionUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.deleteVariableTransaction(variableTransaction.getId()));
    }

    @Test
    public void testCreateProduct() {
        Product product = transactionDomainService.createProduct(variableTransaction.getId(), new Product()
                .setId(2L).setAmount(new Amount(50)).setQuantity(new Quantity(5))
                .setName("Test Product"));
        assertThat(product.getVariableTransaction()).isEqualToComparingFieldByField(variableTransaction);
    }

    @Test
    public void testCreateProductUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.createProduct(variableTransaction.getId(), new Product()
                        .setId(2L).setAmount(new Amount(50)).setQuantity(new Quantity(5))));
    }

    @Test
    public void testDeleteProduct() {
        transactionDomainService.deleteProduct(variableTransaction.getId(), product.getId());
        assertThat(variableTransaction.getProducts()).isEmpty();
    }

    @Test
    public void testDeleteProducts() {
        transactionDomainService.deleteProducts(variableTransaction.getId(), List.of(product.getId()));
        assertThat(variableTransaction.getProducts()).isEmpty();
    }

    @Test
    public void testDeleteProductNotFound() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
                () -> transactionDomainService.deleteProduct(variableTransaction.getId(), 2));
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
                () -> transactionDomainService.deleteProduct(2, product.getId()));
    }

    @Test
    public void testCreateProductNotFound() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
                () -> transactionDomainService.createProduct(-1, new Product()
                        .setId(1L).setAmount(new Amount(50)).setQuantity(new Quantity(5))));
    }

    @Test
    public void testCreateFixedTransaction() {
        fixedTransaction.setTimeRange(new TimeRange(LocalDate.now().minusMonths(2)));
        assertThat(transactionDomainService.createFixedTransaction(fixedTransaction)).isNotNull();

        FixedTransaction newFixedTransaction = fixedTransaction().setId(3L);
        assertThat(transactionDomainService.createFixedTransaction(newFixedTransaction).isActive()).isTrue();
        assertThat(fixedTransaction.isActive()).isFalse();
    }

    @Test
    public void testCreateFixedTransactionIllegalTransactionCategoryClass() {
        fixedTransaction.setCategory(variableCategory);
        assertThatExceptionOfType(IllegalTransactionCategoryClassException.class).isThrownBy(
                () -> transactionDomainService.createFixedTransaction(fixedTransaction));
    }

    @Test
    public void testCreateFixedTransactionUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.createFixedTransaction(fixedTransaction));
    }

    @Test
    public void testUpdateFixedTransaction() {
        final Amount amount = new Amount(20);
        final TimeRange timeRange = new TimeRange(LocalDate.now().minusMonths(1), LocalDate.now());
        final String product = "New Product";
        final String description = "New Description";
        final String vendor = "New Vendor";
        final boolean hasVariableAmounts = true;
        final int day = 3;
        fixedTransaction.getTransactionAmounts().forEach(fixedTransactionAmount1 -> fixedTransactionAmount1.setFixedTransaction(fixedTransaction));
        FixedTransaction updatedTransaction = transactionDomainService.updateFixedTransaction(fixedTransaction.getId(),
                fixedCategory.getId(), amount, timeRange, product, description, vendor, hasVariableAmounts, day,
                fixedTransaction.getTransactionAmounts());

        assertThat(updatedTransaction.getAmount()).isEqualTo(amount);
        assertThat(updatedTransaction.getTotalAmount()).isEqualTo(new Amount(-50));
        assertThat(updatedTransaction.getTimeRange()).isEqualTo(timeRange);
        assertThat(updatedTransaction.getProduct()).isEqualTo(product);
        assertThat(updatedTransaction.getDescription()).isEqualTo(description);
        assertThat(updatedTransaction.getVendor()).isEqualTo(vendor);
        assertThat(updatedTransaction.getDay()).isEqualTo(day);
        assertThat(updatedTransaction.getHasVariableAmounts()).isEqualTo(hasVariableAmounts);
    }

    @Test
    public void testUpdateFixedTransactionCategoryNotFound() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(
                () -> transactionDomainService.updateFixedTransaction(fixedTransaction.getId(),
                        5, fixedTransaction.getTotalAmount(), fixedTransaction.getTimeRange(),
                        fixedTransaction.getProduct(), fixedTransaction.getDescription(), fixedTransaction.getVendor(),
                        fixedTransaction.getIsVariable(), fixedTransaction.getDay(), fixedTransaction.getTransactionAmounts()));
    }

    @Test
    public void testUpdateFixedTransactionUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.updateFixedTransaction(fixedTransaction.getId(),
                        fixedCategory.getId(), fixedTransaction.getTotalAmount(), fixedTransaction.getTimeRange(),
                        fixedTransaction.getProduct(), fixedTransaction.getDescription(), fixedTransaction.getVendor(),
                        fixedTransaction.getIsVariable(), fixedTransaction.getDay(), fixedTransaction.getTransactionAmounts()));
    }

    @Test
    public void testUpdateFixedTransactionWithoutChanges() {
        FixedTransaction updatedTransaction = transactionDomainService.updateFixedTransaction(fixedTransaction.getId(),
                fixedCategory.getId(), fixedTransaction.getTotalAmount(), fixedTransaction.getTimeRange(), fixedTransaction.getProduct(),
                fixedTransaction.getDescription(), fixedTransaction.getVendor(), fixedTransaction.getIsVariable(),
                fixedTransaction.getDay(), fixedTransaction.getTransactionAmounts());
        assertThat(updatedTransaction).isEqualToComparingFieldByField(fixedTransaction);
    }

    @Test
    public void testDeleteFixedTransaction() {
        transactionDomainService.deleteFixedTransaction(fixedTransaction.getId());
    }

    @Test
    public void testDeleteFixedTransactionUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.deleteFixedTransaction(fixedTransaction.getId()));
    }

    @Test
    public void testCreateFixedTransactionAmount() {
        FixedTransactionAmount transactionAmount = transactionDomainService.createFixedTransactionAmount(fixedTransaction.getId(),
                new FixedTransactionAmount().setId(1L).setAmount(new Amount(50)).setValueDate(new ValueDate()));
        assertThat(transactionAmount.getFixedTransaction()).isEqualToComparingFieldByField(fixedTransaction);
        assertThat(transactionAmount.getFixedTransaction().getTransactionAmounts()).isNotEmpty();
    }

    @Test
    public void testUpdateFixedTransactionAmount() {
        final ValueDate valueDate = new ValueDate(LocalDate.now().plusMonths(1));
        FixedTransactionAmount transactionAmount = transactionDomainService.updateFixedTransactionAmount(fixedTransaction.getId(),
                fixedTransactionAmount.getId(), new Amount(20), valueDate);
        assertThat(transactionAmount.getFixedTransaction()).isEqualToComparingFieldByField(fixedTransaction);
        assertThat(transactionAmount.getFixedTransaction().getTransactionAmounts()).isNotEmpty().first().isEqualTo(transactionAmount);
        assertThat(transactionAmount.getTotalAmount(valueDate).getAmount()).isEqualTo(-20);
    }

    @Test
    public void testUpdateFixedTransactionAmountWithoutChanges() {
        FixedTransactionAmount transactionAmount = transactionDomainService.updateFixedTransactionAmount(fixedTransaction.getId(),
                fixedTransactionAmount.getId(), fixedTransactionAmount.getTotalAmount(), fixedTransactionAmount.getValueDate());
        assertThat(transactionAmount).isEqualToComparingFieldByField(fixedTransactionAmount);
    }

    @Test
    public void testDeleteFixedTransactionAmount() {
        transactionDomainService.deleteFixedTransactionAmount(fixedTransaction.getId(), fixedTransactionAmount.getId());
        assertThat(fixedTransaction.getTransactionAmounts()).isEmpty();
    }

    @Test
    public void testDeleteFixedTransactionAmounts() {
        transactionDomainService.deleteFixedTransactionAmounts(fixedTransaction.getId(), List.of(fixedTransactionAmount.getId()));
        assertThat(fixedTransaction.getTransactionAmounts()).isEmpty();
    }

    @Test
    public void testDeleteFixedTransactionAmountNotFound() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
                transactionDomainService.deleteFixedTransactionAmount(fixedTransaction.getId(), -1));
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
                transactionDomainService.deleteFixedTransactionAmount(-1, fixedTransactionAmount.getId()));
    }

    @Test
    public void testCreateAttachment() {
        Attachment attachmentToAssert = transactionDomainService.createAttachment(variableTransaction.getId(), attachment);
        assertThat(attachmentToAssert).isNotNull();
    }

    @Test
    public void testCreateAttachmentUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(() ->
                transactionDomainService.createAttachment(variableTransaction.getId(), attachment));
    }

    @Test
    public void testGetAttachmentById() {
        assertThat(transactionDomainService.getAttachmentById(variableTransaction.getId(), attachment.getId()))
                .isEqualToComparingFieldByField(attachment);
    }

    @Test
    public void testGetAttachmentByIdNotFoundException() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
                transactionDomainService.getAttachmentById(variableTransaction.getId(), -1));
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
                transactionDomainService.getAttachmentById(-1, attachment.getId()));
    }

    @Test
    public void testDeleteAttachment() {
        variableTransaction.addAttachment(attachment);
        transactionDomainService.deleteAttachment(variableTransaction.getId(), attachment.getId());
    }
}