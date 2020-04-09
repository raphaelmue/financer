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
//    /**
//     * Deletes a category and all its children as well as all transactions.
//     *
//     * @param categoryId id of the category that will be deleted
//     * @return void
//     */
//    @Transactional
//    public ConnectionResult<Serializable> deleteCategory(int categoryId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Deleting category ...");
//
//        session.createQuery("delete from FixedTransactionEntity where category.id = :categoryId")
//                .setParameter("categoryId", categoryId).executeUpdate();
//        session.createQuery("delete from VariableTransactionEntity where category.id = :categoryId")
//                .setParameter("categoryId", categoryId).executeUpdate();
//        session.createQuery("delete from CategoryEntity where id = :categoryId")
//                .setParameter("categoryId", categoryId).executeUpdate();
//
//        this.deleteCategoryChildren(categoryId);
//
//        return new ConnectionResult<>(null);
//    }
//
//    /**
//     * Deletes all children of a given category.
//     *
//     * @param categoryId category id to delete children
//     */
//    @Transactional
//    void deleteCategoryChildren(int categoryId) {
//        Session session = sessionFactory.getCurrentSession();
//        List<CategoryEntity> categories;
//
//        categories = session.createQuery("from CategoryEntity where parentId = :parentId", CategoryEntity.class)
//                .setParameter("parentId", categoryId).list();
//        if (!categories.isEmpty()) {
//            for (CategoryEntity databaseCategory : categories) {
//                session.delete(databaseCategory);
//                deleteCategoryChildren(databaseCategory.getId());
//            }
//        }
//    }
//
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
