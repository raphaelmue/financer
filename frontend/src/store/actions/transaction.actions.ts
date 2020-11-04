import {Action}                       from 'redux';
import {
    CreateProductRequest,
    CreateTransactionRequest,
    GetUsersVariableTransactionsRequest,
    GetVariableTransactionByIdRequest
}                                     from '../../.openapi/apis';
import {ErrorMessage}                                                from '../errorMessage';
import {PagedModelVariableTransaction, Product, VariableTransaction} from '../../.openapi/models';

export enum TransactionActionDefinition {
    LOAD_VARIABLE_TRANSACTIONS_REQUEST = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_REQUEST',
    LOAD_VARIABLE_TRANSACTIONS_SUCCESS = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_SUCCESS',
    LOAD_VARIABLE_TRANSACTIONS_FAILED = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTIONS_FAILED',
    LOAD_VARIABLE_TRANSACTION_REQUEST = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTION_REQUEST',
    LOAD_VARIABLE_TRANSACTION_SUCCESS = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTION_SUCCESS',
    LOAD_VARIABLE_TRANSACTION_FAILED = 'TRANSACTIONS:LOAD_VARIABLE_TRANSACTION_FAILED',
    CREATE_VARIABLE_TRANSACTION_REQUEST = 'TRANSACTION:CREATE_VARIABLE_TRANSACTION_REQUEST',
    CREATE_VARIABLE_TRANSACTION_SUCCESS = 'TRANSACTION:CREATE_VARIABLE_TRANSACTION_SUCCESS',
    CREATE_VARIABLE_TRANSACTION_FAILED = 'TRANSACTION:CREATE_VARIABLE_TRANSACTION_FAILED',
    CREATE_PRODUCT_REQUEST = 'TRANSACTION:CREATE_PRODUCT_REQUEST',
    CREATE_PRODUCT_SUCCESS = 'TRANSACTION:CREATE_PRODUCT_SUCCESS',
    CREATE_PRODUCT_FAILED = 'TRANSACTION:CREATE_PRODUCT_FAILED',

}

export type TransactionAction = LoadVariableTransactionsRequestAction
    | LoadVariableTransactionsSuccessAction
    | LoadVariableTransactionsFailedAction
    | LoadVariableTransactionRequestAction
    | LoadVariableTransactionSuccessAction
    | LoadVariableTransactionFailedAction
    | CreateVariableTransactionRequestAction
    | CreateVariableTransactionSuccessAction
    | CreateVariableTransactionFailedAction
    | CreateProductRequestAction
    | CreateProductSuccessAction
    | CreateProductFailedAction;

interface LoadVariableTransactionsRequestAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST,
    payload: GetUsersVariableTransactionsRequest
}

interface LoadVariableTransactionsSuccessAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS,
    payload: PagedModelVariableTransaction
}

interface LoadVariableTransactionsFailedAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED,
    payload: ErrorMessage
}

interface LoadVariableTransactionRequestAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_REQUEST,
    payload: GetVariableTransactionByIdRequest
}

interface LoadVariableTransactionSuccessAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_SUCCESS,
    payload: VariableTransaction
}

interface LoadVariableTransactionFailedAction extends Action {
    type: TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_FAILED,
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

interface CreateProductRequestAction extends Action {
    type: TransactionActionDefinition.CREATE_PRODUCT_REQUEST,
    payload: CreateProductRequest
}

interface CreateProductSuccessAction extends Action {
    type: TransactionActionDefinition.CREATE_PRODUCT_SUCCESS,
    payload: Product
}

interface CreateProductFailedAction extends Action {
    type: TransactionActionDefinition.CREATE_PRODUCT_FAILED,
    payload: ErrorMessage
}