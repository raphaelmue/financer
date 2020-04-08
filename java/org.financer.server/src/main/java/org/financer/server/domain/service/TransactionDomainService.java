package org.financer.server.domain.service;

import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.repository.CategoryRepository;
import org.financer.server.domain.repository.FixedTransactionRepository;
import org.financer.server.domain.repository.VariableTransactionRepository;
import org.financer.shared.domain.model.value.objects.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

@Service
public class TransactionDomainService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDomainService.class);

    private final CategoryRepository categoryRepository;
    private final VariableTransactionRepository variableTransactionRepository;
    private final FixedTransactionRepository fixedTransactionRepository;

    @Autowired
    public TransactionDomainService(CategoryRepository categoryRepository, VariableTransactionRepository variableTransactionRepository, FixedTransactionRepository fixedTransactionRepository) {
        this.categoryRepository = categoryRepository;
        this.variableTransactionRepository = variableTransactionRepository;
        this.fixedTransactionRepository = fixedTransactionRepository;
    }

    /**
     * Inserts the given transaction into the database.
     *
     * @param userId                    user id
     * @param variableTransactionEntity transaction to insert
     * @return inserted transaction object
     */
    public VariableTransaction createVariableTransaction(long userId, VariableTransaction variableTransactionEntity) {
        logger.info("Creating new variable transaction.");
        Optional<Category> categoryOptional = categoryRepository.findById(variableTransactionEntity.getCategory().getId());
        if (categoryOptional.isPresent()) {
            variableTransactionEntity.setCategory(categoryOptional.get());
            variableTransactionEntity.throwIfInvalidCategoryClass();
            variableTransactionEntity.throwIfNotUsersProperty(userId);
            return variableTransactionRepository.save(variableTransactionEntity);
        }
        throw new NotFoundException(Category.class, variableTransactionEntity.getCategory().getId());
    }

    /**
     * Deletes the given transaction and checks, whether the user is allowed to perform this action.
     *
     * @param userId                id of the user
     * @param variableTransactionId id of the transaction to delete
     */
    public void deleteVariableTransaction(long userId, long variableTransactionId) {
        Optional<VariableTransaction> variableTransactionOptional = variableTransactionRepository.findById(variableTransactionId);
        if (variableTransactionOptional.isPresent()) {
            variableTransactionOptional.get().throwIfNotUsersProperty(userId);
            variableTransactionRepository.deleteById(variableTransactionId);
        }
    }

    /**
     * Creates a new fixed transaction and inserts it to the database.
     *
     * <p> First the transaction object is validated by checking the category, isVariable state and the users
     * permission. </p>
     *
     * <p> If there is an existing transaction that is active (see {@link FixedTransaction#isActive()}) of the
     * same category, it will be canceled (see {@link FixedTransaction#cancel(LocalDate)}) on the start date of the new
     * transaction. </p>
     *
     * @param userId           id of the user
     * @param fixedTransaction fixed transaction to be inserted
     * @return inserted fixed transaction
     */
    public FixedTransaction createFixedTransaction(long userId, FixedTransaction fixedTransaction) {
        logger.info("Creating new fixed transaction.");
        Optional<Category> categoryOptional = categoryRepository.findById(fixedTransaction.getCategory().getId());
        if (categoryOptional.isPresent()) {
            fixedTransaction.setCategory(categoryOptional.get());
            fixedTransaction.throwIfInvalidCategoryClass();
            fixedTransaction.throwIfNotUsersProperty(userId);

            // delete transaction amounts if transaction is not variable
            if (fixedTransaction.isVariable()) {
                fixedTransaction.setAmount(new Amount());
            } else {
                fixedTransaction.setTransactionAmounts(new HashSet<>());
            }

            Optional<FixedTransaction> activeTransactionOptional = fixedTransactionRepository.findActiveTransactionByCategory(
                    fixedTransaction.getCategory());

            // cancels the current fixed transaction if exists to the start date of the current transaction.
            if (activeTransactionOptional.isPresent()) {
                activeTransactionOptional.get().cancel(fixedTransaction.getTimeRange().getStartDate().minusDays(1));
                fixedTransactionRepository.save(activeTransactionOptional.get());
            }

            return fixedTransactionRepository.save(fixedTransaction);
        }
        throw new NoResultException(String.format("No category with id %d found!", fixedTransaction.getCategory().getId()));
    }

    /**
     * Deletes a fixed transaction and checks the ownership.
     *
     * @param userId             id of the user
     * @param fixedTransactionId id of the fixed transaction
     */
    public void deleteFixedTransaction(long userId, long fixedTransactionId) {
        Optional<FixedTransaction> fixedTransactionOptional = fixedTransactionRepository.findById(fixedTransactionId);
        if (fixedTransactionOptional.isPresent()) {
            fixedTransactionOptional.get().throwIfNotUsersProperty(userId);
            fixedTransactionRepository.delete(fixedTransactionOptional.get());
        }
    }
}
