package org.financer.server.domain.service;

import org.financer.server.domain.model.transaction.FixedTransactionEntity;
import org.financer.server.domain.model.transaction.VariableTransactionEntity;
import org.financer.server.domain.repository.CategoryRepository;
import org.financer.server.domain.repository.FixedTransactionRepository;
import org.financer.server.domain.repository.VariableTransactionRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VariableTransactionRepository variableTransactionRepository;

    @Autowired
    private FixedTransactionRepository fixedTransactionRepository;

    /**
     * Inserts the given transaction into the database.
     *
     * @param userId                    user id
     * @param variableTransactionEntity transaction to insert
     * @return inserted transaction object
     */
    public VariableTransactionEntity createVariableTransaction(long userId, VariableTransactionEntity variableTransactionEntity) {
        logger.info("Creating new variable transaction. ");
        if (variableTransactionEntity.getCategory() != null && categoryRepository.existsById(variableTransactionEntity.getCategory().getId())) {
            variableTransactionEntity.throwIfNotUsersProperty(userId);
            return variableTransactionRepository.save(variableTransactionEntity);
        }
        throw new NoResultException(String.format("No category with id %d found!", variableTransactionEntity.getCategory().getId()));
    }

    /**
     * Deletes the given transaction and checks, whether the user is allowed to perform this action.
     *
     * @param userId                id of the user
     * @param variableTransactionId id of the transaction to delete
     */
    public void deleteVariableTransaction(long userId, long variableTransactionId) {
        Optional<VariableTransactionEntity> variableTransactionOptional = variableTransactionRepository.findById(variableTransactionId);
        if (variableTransactionOptional.isPresent()) {
            variableTransactionOptional.get().throwIfNotUsersProperty(userId);
            variableTransactionRepository.deleteById(variableTransactionId);
        }
    }

    /**
     * Creates a new fixed transaction and inserts it to the database.
     *
     * <p> First the transaction object is validated by checking the category, isVariable state and the users permission. </p>
     *
     * <p> If there is an existing transaction that is active (see {@link FixedTransactionEntity#isActive()}) of the
     * same category, it will be canceled (see {@link FixedTransactionEntity#cancel(LocalDate)}) on the start date of
     * the new transaction. </p>
     *
     * @param userId                 id of the user
     * @param fixedTransactionEntity fixed transaction to be inserted
     * @return inserted fixed transaction
     */
    public FixedTransactionEntity createFixedTransaction(long userId, FixedTransactionEntity fixedTransactionEntity) {
        logger.info("Creating new fixed transaction.");
        if (categoryRepository.existsById(fixedTransactionEntity.getCategory().getId())) {

            fixedTransactionEntity.throwIfNotUsersProperty(userId);

            // delete transaction amounts if transaction is not variable
            if (fixedTransactionEntity.isVariable()) {
                fixedTransactionEntity.setAmount(null);
            } else {
                fixedTransactionEntity.setTransactionAmounts(new HashSet<>());

                // amount must not be empty if transaction is not variable
                if (fixedTransactionEntity.getAmount() == null) {
                    // TODO implemented custom validator
                }
            }

            Optional<FixedTransactionEntity> activeTransactionOptional = fixedTransactionRepository.findActiveTransactionByCategory(
                    fixedTransactionEntity.getCategory());

            // cancels the current fixed transaction if exists to the start date of the current transaction.
            if (activeTransactionOptional.isPresent()) {
                activeTransactionOptional.get().cancel(fixedTransactionEntity.getTimeRange().getStartDate().minusDays(1));
                fixedTransactionRepository.save(activeTransactionOptional.get());
            }

            return fixedTransactionRepository.save(fixedTransactionEntity);
        }
        throw new NoResultException(String.format("No category with id %d found!", fixedTransactionEntity.getCategory().getId()));
    }

    /**
     * Deletes a fixed transaction and checks the ownership.
     *
     * @param userId             id of the user
     * @param fixedTransactionId id of the fixed transaction
     */
    public void deleteFixedTransaction(long userId, long fixedTransactionId) {
        Optional<FixedTransactionEntity> fixedTransactionOptional = fixedTransactionRepository.findById(fixedTransactionId);
        if (fixedTransactionOptional.isPresent()) {
            fixedTransactionOptional.get().throwIfNotUsersProperty(userId);
            fixedTransactionRepository.delete(fixedTransactionOptional.get());
        }
    }
}
