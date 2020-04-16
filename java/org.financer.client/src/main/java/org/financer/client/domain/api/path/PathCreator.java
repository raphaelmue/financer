package org.financer.client.domain.api.path;

public interface PathCreator {

    interface CompletePath {
        String build();
    }

    interface UserParameterPath extends CompletePath{
        UserPath userId();

        UserPath userId(long userId);
    }

    interface TokenParameterPath {
        CompletePath tokenId();

        CompletePath tokenId(long tokenId);
    }

    interface UserPath {
        TokenParameterPath tokens();

        CompletePath password();

        CompletePath personalInformation();

        CompletePath settings();

        CompletePath categories();

        CompletePath fixedTransactions();

        CompletePath variableTransactions();
    }

    interface CategoryParameterPath extends CompletePath {
        CompletePath categoryId();

        CompletePath categoryId(long categoryId);
    }

    interface FixedTransactionParameterPath extends CompletePath {
        FixedTransactionPath fixedTransactionId();

        FixedTransactionPath fixedTransactionId(long fixedTransactionId);
    }

    interface FixedTransactionPath extends CompletePath {
        TransactionAmountParameterPath transactionAmounts();
    }

    interface TransactionAmountParameterPath extends CompletePath {
        CompletePath transactionAmountId();

        CompletePath transactionAmountId(long transactionAmountId);
    }

    interface VariableTransactionParameterPath extends CompletePath {
        VariableTransactionPath variableTransactionId();

        VariableTransactionPath variableTransactionId(long variableTransactionId);
    }

    interface VariableTransactionPath extends CompletePath {
        ProductParameterPath products();
    }

    interface ProductParameterPath extends CompletePath {
        CompletePath productId();

        CompletePath productId(long productId);
    }

    UserParameterPath users();

    CategoryParameterPath categories();

    FixedTransactionParameterPath fixedTransactions();

    VariableTransactionParameterPath variableTransactions();
}
