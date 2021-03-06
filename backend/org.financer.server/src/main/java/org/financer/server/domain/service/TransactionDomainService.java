package org.financer.server.domain.service;

import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.*;
import org.financer.server.domain.repository.*;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.SettingPair;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.*;

@Service
public class TransactionDomainService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDomainService.class);

    private final AuthenticationService authenticationService;
    private final CategoryDomainService categoryDomainService;
    private final CategoryRepository categoryRepository;
    private final VariableTransactionRepository variableTransactionRepository;
    private final ProductRepository productRepository;
    private final FixedTransactionRepository fixedTransactionRepository;
    private final FixedTransactionAmountRepository fixedTransactionAmountRepository;
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public TransactionDomainService(AuthenticationService authenticationService, CategoryDomainService categoryDomainService,
                                    CategoryRepository categoryRepository, VariableTransactionRepository variableTransactionRepository,
                                    ProductRepository productRepository, FixedTransactionRepository fixedTransactionRepository,
                                    FixedTransactionAmountRepository fixedTransactionAmountRepository,
                                    AttachmentRepository attachmentRepository) {
        this.authenticationService = authenticationService;
        this.categoryDomainService = categoryDomainService;
        this.categoryRepository = categoryRepository;
        this.variableTransactionRepository = variableTransactionRepository;
        this.productRepository = productRepository;
        this.fixedTransactionRepository = fixedTransactionRepository;
        this.fixedTransactionAmountRepository = fixedTransactionAmountRepository;
        this.attachmentRepository = attachmentRepository;
    }


    /**
     * Returns a list of all fixed transactions that belong to a user.
     *
     * @return list of fixed transactions
     */
    public Page<FixedTransaction> fetchFixedTransactions(Long userId, boolean onlyActive, Long categoryId, Pageable pageable) {
        authenticationService.getAuthenticatedUser().throwIfNotUsersProperty(userId);
        if (onlyActive) {
            if (categoryId == null) {
                return fixedTransactionRepository.findAllActiveByUserId(userId, pageable);
            } else {
                Optional<FixedTransaction> optionalFixedTransaction = fixedTransactionRepository.findActiveByCategoryId(categoryId);
                return optionalFixedTransaction.
                        <Page<FixedTransaction>>map(fixedTransaction -> new PageImpl<>(List.of(fixedTransaction)))
                        .orElseGet(() -> new PageImpl<>(Collections.emptyList()));
            }
        } else {
            if (categoryId == null) {
                return fixedTransactionRepository.findAllByUserId(userId, pageable);
            } else {
                return fixedTransactionRepository.findAllByCategoryId(categoryId, pageable);
            }
        }
    }

    /**
     * Inserts the given transaction into the database.
     *
     * @param variableTransactionEntity transaction to insert
     * @return inserted transaction object
     */
    public VariableTransaction createVariableTransaction(VariableTransaction variableTransactionEntity) {
        logger.info("Creating new variable transaction.");
        Optional<Category> categoryOptional = categoryRepository.findById(variableTransactionEntity.getCategory().getId());
        if (categoryOptional.isPresent()) {
            variableTransactionEntity.setId(0L);
            variableTransactionEntity.setCategory(categoryOptional.get());
            variableTransactionEntity.throwIfInvalidCategoryClass();
            variableTransactionEntity.throwIfNotUsersProperty(authenticationService.getUserId());

            for (Product product : variableTransactionEntity.getProducts()) {
                product.setVariableTransaction(variableTransactionEntity);
            }

            for (Attachment attachment : variableTransactionEntity.getAttachments()) {
                attachment.setTransaction(variableTransactionEntity);
                attachment.setUploadDate(LocalDate.now());
            }

            this.adjustAmountSign(variableTransactionEntity);
            return variableTransactionRepository.save(variableTransactionEntity);
        }
        throw new NotFoundException(Category.class, variableTransactionEntity.getCategory().getId());
    }

    /**
     * Updates the variable transaction with given values.
     *
     * <p> The values are validated, before updating the transaction. If the given parameters are null or equal to the
     * transaction that will be updated, they will be ignored in the updating process. If no changes are applied to the
     * transaction, the transaction is returned.</p>
     *
     * @param transactionId id of the transaction to be updated
     * @param categoryId    updated category id
     * @param valueDate     updated value date
     * @param description   updated description
     * @param vendor        updated vendor
     * @return update variable transaction
     */
    public VariableTransaction updateVariableTransaction(long transactionId, long categoryId, ValueDate valueDate, String description, String vendor) {
        logger.info("Updating transaction with id {}.", transactionId);
        VariableTransaction variableTransaction = getVariableTransactionById(transactionId);
        variableTransaction.throwIfNotUsersProperty(authenticationService.getUserId());

        boolean transactionChanged = changeTransactionCategory(variableTransaction, categoryId)
                | changeVariableTransactionValueDate(variableTransaction, valueDate)
                | changeTransactionDescription(variableTransaction, description)
                | changeTransactionVendor(variableTransaction, vendor);

        if (transactionChanged) {
            this.adjustAmountSign(variableTransaction);

            return variableTransactionRepository.save(variableTransaction);
        }
        return variableTransaction;
    }

    private VariableTransaction getVariableTransactionById(long variableTransactionId) {
        Optional<VariableTransaction> variableTransactionOptional = variableTransactionRepository.findById(variableTransactionId);
        if (variableTransactionOptional.isEmpty()) {
            throw new NotFoundException(VariableTransaction.class, variableTransactionId);
        }
        return variableTransactionOptional.get();
    }

    private boolean changeVariableTransactionValueDate(VariableTransaction transaction, ValueDate valueDate) {
        if (valueDate != null && valueDate != transaction.getValueDate()) {
            transaction.setValueDate(valueDate);
            return true;
        }
        return false;
    }

    private boolean changeTransactionCategory(Transaction transaction, long categoryId) {
        if (categoryId > 0) {
            transaction.setCategory(categoryDomainService.getCategoryById(categoryId));
            transaction.throwIfInvalidCategoryClass();
            return true;
        }
        return false;
    }

    private boolean changeTransactionDescription(Transaction transaction, String description) {
        if (description != null) {
            transaction.setDescription(description);
            return true;
        }
        return false;
    }

    private boolean changeTransactionVendor(Transaction transaction, String vendor) {
        if (vendor != null) {
            transaction.setVendor(vendor);
            return true;
        }
        return false;
    }

    /**
     * Deletes the given transaction and checks, whether the user is allowed to perform this action.
     *
     * @param variableTransactionId id of the transaction to delete
     */
    public void deleteVariableTransaction(long variableTransactionId) {
        logger.info("Deleting variable transaction {}", variableTransactionId);
        VariableTransaction variableTransaction = getVariableTransactionById(variableTransactionId);
        variableTransaction.throwIfNotUsersProperty(authenticationService.getUserId());
        variableTransactionRepository.delete(variableTransaction);
    }

    /**
     * Creates a new product and inserts it into database. If the transaction id does not exist, {@link
     * NotFoundException} is thrown.
     *
     * @param transactionId transaction id to which the product is assigned
     * @param product       product to be inserted
     * @return product object
     */
    public Product createProduct(long transactionId, Product product) {
        logger.info("Creating new product for variable transaction with {}", transactionId);
        VariableTransaction variableTransaction = getVariableTransactionById(transactionId);
        product.setVariableTransaction(variableTransaction);
        product.throwIfNotUsersProperty(authenticationService.getUserId());
        this.adjustAmountSign(product);
        return productRepository.save(product);
    }

    /**
     * Deletes a product by id.
     *
     * @param transactionId id of the transaction
     * @param productId     id of the product to delete
     * @throws NoResultException thrown when the product does not exist or the product is not assigned to the given
     *                           transaction id
     */
    public void deleteProduct(long transactionId, long productId) {
        logger.info("Deleting product with id {} (transaction id {})", productId, transactionId);
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            productOptional.get().throwIfNotUsersProperty(authenticationService.getUserId());
            if (productOptional.get().getVariableTransaction().getId() == transactionId) {
                productOptional.get().getVariableTransaction().getProducts().remove(productOptional.get());
                variableTransactionRepository.save(productOptional.get().getVariableTransaction());
                return;
            }
        }
        throw new NotFoundException(Product.class, productId);
    }

    /**
     * Deletes a product by id.
     *
     * @param transactionId id of the transaction
     * @param productIds    ids of the products to delete
     * @throws NoResultException thrown when the product does not exist or the product is not assigned to the given
     *                           transaction id
     */
    public void deleteProducts(long transactionId, List<Long> productIds) {
        for (long productId : productIds) {
            this.deleteProduct(transactionId, productId);
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
     * @param fixedTransaction fixed transaction to be inserted
     * @return inserted fixed transaction
     */
    public FixedTransaction createFixedTransaction(FixedTransaction fixedTransaction) {
        logger.info("Creating new fixed transaction.");
        Optional<Category> categoryOptional = categoryRepository.findById(fixedTransaction.getCategory().getId());
        if (categoryOptional.isPresent()) {
            fixedTransaction.setCategory(categoryOptional.get());
            fixedTransaction.throwIfInvalidCategoryClass();
            fixedTransaction.throwIfNotUsersProperty(authenticationService.getUserId());

            // delete transaction amounts if transaction is not variable
            if (fixedTransaction.getHasVariableAmounts()) {
                fixedTransaction.setAmount(new Amount());
            } else {
                fixedTransaction.setTransactionAmounts(new HashSet<>());
            }

            Optional<FixedTransaction> activeTransactionOptional = fixedTransactionRepository.findActiveByCategoryId(
                    fixedTransaction.getCategory().getId());

            // cancels the current fixed transaction if exists to the start date of the current transaction.
            if (activeTransactionOptional.isPresent()) {
                activeTransactionOptional.get().cancel(fixedTransaction.getTimeRange().getStartDate().minusDays(1));
                fixedTransactionRepository.save(activeTransactionOptional.get());
            }

            this.adjustAmountSign(fixedTransaction);
            return fixedTransactionRepository.save(fixedTransaction);
        }
        throw new NoResultException(String.format("No category with id %d found!", fixedTransaction.getCategory().getId()));
    }

    /**
     * Updates the fixed transaction with given values.
     *
     * <p> The values are validated, before updating the transaction. If the given parameters are null or equal to the
     * transaction that will be updated, they will be ignored in the updating process. If no changes are applied to the
     * transaction, the transaction is returned.</p>
     *
     * @param fixedTransactionId id of the transaction to be updated
     * @param categoryId         updated category id
     * @param amount             updated amount
     * @param timeRange          updated time range
     * @param product            updated product
     * @param description        updated description
     * @param vendor             updated vendor
     * @param isVariable         updated is variable
     * @param day                update day
     * @param transactionAmounts updated set of transaction amounts
     * @return update fixed transaction
     */
    public FixedTransaction updateFixedTransaction(long fixedTransactionId, long categoryId, Amount amount, TimeRange timeRange,
                                                   String product, String description, String vendor, boolean isVariable, int day,
                                                   Set<FixedTransactionAmount> transactionAmounts) {
        logger.info("Updating fixed transaction {}", fixedTransactionId);
        FixedTransaction fixedTransaction = getFixedTransactionById(fixedTransactionId);
        fixedTransaction.throwIfNotUsersProperty(authenticationService.getUserId());

        boolean fixedTransactionChanged = changeTransactionCategory(fixedTransaction, categoryId)
                | changeFixedTransactionAmount(fixedTransaction, amount)
                | changeFixedTransactionTimeRange(fixedTransaction, timeRange)
                | changeFixedTransactionProduct(fixedTransaction, product)
                | changeTransactionDescription(fixedTransaction, description)
                | changeTransactionVendor(fixedTransaction, vendor)
                | changeFixedTransactionHasVariableAmounts(fixedTransaction, isVariable)
                | changeFixedTransactionDay(fixedTransaction, day)
                | changeFixedTransactionTransactionAmounts(fixedTransaction, transactionAmounts);

        if (fixedTransactionChanged) {
            this.adjustAmountSign(fixedTransaction);
            fixedTransactionRepository.save(fixedTransaction);
        }
        return fixedTransaction;
    }

    private boolean changeFixedTransactionAmount(FixedTransaction fixedTransaction, Amount amount) {
        if (amount != null && amount.getAmount() != 0 && amount != fixedTransaction.getTotalAmount()) {
            fixedTransaction.setAmount(amount);
            return true;
        }
        return false;
    }

    private boolean changeFixedTransactionTimeRange(FixedTransaction fixedTransaction, TimeRange timeRange) {
        if (timeRange != null && timeRange != fixedTransaction.getTimeRange()) {
            fixedTransaction.setTimeRange(timeRange);
            return true;
        }
        return false;
    }

    private boolean changeFixedTransactionProduct(FixedTransaction fixedTransaction, String product) {
        if (product != null && !product.equals(fixedTransaction.getProduct())) {
            fixedTransaction.setProduct(product);
            return true;
        }
        return false;
    }

    private boolean changeFixedTransactionHasVariableAmounts(FixedTransaction fixedTransaction, boolean hasVariableAmounts) {
        if (hasVariableAmounts != fixedTransaction.getHasVariableAmounts()) {
            fixedTransaction.setHasVariableAmounts(hasVariableAmounts);
            return true;
        }
        return false;
    }

    private boolean changeFixedTransactionDay(FixedTransaction fixedTransaction, int day) {
        if (day > 0 && day != fixedTransaction.getDay()) {
            fixedTransaction.setDay(day);
            return true;
        }
        return false;
    }

    private boolean changeFixedTransactionTransactionAmounts(FixedTransaction fixedTransaction, Set<FixedTransactionAmount> transactionAmounts) {
        if (transactionAmounts != null && !transactionAmounts.isEmpty()) {
            fixedTransaction.getTransactionAmounts().addAll(transactionAmounts);
            return true;
        }
        return false;
    }

    /**
     * Deletes a fixed transaction and checks the ownership.
     *
     * @param fixedTransactionId id of the fixed transaction
     */
    public void deleteFixedTransaction(long fixedTransactionId) {
        logger.info("Deleting fixed transaction {}.", fixedTransactionId);
        FixedTransaction fixedTransaction = getFixedTransactionById(fixedTransactionId);
        fixedTransaction.throwIfNotUsersProperty(authenticationService.getUserId());
        fixedTransactionRepository.delete(fixedTransaction);
    }

    /**
     * Returns the fixed transaction by id.
     *
     * @param fixedTransactionId id of the fixed transaction
     * @return fixed transaction object
     * @throws NotFoundException thrown when the given transaction id does not exist
     */
    private FixedTransaction getFixedTransactionById(long fixedTransactionId) {
        Optional<FixedTransaction> fixedTransactionOptional = fixedTransactionRepository.findById(fixedTransactionId);
        if (fixedTransactionOptional.isPresent()) {
            return fixedTransactionOptional.get();
        }
        throw new NotFoundException(FixedTransaction.class, fixedTransactionId);
    }

    /**
     * Creates a new transaction amount for a given transaction.
     *
     * @param fixedTransactionId id of transaction to add transaction amount to
     * @param transactionAmount  transaction amount to insert
     * @return update fixed transaction
     */
    public FixedTransactionAmount createFixedTransactionAmount(long fixedTransactionId, FixedTransactionAmount transactionAmount) {
        logger.info("Create new transaction amount for transaction {}", fixedTransactionId);
        FixedTransaction fixedTransaction = getFixedTransactionById(fixedTransactionId);
        transactionAmount.setFixedTransaction(fixedTransaction);
        this.adjustAmountSign(transactionAmount);
        return fixedTransactionAmountRepository.save(transactionAmount);
    }

    /**
     * Updates a fixed transaction amount with given values.
     *
     * <p> The values are validated, before updating the transaction amount. If the given parameters are null or equal
     * to the transaction amount that will be updated, they will be ignored in the updating process. If no changes are
     * applied to the transaction amount, the transaction amount is returned.</p>
     *
     * @param fixedTransactionId       id of the fixed transaction that owns the transaction amount
     * @param fixedTransactionAmountId id of the fixed transaction amount
     * @param amount                   update amount
     * @param valueDate                updated value date
     * @return update transaction amount
     */
    public FixedTransactionAmount updateFixedTransactionAmount(long fixedTransactionId, long fixedTransactionAmountId,
                                                               Amount amount, ValueDate valueDate) {
        logger.info("Updating fixed transaction amount {} which belongs to transaction {}", fixedTransactionAmountId, fixedTransactionId);
        FixedTransactionAmount fixedTransactionAmount = getFixedTransactionAmountById(fixedTransactionId, fixedTransactionAmountId);
        fixedTransactionAmount.throwIfNotUsersProperty(authenticationService.getUserId());

        boolean fixedTransactionAmountChanged = changeFixedTransactionAmountAmount(fixedTransactionAmount, amount)
                | changeFixedTransactionAmountValueDate(fixedTransactionAmount, valueDate);

        if (fixedTransactionAmountChanged) {
            this.adjustAmountSign(fixedTransactionAmount);
            return fixedTransactionAmountRepository.save(fixedTransactionAmount);
        }
        return fixedTransactionAmount;
    }

    private boolean changeFixedTransactionAmountAmount(FixedTransactionAmount fixedTransactionAmount, Amount amount) {
        if (amount != null && amount.getAmount() != 0) {
            fixedTransactionAmount.setAmount(amount);
            return true;
        }
        return false;
    }

    private boolean changeFixedTransactionAmountValueDate(FixedTransactionAmount fixedTransactionAmount, ValueDate valueDate) {
        if (valueDate != null) {
            fixedTransactionAmount.setValueDate(valueDate);
            return true;
        }
        return false;
    }


    /**
     * Deletes a fixed transaction amount.
     *
     * @param fixedTransactionId       id of transaction to which the fixed transaction amount belongs
     * @param fixedTransactionAmountId id of fixed transaction amount that will be deleted
     * @throws NotFoundException thrown when either the fixed transaction amount id does not exist or the transaction
     *                           amount is not assigned to the given fixed transaction id
     */
    public void deleteFixedTransactionAmount(long fixedTransactionId, long fixedTransactionAmountId) {
        logger.info("Deleting fixed transaction amount {} of transaction {}", fixedTransactionAmountId, fixedTransactionId);
        FixedTransactionAmount fixedTransactionAmount = getFixedTransactionAmountById(fixedTransactionId, fixedTransactionAmountId);
        fixedTransactionAmount.throwIfNotUsersProperty(authenticationService.getUserId());
        FixedTransaction fixedTransaction = fixedTransactionAmount.getFixedTransaction();
        fixedTransaction.removeFixedTransactionAmount(fixedTransactionAmount);
        fixedTransactionRepository.save(fixedTransaction);
    }

    public void deleteFixedTransactionAmounts(long fixedTransactionId, List<Long> fixedTransactionAmountIds) {
        logger.info("Deleting fixed transaction amount {} of transaction {}", fixedTransactionAmountIds, fixedTransactionId);
        FixedTransaction fixedTransaction = getFixedTransactionById(fixedTransactionId);
        for (long fixedTransactionAmountId : fixedTransactionAmountIds) {
            FixedTransactionAmount fixedTransactionAmount = getFixedTransactionAmountById(fixedTransactionId, fixedTransactionAmountId);
            fixedTransactionAmount.throwIfNotUsersProperty(authenticationService.getUserId());
            fixedTransaction.removeFixedTransactionAmount(fixedTransactionAmount);
        }
        fixedTransactionRepository.save(fixedTransaction);
    }

    private FixedTransactionAmount getFixedTransactionAmountById(long fixedTransactionId, long fixedTransactionAmountId) {
        Optional<FixedTransactionAmount> fixedTransactionAmountOptional = fixedTransactionAmountRepository.findById(fixedTransactionAmountId);
        if (fixedTransactionAmountOptional.isPresent() && fixedTransactionAmountOptional.get().getFixedTransaction().getId() == fixedTransactionId) {
            return fixedTransactionAmountOptional.get();
        }
        throw new NotFoundException(FixedTransaction.class, fixedTransactionAmountId);
    }

    /**
     * Creates a new attachment and inserts it into database.
     *
     * @param attachment attachment to insert
     * @return inserted attachment
     */
    public Attachment createAttachment(long transactionId, Attachment attachment) {
        logger.info("Creating new attachment for transaction {}", transactionId);
        attachment.throwIfNotUsersProperty(authenticationService.getUserId());
        attachment.setTransaction(findTransactionById(transactionId));
        return attachmentRepository.save(attachment);
    }

    /**
     * Checks whether there is a transaction with the given id and returns it.
     *
     * @param transactionId id of transaction
     * @return transaction object
     */
    public Transaction findTransactionById(long transactionId) {
        Optional<? extends Transaction> transactionOptional = fixedTransactionRepository.findById(transactionId);
        if (transactionOptional.isEmpty()) {
            transactionOptional = variableTransactionRepository.findById(transactionId);
            if (transactionOptional.isEmpty()) {
                throw new NotFoundException(Transaction.class, transactionId);
            }
        }
        return transactionOptional.get();
    }

    /**
     * Fetches an attachment by id.
     *
     * @param transactionId id of transaction
     * @param attachmentId  id of attachment to fetch
     * @return attachment
     * @throws NoResultException thrown when the attachment does not exist or the attachment is not assigned to the
     *                           given transaction id
     */
    public Attachment getAttachmentById(long transactionId, long attachmentId) {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(attachmentId);
        if (attachmentOptional.isPresent()) {
            attachmentOptional.get().throwIfNotUsersProperty(authenticationService.getUserId());
            if (attachmentOptional.get().getTransaction().getId() == transactionId) {
                return attachmentOptional.get();
            }
        }
        throw new NotFoundException(Attachment.class, attachmentId);
    }

    /**
     * Deletes the given attachment.
     *
     * @param transactionId id of the transaction
     * @param attachmentId  id of the attachment to be deleted
     */
    public void deleteAttachment(long transactionId, long attachmentId) {
        logger.info("Deleting attachment {} of transaction {}.", attachmentId, transactionId);
        Attachment attachment = getAttachmentById(transactionId, attachmentId);
        attachmentRepository.delete(attachment);
    }

    private void adjustAmountSign(AmountProvider amountProvider) {
        Boolean changeSignAutomaticallySetting = authenticationService.getAuthenticatedUser().getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY);
        if (changeSignAutomaticallySetting) {
            amountProvider.adjustAmountSign();
        }
    }
}
