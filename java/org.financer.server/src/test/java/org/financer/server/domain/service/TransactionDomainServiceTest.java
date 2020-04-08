package org.financer.server.domain.service;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.api.error.IllegalTransactionCategoryClassException;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.repository.*;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FinancerServer.class, TransactionDomainService.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionDomainServiceTest {

    @Autowired
    private TransactionDomainService transactionDomainService;

    private Category variableCategory = new Category()
            .setId(1)
            .setUser(UserDomainServiceTest.user)
            .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
            .setName("Variable Category")
            .setParent(null);

    private Category fixedCategory = new Category()
            .setId(2)
            .setUser(UserDomainServiceTest.user)
            .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES))
            .setName("Fixed Category")
            .setParent(null);

    private VariableTransaction variableTransaction = new VariableTransaction()
            .setId(1)
            .setValueDate(new ValueDate())
            .setCategory(variableCategory)
            .setAmount(new Amount(50.0))
            .setProduct("Test Product")
            .setPurpose("Test Purpose")
            .setVendor("Test Vendor");

    private FixedTransaction fixedTransaction = new FixedTransaction()
            .setId(2)
            .setCategory(fixedCategory)
            .setTimeRange(new TimeRange())
            .setVariable(false)
            .setAmount(new Amount(50.0))
            .setDay(1)
            .setProduct("Test Product")
            .setPurpose("Test Purpose")
            .setVendor("Test Vendor");

    @BeforeEach
    public void setUp() {
        when(authenticationService.getUserId()).thenReturn(UserDomainServiceTest.user.getId());

        when(categoryRepository.existsById(any())).thenReturn(false);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.existsById(2L)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(variableCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(fixedCategory));
        when(variableTransactionRepository.findById(any())).thenReturn(Optional.empty());
        when(variableTransactionRepository.findById(1L)).thenReturn(Optional.of(variableTransaction));
        when(variableTransactionRepository.save(any(VariableTransaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(fixedTransactionRepository.findById(any())).thenReturn(Optional.empty());
        when(fixedTransactionRepository.findById(2L)).thenReturn(Optional.of(fixedTransaction));
        when(fixedTransactionRepository.findActiveTransactionByCategory(any(Category.class))).thenReturn(Optional.empty());
        when(fixedTransactionRepository.save(any(FixedTransaction.class))).thenAnswer(i -> i.getArguments()[0]);
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
    public void testCreateFixedTransaction() {
        assertThat(transactionDomainService.createFixedTransaction(fixedTransaction)).isNotNull();
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
    public void deleteFixedTransaction() {
        transactionDomainService.deleteFixedTransaction(fixedTransaction.getId());
    }

    @Test
    public void testDeleteFixedTransactionUnauthorized() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.deleteFixedTransaction(fixedTransaction.getId()));
    }

    private void mockAnotherUserAuthenticated() {
        when(authenticationService.getUserId()).thenReturn(-1L);
    }

    /*
     * Mock Beans
     */

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserDomainService userDomainService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private VerificationTokenRepository verificationTokenRepository;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private CategoryDomainService categoryDomainService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private VariableTransactionRepository variableTransactionRepository;

    @MockBean
    private FixedTransactionRepository fixedTransactionRepository;

    @MockBean
    private AttachmentRepository attachmentRepository;
}