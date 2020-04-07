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
//    /**
//     * Deletes a token of a user.
//     *
//     * @param tokenId id of the token that should be deleted
//     */
//    @Transactional
//    public void deleteToken(int tokenId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Deleting users token ...");
//
//        session.createQuery("delete from TokenEntity where id = :tokenId")
//                .setParameter("tokenId", tokenId).executeUpdate();
//    }
//
//    /**
//     * Updates the settings of a user
//     *
//     * @param user user whose settings should be updated
//     * @return User object
//     */
//    @Transactional
//    public ConnectionResult<User> updateUsersSettings(User user) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Updating users settings ...");
//
//        for (SettingEntity settingEntity : user.getSettings().getProperties().values()) {
//            settingEntity.setUser(user);
//            if (settingEntity.getId() > 0) {
//                session.merge(settingEntity.toEntity());
//            } else {
//                settingEntity.setId((int) session.save(settingEntity.toEntity()));
//            }
//        }
//
//        return new ConnectionResult<>(user);
//    }
//
//    /**
//     * Returns the users categories as BaseCategory
//     *
//     * @param userId id of the user whose BaseCategory should be returned
//     * @return BaseCategory object
//     */
//    @Transactional
//    public BaseCategory getUsersCategories(int userId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Fetching users categories ...");
//        BaseCategory baseCategory;
//
//        User user = new User(session.get(UserEntity.class, userId));
//        List<CategoryEntity> categories = new ArrayList<>(user.getCategories());
//        Collections.sort(categories);
//
//        baseCategory = new BaseCategory();
//
//        for (CategoryEntity databaseCategory : categories) {
//            Category category = new Category(databaseCategory);
//            if (databaseCategory.getParentId() == -1) {
//                CategoryTree categoryTree = new CategoryTreeImpl(category);
//                categoryTree.setParent(baseCategory.getCategoryTreeByCategoryClass(category.getCategoryClass()));
//                baseCategory.getCategoryTreeByCategoryClass(category.getCategoryClass()).getChildren().add(categoryTree);
//            } else {
//                if (!TreeUtil.insertByValue(baseCategory.getCategoryTreeByCategoryClass(category.getCategoryClass()), new CategoryTreeImpl(new Category(databaseCategory)),
//                        (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()))) {
//                    throw new IllegalArgumentException("Category \"" + category.getName() + "\" could not be inserted into CategoryTree");
//                }
//            }
//        }
//        return baseCategory;
//    }
//
//    /**
//     * Adds a new category.
//     *
//     * @param category category
//     * @return Category object
//     */
//    @Transactional
//    public Category addCategory(Category category) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Adding new category ...");
//
//        category.setId((int) session.save(category.toEntity()));
//        return category;
//    }
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
//     * Returns all transactions of a user.
//     *
//     * @param userId       id of the user
//     * @param baseCategory base category
//     * @return BaseCategory with transactions
//     */
//    @Transactional
//    public BaseCategory getTransactions(int userId, BaseCategory baseCategory) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Fetching users transaction ...");
//
//        baseCategory.traverse(treeObject -> {
//            CategoryTree categoryTree = (CategoryTree) treeObject;
//            if (!categoryTree.getValue().getCategoryClass().isFixed()) {
//                List<VariableTransactionEntity> databaseVariableTransactions = session
//                        .createQuery("from VariableTransactionEntity where category.id = :categoryId",
//                                VariableTransactionEntity.class)
//                        .setParameter("categoryId", treeObject.getValue().getId())
//                        .list();
//                for (VariableTransactionEntity databaseVariableTransaction : databaseVariableTransactions) {
//                    if (databaseVariableTransaction.getAttachments() != null) {
//                        databaseVariableTransaction.getAttachments().size();
//                    }
//                    categoryTree.getTransactions().add(new VariableTransaction(databaseVariableTransaction, categoryTree));
//                }
//            }
//        });
//
//        return baseCategory;
//    }
//
//    /**
//     * Adds a new transaction.
//     *
//     * @param variableTransaction transaction that will be inserted
//     * @return variable transaction object
//     */
//    @Transactional
//    public VariableTransaction addTransaction(VariableTransaction variableTransaction) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Adding transaction ...");
//
//        this.adjustTransactionAmount(session, variableTransaction);
//        variableTransaction.setId((int) session.save(variableTransaction.toEntity()));
//
//        return variableTransaction;
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
//     * Deletes a transaction.
//     *
//     * @param variableTransactionId id of the transaction that will be deleted
//     */
//    @Transactional
//    public void deleteTransaction(int variableTransactionId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Deleting transaction ...");
//
//        session.createQuery("delete from VariableTransactionEntity where id = :variableTransactionId")
//                .setParameter("variableTransactionId", variableTransactionId)
//                .executeUpdate();
//    }
//
//    /**
//     * Uploads a transaction attachment to the database
//     *
//     * @param transaction transaction
//     * @param attachment  attachment that will be uploaded
//     * @return ContentAttachment object
//     */
//    @Transactional
//    public Attachment uploadTransactionAttachment(VariableTransaction transaction, ContentAttachment attachment) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Uploading AttachmentWithContent ...");
//        attachment.setTransaction(transaction);
//        attachment.setUploadDate(LocalDate.now());
//
//        attachment.setId((int) session.save(attachment.toEntity()));
//
//        return new Attachment(attachment.getId(),
//                transaction,
//                attachment.getName(),
//                attachment.getUploadDate());
//    }
//
//    /**
//     * Returns an attachment with content.
//     *
//     * @param attachmentId id of the attachment
//     * @return Attachment object or null, if none found
//     */
//    @Transactional
//    public ContentAttachment getAttachment(int attachmentId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Fetching attachment ...");
//
//        ContentAttachment attachment = null;
//        ContentAttachmentEntity attachmentEntity = session.get(ContentAttachmentEntity.class, attachmentId);
//        if (attachmentEntity != null) {
//            attachment = new ContentAttachment(attachmentEntity);
//        }
//
//        return attachment;
//    }
//
//    /**
//     * Deletes a attachment.
//     *
//     * @param attachmentId id of the attachment that will be deleted
//     */
//    @Transactional
//    public void deleteAttachment(int attachmentId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Deleting attachment ...");
//
//        AttachmentEntity attachmentEntity = session.get(AttachmentEntity.class, attachmentId);
//        session.delete(attachmentEntity);
//    }
//
//    /**
//     * Returns a BaseCategory object with all fixed transactions.
//     *
//     * @param baseCategory base category
//     * @return BaseCategory object with fixed transactions
//     */
//    @Transactional
//    public BaseCategory getFixedTransactions(BaseCategory baseCategory) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Fetching fixed transactions ...");
//
//        baseCategory.traverse(treeObject -> {
//            CategoryTree categoryTree = (CategoryTree) treeObject;
//            if (categoryTree.getValue().getCategoryClass().isFixed()) {
//                List<FixedTransactionEntity> databaseFixedTransactions = session
//                        .createQuery("from FixedTransactionEntity where category.id = :categoryId",
//                                FixedTransactionEntity.class)
//                        .setParameter("categoryId", treeObject.getValue().getId())
//                        .list();
//                for (FixedTransactionEntity fixedTransactionEntity : databaseFixedTransactions) {
//                    fixedTransactionEntity.getTransactionAmounts().size();
//                    FixedTransaction fixedTransaction = new FixedTransaction(fixedTransactionEntity, categoryTree);
//                    categoryTree.getTransactions().remove(fixedTransaction);
//                    categoryTree.getTransactions().add(fixedTransaction);
//                }
//            }
//        });
//
//        return baseCategory;
//    }
//
//    /**
//     * Adds a new fixed transaction and stops the old.
//     *
//     * @param fixedTransaction transaction that will be inserted
//     * @return FixedTransaction object
//     */
//    @Transactional
//    public synchronized FixedTransaction addFixedTransactions(FixedTransaction fixedTransaction) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Adding fixed transactions ...");
//
//        FixedTransactionEntity oldFixedTransaction = session.createQuery("from FixedTransactionEntity where category.id = :categoryId " +
//                "and endDate = null ", FixedTransactionEntity.class)
//                .setParameter("categoryId", fixedTransaction.getCategoryTree().getValue().getId())
//                .uniqueResult();
//        if (oldFixedTransaction != null) {
//            oldFixedTransaction.setEndDate(fixedTransaction.getStartDate().minusDays(1));
//            session.update(oldFixedTransaction);
//        }
//        this.adjustTransactionAmount(session, fixedTransaction);
//        fixedTransaction.setId((int) session.save(fixedTransaction.toEntity()));
//
//        return fixedTransaction;
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
//    /**
//     * Deletes a fixed transaction.
//     *
//     * @param fixedTransactionId id of the transaction that will be deleted
//     * @return void
//     */
//    @Transactional
//    public ConnectionResult<Serializable> deleteFixedTransaction(int fixedTransactionId) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Deleting fixed transaction ...");
//
//        session.createQuery("delete from FixedTransactionEntity where id = :fixedTransactionId")
//                .setParameter("fixedTransactionId", fixedTransactionId)
//                .executeUpdate();
//
//        return new ConnectionResult<>(null);
//    }
//
//    private void adjustTransactionAmount(Session session, org.financer.shared.model.transactions.Transaction transaction) {
//        User user = new User(session.get(UserEntity.class, transaction.getCategoryTree().getValue().getUser().getId()));
//        if (user.getSettings().isChangeAmountSignAutomatically()) {
//            transaction.adjustAmountSign();
//        }
//    }
//
//    /**
//     * Adds a new transaction amount.
//     *
//     * @param transactionAmount transaction amount object that will be inserted
//     * @return TransactionAmount object
//     */
//    @Transactional
//    public TransactionAmount addTransactionAmount(TransactionAmount transactionAmount) {
//        Session session = sessionFactory.getCurrentSession();
//        logger.info("Adding new transaction amount ...");
//
//        transactionAmount.setId((int) session.save(transactionAmount.toEntity()));
//
//        return transactionAmount;
//    }
}
