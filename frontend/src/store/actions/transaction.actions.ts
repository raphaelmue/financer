import {Action}       from 'redux';
import {
    CreateProductRequest,
    CreateTransactionRequest,
    DeleteProductRequest,
    DeleteVariableTransactionRequest,
    GetUsersFixedTransactionsRequest,
    GetUsersVariableTransactionsRequest,
    GetVariableTransactionByIdRequest,
    PagedModelFixedTransaction,
    PagedModelVariableTransaction,
    Product,
    UpdateVariableTransactionRequest,
    VariableTransaction,
}                     from '../../.openapi';
import {ErrorMessage} from '../errorMessage';

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
    UPDATE_VARIABLE_TRANSACTION_REQUEST = 'TRANSACTION:UPDATE_VARIABLE_TRANSACTION_REQUEST',
    UPDATE_VARIABLE_TRANSACTION_SUCCESS = 'TRANSACTION:UPDATE_VARIABLE_TRANSACTION_SUCCESS',
    UPDATE_VARIABLE_TRANSACTION_FAILED = 'TRANSACTION:UPDATE_VARIABLE_TRANSACTION_FAILED',
    DELETE_VARIABLE_TRANSACTION_REQUEST = 'TRANSACTION:DELETE_VARIABLE_TRANSACTION_REQUEST',
    DELETE_VARIABLE_TRANSACTION_SUCCESS = 'TRANSACTION:DELETE_VARIABLE_TRANSACTION_SUCCESS',
    DELETE_VARIABLE_TRANSACTION_FAILED = 'TRANSACTION:DELETE_VARIABLE_TRANSACTION_FAILED',
    CREATE_PRODUCT_REQUEST = 'TRANSACTION:CREATE_PRODUCT_REQUEST',
    CREATE_PRODUCT_SUCCESS = 'TRANSACTION:CREATE_PRODUCT_SUCCESS',
    CREATE_PRODUCT_FAILED = 'TRANSACTION:CREATE_PRODUCT_FAILED',
    DELETE_PRODUCTS_REQUEST = 'TRANSACTION:DELETE_PRODUCTS_REQUEST',
    DELETE_PRODUCTS_SUCCESS = 'TRANSACTION:DELETE_PRODUCTS_SUCCESS',
    DELETE_PRODUCTS_FAILED = 'TRANSACTION:DELETE_PRODUCTS_FAILED',
    LOAD_FIXED_TRANSACTIONS_REQUEST = 'TRANSACTION:LOAD_FIXED_TRANSACTIONS_REQUEST',
    LOAD_FIXED_TRANSACTIONS_SUCCESS = 'TRANSACTION:LOAD_FIXED_TRANSACTIONS_SUCCESS',
    LOAD_FIXED_TRANSACTIONS_FAILED = 'TRANSACTION:LOAD_FIXED_TRANSACTIONS_FAILED',
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
    | UpdateVariableTransactionRequestAction
    | UpdateVariableTransactionSuccessAction
    | UpdateVariableTransactionFailedAction
    | DeleteVariableTransactionRequestAction
    | DeleteVariableTransactionSuccessAction
    | DeleteVariableTransactionFailedAction
    | CreateProductRequestAction
    | CreateProductSuccessAction
    | CreateProductFailedAction
    | DeleteProductsRequestAction
    | DeleteProductsSuccessAction
    | DeleteProductsFailedAction
    | LoadFixedTransactionRequestAction
    | LoadFixedTransactionSuccessAction
    | LoadFixedTransactionFailedAction;

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

interface UpdateVariableTransactionRequestAction extends Action {
    type: TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_REQUEST,
    payload: UpdateVariableTransactionRequest
}

interface UpdateVariableTransactionSuccessAction extends Action {
    type: TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_SUCCESS,
    payload: VariableTransaction
}

interface UpdateVariableTransactionFailedAction extends Action {
    type: TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_FAILED,
    payload: ErrorMessage
}

interface DeleteVariableTransactionRequestAction extends Action {
    type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_REQUEST,
    payload: DeleteVariableTransactionRequest
}

interface DeleteVariableTransactionSuccessAction extends Action {
    type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_SUCCESS
}

interface DeleteVariableTransactionFailedAction extends Action {
    type: TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_FAILED,
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

interface DeleteProductsRequestAction extends Action {
    type: TransactionActionDefinition.DELETE_PRODUCTS_REQUEST,
    payload: DeleteProductRequest
}

interface DeleteProductsSuccessAction extends Action {
    type: TransactionActionDefinition.DELETE_PRODUCTS_SUCCESS
}

interface DeleteProductsFailedAction extends Action {
    type: TransactionActionDefinition.DELETE_PRODUCTS_FAILED,
    payload: ErrorMessage
}

interface LoadFixedTransactionRequestAction extends Action {
    type: TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_REQUEST,
    payload: GetUsersFixedTransactionsRequest
}

interface LoadFixedTransactionSuccessAction extends Action {
    type: TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_SUCCESS,
    payload: PagedModelFixedTransaction
}

interface LoadFixedTransactionFailedAction extends Action {
    type: TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_FAILED,
    payload: ErrorMessage
}
