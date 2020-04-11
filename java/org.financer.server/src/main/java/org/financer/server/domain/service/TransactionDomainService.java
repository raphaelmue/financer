package org.financer.server.domain.service;

import org.financer.server.application.api.error.NotFoundException;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.*;
import org.financer.server.domain.repository.*;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;
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
     * Inserts the given transaction into the database.
     *
     * @param variableTransactionEntity transaction to insert
     * @return inserted transaction object
     */
    public VariableTransaction createVariableTransaction(VariableTransaction variableTransactionEntity) {
        logger.info("Creating new variable transaction.");
        Optional<Category> categoryOptional = categoryRepository.findById(variableTransactionEntity.getCategory().getId());
        if (categoryOptional.isPresent()) {
            variableTransactionEntity.setCategory(categoryOptional.get());
            variableTransactionEntity.throwIfInvalidCategoryClass();
            variableTransactionEntity.throwIfNotUsersProperty(authenticationService.getUserId());

            for (Product product : variableTransactionEntity.getProducts()) {
                product.setTransaction(variableTransactionEntity);
            }

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
    public VariableTransaction updateVariableTransaction(long transactionId, long categoryId, LocalDate valueDate, String description, String vendor) {
        logger.info("Updating transaction with id {}.", transactionId);
        VariableTransaction variableTransaction = getVariableTransactionById(transactionId);
        variableTransaction.throwIfNotUsersProperty(authenticationService.getUserId());

        boolean transactionChanged = changeTransactionCategory(variableTransaction, categoryId)
                | changeVariableTransactionValueDate(variableTransaction, valueDate)
                | changeTransactionDescription(variableTransaction, description)
                | changeTransactionVendor(variableTransaction, vendor);

        if (transactionChanged) {
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

    private boolean changeVariableTransactionValueDate(VariableTransaction transaction, LocalDate valueDate) {
        if (valueDate != null) {
            transaction.setValueDate(new ValueDate(valueDate));
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
        variableTransaction.throwIfNotUsersProperty(variableTransactionId);
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
        product.setTransaction(variableTransaction);
        product.throwIfNotUsersProperty(authenticationService.getUserId());
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
            if (productOptional.get().getTransaction().getId() == transactionId) {
                productOptional.get().getTransaction().getProducts().remove(productOptional.get());
                variableTransactionRepository.save(productOptional.get().getTransaction());
            }
        }
        throw new NotFoundException(Product.class, productId);
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
            if (fixedTransaction.getIsVariable()) {
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
        logger.info("Creating new attachment");
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
    private Transaction findTransactionById(long transactionId) {
        Optional<? extends Transaction> transactionOptional = fixedTransactionRepository.findById(transactionId);
        if (transactionOptional.isEmpty()) {
            transactionOptional = variableTransactionRepository.findById(transactionId);
            if (transactionOptional.isEmpty()) {
                throw new NotFoundException(Transaction.class, transactionId);
            }
        }
        return transactionOptional.get();
    }

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
        logger.info("Deleting attachment.");
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(attachmentId);
        if (attachmentOptional.isPresent()) {
            attachmentOptional.get().throwIfNotUsersProperty(authenticationService.getUserId());
            if (attachmentOptional.get().getTransaction().getId() != transactionId) {
                throw new NotFoundException(Attachment.class, attachmentId);
            }
            attachmentRepository.delete(attachmentOptional.get());
        }
    }
}
