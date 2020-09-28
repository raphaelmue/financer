package org.financer.server.application.service;

import org.financer.util.RandomString;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinancerService {

    private RandomString tokenGenerator = new RandomString(64);
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FinancerService.class);

    @Autowired
    private static VerificationService verificationService;

    @Autowired
    private SessionFactory sessionFactory;

//    /**
//     * Updates users personal information
//     *
//     * @param user user to be updated
//     */
//    @Transactional
//    public void updateUser(User user) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Updating users personal information ...");
//
//        session.merge(user.toEntity());
//    }
//
//
//    /**
//     * Updates a category.
//     *
//     * @param category category that should be updated
//     */
//    @Transactional
//    public void updateCategory(Category category) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Updating users categories ...");
//
//        session.update(category.toEntity());
//    }
//
//    /
//    /**
//     * Updates a variable transaction.
//     *
//     * @param variableTransaction transaction that will be updated
//     */
//    @Transactional
//    public void updateTransaction(VariableTransaction variableTransaction) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Adding transaction ...");
//
//        this.adjustTransactionAmount(session, variableTransaction);
//        session.update(variableTransaction.toEntity());
//    }
//
//    /**
//     * Updates a fixed transaction.
//     *
//     * @param newFixedTransaction transaction object
//     * @return Fixed Transaction object
//     */
//    @Transactional
//    public FixedTransaction updateFixedTransaction(FixedTransaction newFixedTransaction) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Updating fixed transaction ...");
//
//        FixedTransactionEntity entity = newFixedTransaction.toEntity();
//        FixedTransactionEntity oldFixedTransaction = session.get(FixedTransactionEntity.class, entity.getId());
//
//        this.adjustTransactionAmount(session, newFixedTransaction);
//        session.merge(entity);
//
//        oldFixedTransaction.getTransactionAmounts().removeAll(entity.getTransactionAmounts());
//        for (FixedTransactionAmountEntity transactionAmountEntity : oldFixedTransaction.getTransactionAmounts()) {
//            entity.getTransactionAmounts().remove(transactionAmountEntity);
//        }
//
//        return new FixedTransaction(entity);
//    }
//
}
