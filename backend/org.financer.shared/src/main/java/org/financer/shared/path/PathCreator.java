package org.financer.shared.path;

public interface PathCreator extends AnyPath {

    interface CompletePath extends AnyPath {
        Path build();
    }

    interface UserParameterPath extends CompletePath, AnyPath {
        UserPath userId();

        UserPath userId(long userId);
    }

    interface AdminPath extends AnyPath {
        CompletePath configuration();

        UserParameterPath users();
    }

    interface TokenParameterPath extends AnyPath {
        CompletePath tokenId();

        CompletePath tokenId(long tokenId);
    }

    interface UserPath extends AnyPath {
        TokenParameterPath tokens();

        CompletePath password();

        CompletePath personalInformation();

        CompletePath settings();

        CompletePath categories();

        CompletePath fixedTransactions();

        CompletePath variableTransactions();

        CompletePath verificationToken();
    }

    interface TransactionPath extends CompletePath, AnyPath {
        AttachmentParameterPath attachments();
    }

    interface AttachmentParameterPath extends CompletePath, AnyPath {
        CompletePath attachmentId();

        CompletePath attachmentId(long attachmentId);
    }

    interface CategoryParameterPath extends CompletePath, AnyPath {
        CompletePath categoryId();

        CompletePath categoryId(long categoryId);
    }

    interface FixedTransactionParameterPath extends CompletePath, AnyPath {
        FixedTransactionPath fixedTransactionId();

        FixedTransactionPath fixedTransactionId(long fixedTransactionId);
    }

    interface FixedTransactionPath extends TransactionPath, CompletePath, AnyPath {
        TransactionAmountParameterPath transactionAmounts();
    }

    interface TransactionAmountParameterPath extends CompletePath, AnyPath {
        CompletePath transactionAmountId();

        CompletePath transactionAmountId(long transactionAmountId);
    }

    interface VariableTransactionParameterPath extends CompletePath, AnyPath {
        VariableTransactionPath variableTransactionId();

        VariableTransactionPath variableTransactionId(long variableTransactionId);
    }

    interface VariableTransactionPath extends TransactionPath, CompletePath, AnyPath {
        ProductParameterPath products();
    }

    interface ProductParameterPath extends CompletePath, AnyPath {
        CompletePath productId();

        CompletePath productId(long productId);
    }

    UserParameterPath users();

    AdminPath admin();

    CategoryParameterPath categories();

    FixedTransactionParameterPath fixedTransactions();

    VariableTransactionParameterPath variableTransactions();

    CompletePath apiDocumentation();

    CompletePath apiDocumentationUI();
}
