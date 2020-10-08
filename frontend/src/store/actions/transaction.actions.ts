import {Action}                              from 'redux';
import {GetUsersVariableTransactionsRequest} from '../../.openapi/apis';
import {ErrorMessage}                        from '../errorMessage';
import {VariableTransaction}                 from '../../.openapi/models';

export enum TransactionActionDefinition {
    LOAD_VARIABLE_TRANSACTIONS_REQUEST = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_REQUEST',
    LOAD_VARIABLE_TRANSACTIONS_SUCCESS = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_SUCCESS',
    LOAD_VARIABLE_TRANSACTIONS_FAILED = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_FAILED'
}

export type TransactionAction =
    LoadVariableTransactionRequestAction | LoadVariableTransactionSuccessAction | LoadVariableTransactionFailedAction;

interface LoadVariableTransactionRequestAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST,
    payload: GetUsersVariableTransactionsRequest
}

interface LoadVariableTransactionSuccessAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS,
    payload: VariableTransaction[]
}

interface LoadVariableTransactionFailedAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED,
    payload: ErrorMessage
}