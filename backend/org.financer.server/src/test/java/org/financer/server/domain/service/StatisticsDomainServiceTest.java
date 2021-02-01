package org.financer.server.domain.service;

import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.statistics.DataSet;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.FixedTransactionAmount;
import org.financer.server.domain.model.transaction.Product;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.model.user.User;
import org.financer.server.utils.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {StatisticsDomainService.class})
class StatisticsDomainServiceTest extends ServiceTest {

    @MockBean
    private UserDomainService userDomainService;

    @MockBean
    private TransactionDomainService transactionDomainService;

    @MockBean
    private CategoryDomainService categoryDomainService;

    @Autowired
    private StatisticsDomainService statisticsDomainService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = user();

        VariableTransaction variableTransaction = variableTransaction();
        Product product = product().setVariableTransaction(variableTransaction);
        variableTransaction.setProducts(Set.of(product));

        FixedTransaction fixedTransaction = fixedTransaction();
        FixedTransactionAmount fixedTransactionAmount = fixedTransactionAmount().setFixedTransaction(fixedTransaction);
        fixedTransaction.setTransactionAmounts(Set.of(fixedTransactionAmount));

        Category variableCategory = variableCategory().setTransactions(Set.of(variableTransaction));
        Category fixedCategory = fixedCategory().setTransactions(Set.of(fixedTransaction));

        user.setCategories(Set.of(variableCategory, fixedCategory));

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(authenticationService.getUserId()).thenReturn(user.getId());
        doAnswer(i -> {
            if (user.getRoles().stream().filter(role -> role.getName().equals(i.getArgument(0))).findAny().isEmpty()) {
                throw new UnauthorizedOperationException(user.getId());
            }
            return null;
        }).when(authenticationService).throwIfUserHasNotRole(anyString());

        when(userDomainService.getUserById(anyLong())).thenReturn(user);
        when(categoryDomainService.getCategoryById(1L)).thenReturn(variableCategory);
        when(categoryDomainService.getCategoryById(2L)).thenReturn(fixedCategory);
    }

    @Test
    void testGetBalanceHistoryOfUser() {
        String dateString = LocalDate.now().query(YearMonth::from).toString();

        DataSet dataSet = statisticsDomainService.getBalanceHistoryOfUser(user.getId(), 6);
        assertThat(dataSet.getRecords()).hasSize(6);
        assertThat(dataSet.getRecordByIndex(dateString)).isPresent();
        assertThat(dataSet.getRecordByIndex(dateString).get().getData().get("balance")).isEqualTo(150);

        user.setRoles(Set.of(userRole()));
        assertThatExceptionOfType(UnauthorizedOperationException.class)
                .isThrownBy(() -> statisticsDomainService.getBalanceHistoryOfUser(2L, 6));
    }

    @Test
    void testGetCategoryHistoryOfUser() {
        String dateString = LocalDate.now().query(YearMonth::from).toString();

        DataSet dataSet = statisticsDomainService.getCategoriesHistory(user.getId(), List.of(1L, 2L), 6);
        assertThat(dataSet.getRecords()).hasSize(6);
        assertThat(dataSet.getRecordByIndex(dateString)).isPresent();
        assertThat(dataSet.getRecordByIndex(dateString).get().getData().get("Variable Category")).isEqualTo(100);
        assertThat(dataSet.getRecordByIndex(dateString).get().getData().get("Fixed Category")).isEqualTo(50);

        user.setRoles(Set.of(userRole()));
        assertThatExceptionOfType(UnauthorizedOperationException.class)
                .isThrownBy(() -> statisticsDomainService.getBalanceHistoryOfUser(2L, 6));
    }

    @Test
    void testGetCategoryDistributionOfUser() {
        DataSet dataSet = statisticsDomainService.getCategoriesDistribution(user.getId(), "expenses", 6);
        assertThat(dataSet.getRecords()).hasSize(2);
        assertThat(dataSet.getRecordByIndex("Variable Category")).isPresent();
        assertThat(dataSet.getRecordByIndex("Variable Category").get().getData().get("amount")).isEqualTo(100);
        assertThat(dataSet.getRecordByIndex("Fixed Category")).isPresent();
        assertThat(dataSet.getRecordByIndex("Fixed Category").get().getData().get("amount")).isEqualTo(50);

        user.setRoles(Set.of(userRole()));
        assertThatExceptionOfType(UnauthorizedOperationException.class)
                .isThrownBy(() -> statisticsDomainService.getBalanceHistoryOfUser(2L, 6));
    }
}