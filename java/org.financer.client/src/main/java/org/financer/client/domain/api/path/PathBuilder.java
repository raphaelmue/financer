package org.financer.client.domain.api.path;

import java.util.Map;

public class PathBuilder implements PathCreator, PathCreator.UserParameterPath, PathCreator.TokenParameterPath,
        PathCreator.VariableTransactionParameterPath, PathCreator.FixedTransactionParameterPath, PathCreator.CategoryParameterPath,
        PathCreator.ProductParameterPath, PathCreator.TransactionAmountParameterPath,
        PathCreator.UserPath, PathCreator.FixedTransactionPath, PathCreator.VariableTransactionPath {

    private static final String USERS_ENDPOINT = "/user";
    private static final String TOKENS_ENDPOINT = "/tokens";
    private static final String CATEGORIES_ENDPOINT = "/categories";
    private static final String FIXED_TRANSACTIONS_ENDPOINT = "/fixedTransactions";
    private static final String TRANSACTION_AMOUNTS_ENDPOINT = "/transactionAmounts";
    private static final String VARIABLE_TRANSACTIONS_ENDPOINT = "/variableTransactions";
    private static final String PRODUCTS_ENDPOINT = "/products";
    private static final String PASSWORD_ENDPOINT = "/password";
    private static final String SETTINGS_ENDPOINT = "/settings";
    private static final String PERSONAL_INFORMATION_ENDPOINT = "/personalInformation";

    private static final String USER_ID_PARAMETER = "userId";
    private static final String TOKEN_ID_PARAMETER = "tokenId";
    private static final String CATEGORY_ID_PARAMETER = "categoryId";
    private static final String TRANSACTION_ID_PARAMETER = "transactionId";
    private static final String TRANSACTION_AMOUNT_ID_PARAMETER = "transactionAmountId";
    private static final String PRODUCT_ID_PARAMETER = "productId";

    private String path;
    private Map<String, String> parameters;

    private PathBuilder() {

    }

    public static PathCreator start() {
        return new PathBuilder();
    }

    private String toPathParameter(String parameter) {
        return "/{" + parameter + "}";
    }

    private void insertParameters() {
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            path = path.replace("{" + entry.getKey() + "}", entry.getValue());
            this.parameters.remove(entry.getKey());
        }
    }

    @Override
    public String build() {
        insertParameters();
        return path;
    }

    @Override
    public UserPath userId() {
        this.path += toPathParameter(USER_ID_PARAMETER);
        return this;
    }

    @Override
    public UserPath userId(long userId) {
        this.parameters.put(USER_ID_PARAMETER, String.valueOf(userId));
        return userId();
    }

    @Override
    public CompletePath tokenId() {
        this.path += toPathParameter(TOKEN_ID_PARAMETER);
        return this;
    }

    @Override
    public CompletePath tokenId(long tokenId) {
        this.parameters.put(TOKEN_ID_PARAMETER, String.valueOf(tokenId));
        return tokenId();
    }

    @Override
    public TokenParameterPath tokens() {
        this.path += TOKENS_ENDPOINT;
        return this;
    }

    @Override
    public CompletePath password() {
        this.path += PASSWORD_ENDPOINT;
        return this;
    }

    @Override
    public CompletePath personalInformation() {
        this.path += PERSONAL_INFORMATION_ENDPOINT;
        return this;
    }

    @Override
    public CompletePath settings() {
        this.path += SETTINGS_ENDPOINT;
        return this;
    }

    @Override
    public CompletePath categoryId() {
        this.path += toPathParameter(CATEGORY_ID_PARAMETER);
        return this;
    }

    @Override
    public CompletePath categoryId(long categoryId) {
        this.parameters.put(CATEGORY_ID_PARAMETER, String.valueOf(categoryId));
        return this.categoryId();
    }

    @Override
    public TransactionAmountParameterPath transactionAmounts() {
        this.path += TRANSACTION_AMOUNTS_ENDPOINT;
        return this;
    }

    @Override
    public CompletePath transactionAmountId() {
        this.path += TRANSACTION_AMOUNT_ID_PARAMETER;
        return this;
    }

    @Override
    public CompletePath transactionAmountId(long transactionAmountId) {
        this.parameters.put(TRANSACTION_AMOUNT_ID_PARAMETER, String.valueOf(transactionAmountId));
        return transactionAmountId();
    }

    @Override
    public VariableTransactionPath variableTransactionId() {
        this.path += toPathParameter(TRANSACTION_ID_PARAMETER);
        return this;
    }

    @Override
    public VariableTransactionPath variableTransactionId(long variableTransactionId) {
        this.parameters.put(TRANSACTION_ID_PARAMETER, String.valueOf(variableTransactionId));
        return this.variableTransactionId();
    }

    @Override
    public ProductParameterPath products() {
        this.path += PRODUCTS_ENDPOINT;
        return this;
    }

    @Override
    public CompletePath productId() {
        this.path += toPathParameter(PRODUCT_ID_PARAMETER);
        return this;
    }

    @Override
    public CompletePath productId(long productId) {
        this.parameters.put(PRODUCT_ID_PARAMETER, String.valueOf(productId));
        return this.productId();
    }

    @Override
    public UserParameterPath users() {
        this.path += USERS_ENDPOINT;
        return this;
    }

    @Override
    public CategoryParameterPath categories() {
        this.path += CATEGORIES_ENDPOINT;
        return this;
    }

    @Override
    public FixedTransactionParameterPath fixedTransactions() {
        this.path += FIXED_TRANSACTIONS_ENDPOINT;
        return this;
    }

    @Override
    public VariableTransactionParameterPath variableTransactions() {
        this.path += VARIABLE_TRANSACTIONS_ENDPOINT;
        return this;
    }

    @Override
    public FixedTransactionPath fixedTransactionId() {
        this.path += toPathParameter(TRANSACTION_ID_PARAMETER);
        return this;
    }

    @Override
    public FixedTransactionPath fixedTransactionId(long fixedTransactionId) {
        this.parameters.put(TRANSACTION_ID_PARAMETER, String.valueOf(fixedTransactionId));
        return this.fixedTransactionId();
    }
}
