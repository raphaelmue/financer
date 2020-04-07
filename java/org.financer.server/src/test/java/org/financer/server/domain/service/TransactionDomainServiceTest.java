package org.financer.server.domain.service;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.repository.*;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
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

    private Category category = new Category()
            .setId(1)
            .setUser(UserDomainServiceTest.user)
            .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
            .setName("Test Category")
            .setParent(null);

    private VariableTransaction variableTransaction = new VariableTransaction()
            .setId(1)
            .setValueDate(new ValueDate())
            .setCategory(category)
            .setAmount(new Amount(50.0))
            .setProduct("Test Product")
            .setPurpose("Test Purpose")
            .setVendor("Test Vendor");

    @BeforeEach
    public void setUp() {
        when(categoryRepository.existsById(any())).thenReturn(false);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(variableTransactionRepository.findById(any())).thenReturn(Optional.empty());
        when(variableTransactionRepository.findById(1L)).thenReturn(Optional.of(variableTransaction));
        when(variableTransactionRepository.save(any(VariableTransaction.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void createVariableTransaction() {
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> transactionDomainService.createVariableTransaction(-1, variableTransaction));

        VariableTransaction transactionToAssert = transactionDomainService.createVariableTransaction(UserDomainServiceTest.user.getId(), variableTransaction);
        assertThat(transactionToAssert)
                .isEqualTo(variableTransaction);
        assertThat(variableTransactionRepository.findById(1L)).isPresent().get()
                .isEqualTo(variableTransaction);
    }

    @Test
    public void deleteVariableTransaction() {
    }

    @Test
    public void createFixedTransaction() {
    }

    @Test
    public void deleteFixedTransaction() {
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
}