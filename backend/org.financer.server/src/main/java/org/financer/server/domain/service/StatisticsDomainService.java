package org.financer.server.domain.service;

import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.statistics.DataSet;
import org.financer.server.domain.model.statistics.StatisticDataSet;
import org.financer.server.domain.model.statistics.StatisticDataSetImpl;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.repository.VariableTransactionRepository;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsDomainService {

    private final UserDomainService userDomainService;
    private final CategoryDomainService categoryDomainService;
    private final AuthenticationService authenticationService;
    private final VariableTransactionRepository variableTransactionRepository;

    public StatisticsDomainService(UserDomainService userDomainService, CategoryDomainService categoryDomainService,
                                   AuthenticationService authenticationService, VariableTransactionRepository variableTransactionRepository) {
        this.userDomainService = userDomainService;
        this.categoryDomainService = categoryDomainService;
        this.authenticationService = authenticationService;
        this.variableTransactionRepository = variableTransactionRepository;
    }

    /**
     * Calculates the users balance history for a given numbers of months.
     *
     * @param userId         id of the users
     * @param numberOfMonths number of months that will be calculated backwards from today
     * @return balance history
     */
    public DataSet  getBalanceHistoryOfUser(Long userId, int numberOfMonths) {
        User user = userDomainService.getUserById(userId);
        if (authenticationService.getUserId() != userId) {
            authenticationService.throwIfUserHasNotRole("ADMIN");
        }

        DataSet history = new DataSet();

        for (int i = 0; i < numberOfMonths; i++) {
            history.addRecord(getDateStringMinusMonths(i),
                    "balance",
                    user.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(i))).getAmount());
        }
        return history;
    }

    /**
     * Calculates the users balance history only for given categories and number of months.
     *
     * @param userId         id of the users
     * @param categoryIds    list of category ids for which the balance is calculated
     * @param numberOfMonths number of months that will be calculated backwards from today
     * @return balance history
     */
    public DataSet getCategoriesHistory(Long userId, List<Long> categoryIds, int numberOfMonths) {
        if (authenticationService.getUserId() != userId) {
            authenticationService.throwIfUserHasNotRole("ADMIN");
        }

        DataSet history = new DataSet();
        for (int i = 0; i < numberOfMonths; i++) {
            Map<String, Double> dataMap = new HashMap<>();

            for (Long categoryId : categoryIds) {
                Category category = categoryDomainService.getCategoryById(categoryId);
                category.throwIfNotUsersProperty(userId);
                dataMap.put(category.getName(), category.getTotalAmount(new ValueDate(LocalDate.now().minusMonths(i))).getAmount());

                history.addRecord(getDateStringMinusMonths(i), dataMap);
            }

        }
        return history;
    }

    /**
     * Calculates the distribution of category for either all revenue or expenses categories and number of months.
     *
     * @param userId id of the user
     * @param balanceType either "revenue" or "expenses"
     * @param numberOfMonths number of months
     * @return category distribution
     */
    public DataSet getCategoriesDistribution(Long userId, String balanceType, int numberOfMonths) {
        User user = userDomainService.getUserById(userId);
        if (authenticationService.getUserId() != userId) {
            authenticationService.throwIfUserHasNotRole("ADMIN");
        }

        DataSet history = new DataSet();

        for (Category category : user.getCategories()) {
            if ((balanceType.equals("expenses") && category.isExpenses()) ||
                    (balanceType.equals("revenue") && category.isRevenue())) {
                history.addRecord(category.getName(),
                        "amount",
                        category.getTotalAmount(new TimeRange(LocalDate.now().minusMonths(numberOfMonths), LocalDate.now())).getAmount());
            }
        }
        return history;
    }

    public StatisticDataSet<String, String, Double> getVariableBalanceHistoryOfUser(Long userId, int numberOfMonths) {
        StatisticDataSet<String, String, Double> history = new StatisticDataSetImpl<>();
        for (int i = 0; i < numberOfMonths; i++) {
            history.addRecord(getDateStringMinusMonths(i),
                    "variableBalance",
                    variableTransactionRepository.getUsersBalanceByMonth(userId,
                            LocalDate.now().minusMonths(i).withDayOfMonth(1),
                            LocalDate.now().minusMonths(i).withDayOfMonth(LocalDate.now().minusMonths(i).lengthOfMonth())));
        }

        return history;
    }

    private String getDateStringMinusMonths(int minus) {
        LocalDate date = LocalDate.now().minusMonths(minus);
        return date.query(YearMonth::from).toString();
    }
}
