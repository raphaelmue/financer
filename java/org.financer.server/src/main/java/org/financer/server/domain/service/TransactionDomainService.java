package org.financer.server.domain.service;

import org.financer.server.domain.model.transaction.VariableTransactionEntity;
import org.financer.server.domain.repository.VariableTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionDomainService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDomainService.class);

    @Autowired
    private VariableTransactionRepository variableTransactionRepository;

    /**
     * Insers the given transaction into the database.
     *
     * @param variableTransactionEntity transaction to insert
     * @return inserted transaction object
     */
    public VariableTransactionEntity createVariableTransaction(VariableTransactionEntity variableTransactionEntity) {
        return variableTransactionRepository.save(variableTransactionEntity);
    }

    /**
     * Deletes the given transaction and checks, whether the user is allowed to perform this action.
     *
     * @param userId id of the user
     * @param variableTransactionId id of the transaction to delete
     * @return true if operation was successful
     */
    public boolean deleteVariableTransaction(long userId, long variableTransactionId) {
        Optional<VariableTransactionEntity> variableTransactionOptional = variableTransactionRepository.findById(variableTransactionId);
        if (variableTransactionOptional.isPresent() && variableTransactionOptional.get().getCategory().getUser().getId() == userId) {
            variableTransactionRepository.deleteById(variableTransactionId);
            return true;
        }
        return false;
    }


}
