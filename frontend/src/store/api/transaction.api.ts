import {
    CreateFixedTransactionAmountRequest,
    CreateFixedTransactionRequest,
    CreateProductRequest,
    CreateVariableTransactionRequest,
    DeleteProductsRequest,
    DeleteVariableTransactionRequest,
    FixedTransaction,
    FixedTransactionAmount,
    FixedTransactionApi,
    GetFixedTransactionByIdRequest,
    GetUsersFixedTransactionsRequest,
    GetUsersVariableTransactionsRequest,
    GetVariableTransactionByIdRequest,
    PagedModelVariableTransaction,
    Product,
    UpdateVariableTransactionRequest,
    UserApi,
    VariableTransaction,
    VariableTransactionApi
}                                     from '../../.openapi';
import {bindActionCreators, Dispatch} from 'redux';
import {apiConfiguration}             from './index';
import {ErrorMessage}                 from '../errorMessage';
import {TransactionActionDefinition}  from '../actions/transaction.actions';

const loadVariableTransactions = (data: GetUsersVariableTransactionsRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST,
            payload: data
        });
        const api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersVariableTransactions(data)
            .then((transactions: PagedModelVariableTransaction) => dispatch({
                type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS,
                payload: transactions
            })), TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED, dispatch);
    };
};

const loadVariableTransaction = (data: GetVariableTransactionByIdRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        const api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.getVariableTransactionById(data)
            .then((transaction: VariableTransaction) => {
                dispatch({
                    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_SUCCESS,
                    payload: transaction
                });
                if (callback) {
                    callback(transaction);
                }
            }), TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_FAILED, dispatch);
    };
};

const createVariableTransaction = (data: CreateVariableTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        const api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.createVariableTransaction(data)
            .then((variableTransaction: VariableTransaction) => {
                dispatch({
                    type: TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_SUCCESS,
                    payload: variableTransaction
                });
                if (callback) {
                    callback(variableTransaction);
                }
            }), TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_FAILED, dispatch);
    };
};

const updateVariableTransaction = (data: UpdateVariableTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        const api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.updateVariableTransaction(data)
            .then((variableTransaction: VariableTransaction) => {
                dispatch({
                    type: TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_SUCCESS,
                    payload: variableTransaction
                });
                if (callback) {
                    callback(variableTransaction);
                }
            }), TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_FAILED, dispatch);
    };
};

const deleteVariableTransaction = (data: DeleteVariableTransactionRequest, callback?: () => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        const api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.deleteVariableTransaction(data)
                .then(() => {
                    dispatch({type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_SUCCESS});
                    if (callback) callback();
                }),
            TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_FAILED, dispatch);
    };
};

const createProduct = (data: CreateProductRequest, callback?: (product: Product) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.CREATE_PRODUCT_REQUEST,
            payload: data
        });
        const api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.createProduct(data)
            .then((product: Product) => {
                dispatch({
                    type: TransactionActionDefinition.CREATE_PRODUCT_SUCCESS,
                    payload: product
                });
                if (callback) {
                    callback(product);
                }
            }), TransactionActionDefinition.CREATE_PRODUCT_FAILED, dispatch);
    };
};

const deleteProducts = (data: DeleteProductsRequest, callback?: () => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.DELETE_PRODUCTS_REQUEST,
            payload: data
        });
        const api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.deleteProducts(data)
                .then(() => {
                    dispatch({type: TransactionActionDefinition.DELETE_PRODUCTS_SUCCESS});
                    if (callback) callback();
                }),
            TransactionActionDefinition.DELETE_PRODUCTS_FAILED, dispatch);
    };
};

const loadFixedTransactions = (data: GetUsersFixedTransactionsRequest, callback?: (fixedTransactions: FixedTransaction[]) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_REQUEST,
            payload: data
        });
        const api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersFixedTransactions(data)
            .then((transactions) => {
                dispatch({
                    type: TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_SUCCESS,
                    payload: transactions
                });
                if (callback) {
                    if (transactions.embedded?.fixedTransactionDToes) {
                        callback(transactions.embedded.fixedTransactionDToes);
                    } else {
                        callback([]);
                    }
                }
            }), TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_FAILED, dispatch);
    };
};

const loadFixedTransaction = (data: GetFixedTransactionByIdRequest, callback?: (fixedTransaction: FixedTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_FIXED_TRANSACTION_REQUEST,
            payload: data
        });
        const api = new FixedTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.getFixedTransactionById(data)
            .then((transaction) => {
                dispatch({
                    type: TransactionActionDefinition.LOAD_FIXED_TRANSACTION_SUCCESS,
                    payload: transaction
                });
                if (callback && transaction) {
                    callback(transaction);
                }
            }), TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_FAILED, dispatch);
    };
};

const createFixedTransaction = (data: CreateFixedTransactionRequest, callback?: (fixedTransaction: FixedTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.CREATE_FIXED_TRANSACTION_REQUEST,
            payload: data
        });
        const api = new FixedTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.createFixedTransaction(data)
            .then((fixedTransaction) => {
                dispatch({
                    type: TransactionActionDefinition.CREATE_FIXED_TRANSACTION_SUCCESS,
                    payload: fixedTransaction
                });
                if (callback) callback(fixedTransaction);
            }), TransactionActionDefinition.CREATE_FIXED_TRANSACTION_FAILED, dispatch);
    };
};

const createFixedTransactionAmount = (data: CreateFixedTransactionAmountRequest, callback?: (fixedTransactionAmount: FixedTransactionAmount) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.CREATE_FIXED_TRANSACTION_AMOUNT_REQUEST,
            payload: data
        });
        const api = new FixedTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.createFixedTransactionAmount(data)
            .then((fixedTransactionAmount) => {
                dispatch({
                    type: TransactionActionDefinition.CREATE_FIXED_TRANSACTION_AMOUNT_SUCCESS,
                    payload: fixedTransactionAmount
                });
                if (callback) callback(fixedTransactionAmount);
            }), TransactionActionDefinition.CREATE_FIXED_TRANSACTION_AMOUNT_FAILED, dispatch);
    };
};

export interface TransactionApi {
    dispatchLoadVariableTransactions: (data: GetUsersVariableTransactionsRequest) => void,
    dispatchLoadVariableTransaction: (data: GetVariableTransactionByIdRequest, callback?: (variableTransaction: VariableTransaction) => void) => void,
    dispatchCreateVariableTransaction: (data: CreateVariableTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => void,
    dispatchUpdateVariableTransaction: (data: UpdateVariableTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => void,
    dispatchDeleteVariableTransaction: (data: DeleteVariableTransactionRequest, callback?: () => void) => void,
    dispatchCreateProduct: (data: CreateProductRequest, callback?: (product: Product) => void) => void,
    dispatchDeleteProducts: (data: DeleteProductsRequest, callback?: () => void) => void,
    dispatchLoadFixedTransactions: (data: GetUsersFixedTransactionsRequest, callback?: (fixedTransactions: FixedTransaction[]) => void) => void,
    dispatchLoadFixedTransaction: (data: GetFixedTransactionByIdRequest, callback?: (fixedTransaction: FixedTransaction) => void) => void,
    dispatchCreateFixedTransaction: (data: CreateFixedTransactionRequest, callback?: (fixedTransaction: FixedTransaction) => void) => void,
    dispatchCreateFixedTransactionAmount: (data: CreateFixedTransactionAmountRequest, callback?: (fixedTransactionAmount: FixedTransactionAmount) => void) => void
}

export const transactionDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadVariableTransactions: loadVariableTransactions,
    dispatchLoadVariableTransaction: loadVariableTransaction,
    dispatchCreateVariableTransaction: createVariableTransaction,
    dispatchUpdateVariableTransaction: updateVariableTransaction,
    dispatchDeleteVariableTransaction: deleteVariableTransaction,
    dispatchCreateProduct: createProduct,
    dispatchDeleteProducts: deleteProducts,
    dispatchLoadFixedTransactions: loadFixedTransactions,
    dispatchLoadFixedTransaction: loadFixedTransaction,
    dispatchCreateFixedTransaction: createFixedTransaction,
    dispatchCreateFixedTransactionAmount: createFixedTransactionAmount
}, dispatch);
