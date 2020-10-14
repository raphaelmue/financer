import {
    CreateTransactionRequest,
    GetUsersVariableTransactionsRequest,
    UserApi,
    VariableTransactionApi
}                                    from '../../.openapi/apis';
import {Dispatch}                    from 'redux';
import {apiConfiguration}            from './index';
import {ErrorMessage}                from '../errorMessage';
import {VariableTransaction}         from '../../.openapi/models';
import {TransactionActionDefinition} from '../actions/transaction.actions';

export const loadVariableTransactions = (data: GetUsersVariableTransactionsRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST,
            payload: data
        });
        let api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersVariableTransactions(data)
            .then((transactions: VariableTransaction[]) => dispatch({
                type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS,
                payload: transactions
            })), TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED, dispatch);
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

export interface TransactionApi {
    dispatchLoadVariableTransactions: (data: GetUsersVariableTransactionsRequest) => void
    dispatchCreateVariableTransaction: (data: CreateTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => void
}
