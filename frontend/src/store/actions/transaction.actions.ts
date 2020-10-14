import {Action}                                                        from 'redux';
import {CreateTransactionRequest, GetUsersVariableTransactionsRequest} from '../../.openapi/apis';
import {ErrorMessage}                                                  from '../errorMessage';
import {VariableTransaction}                                           from '../../.openapi/models';

export enum TransactionActionDefinition {
    LOAD_VARIABLE_TRANSACTIONS_REQUEST = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_REQUEST',
    LOAD_VARIABLE_TRANSACTIONS_SUCCESS = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_SUCCESS',
    LOAD_VARIABLE_TRANSACTIONS_FAILED = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_FAILED',
    CREATE_VARIABLE_TRANSACTION_REQUEST = 'TRANSACTION:CREATE_VARIABLE_TRANSACTION_REQUEST',
    CREATE_VARIABLE_TRANSACTION_SUCCESS = 'TRANSACTION:CREATE_VARIABLE_TRANSACTION_SUCCESS',
    CREATE_VARIABLE_TRANSACTION_FAILED = 'TRANSACTION:CREATE_VARIABLE_TRANSACTION_FAILED'
}

export type TransactionAction = LoadVariableTransactionRequestAction
    | LoadVariableTransactionSuccessAction
    | LoadVariableTransactionFailedAction
    | CreateVariableTransactionRequestAction
    | CreateVariableTransactionSuccessAction
    | CreateVariableTransactionFailedAction;

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

interface CreateVariableTransactionRequestAction extends Action {
    type: TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_REQUEST,
    payload: CreateTransactionRequest
}

interface CreateVariableTransactionSuccessAction extends Action {
    type: TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_SUCCESS,
    payload: VariableTransaction
}

interface CreateVariableTransactionFailedAction extends Action {
    type: TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_FAILED,
    payload: ErrorMessage
}
