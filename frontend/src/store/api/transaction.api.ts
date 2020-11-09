import {
    CreateProductRequest,
    CreateTransactionRequest, DeleteVariableTransactionRequest,
    GetUsersVariableTransactionsRequest,
    GetVariableTransactionByIdRequest, UpdateVariableTransactionRequest,
    UserApi,
    VariableTransactionApi
}                                                                    from '../../.openapi/apis';
import {Dispatch}                                                    from 'redux';
import {apiConfiguration}                                            from './index';
import {ErrorMessage}                                                from '../errorMessage';
import {PagedModelVariableTransaction, Product, VariableTransaction} from '../../.openapi/models';
import {TransactionActionDefinition}                                 from '../actions/transaction.actions';

export const loadVariableTransactions = (data: GetUsersVariableTransactionsRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST,
            payload: data
        });
        let api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersVariableTransactions(data)
            .then((transactions: PagedModelVariableTransaction) => dispatch({
                type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS,
                payload: transactions
            })), TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED, dispatch);
    };
};

export const loadVariableTransaction = (data: GetVariableTransactionByIdRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        let api = new VariableTransactionApi(apiConfiguration());
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

export const createVariableTransaction = (data: CreateTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        let api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.createTransaction(data)
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

export const createProduct = (data: CreateProductRequest, callback?: (product: Product) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.CREATE_PRODUCT_REQUEST,
            payload: data
        });
        let api = new VariableTransactionApi(apiConfiguration());
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

export const updateVariableTransaction = (data: UpdateVariableTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        let api = new VariableTransactionApi(apiConfiguration());
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

export const deleteVariableTransaction = (data: DeleteVariableTransactionRequest, callback?: () => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_REQUEST,
            payload: data
        });
        let api = new VariableTransactionApi(apiConfiguration());
        ErrorMessage.resolveError(api.deleteVariableTransaction(data)
                .then(() => {
                    dispatch({type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_SUCCESS});
                    if (callback) callback();
                }),
            TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_FAILED, dispatch);
    };
};

export interface TransactionApi {
    dispatchLoadVariableTransactions: (data: GetUsersVariableTransactionsRequest) => void,
    dispatchLoadVariableTransaction: (data: GetVariableTransactionByIdRequest, callback?: (variableTransaction: VariableTransaction) => void) => void,
    dispatchCreateVariableTransaction: (data: CreateTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => void
    dispatchCreateProduct: (data: CreateProductRequest, callback?: (product: Product) => void) => void,
    dispatchUpdateVariableTransaction: (data: UpdateVariableTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => void,
    dispatchDeleteVariableTransaction: (data: DeleteVariableTransactionRequest, callback?: () => void) => void
}
