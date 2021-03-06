import {FixedTransaction, PageMetadata, VariableTransaction} from '../../.openapi';
import {ReducerState}                                        from './reducers';
import {TransactionAction, TransactionActionDefinition}      from '../actions/transaction.actions';
import {TransactionApi}                                      from '../api/transaction.api';

export interface TransactionState extends ReducerState {
    fixedTransactions: FixedTransaction[],
    variableTransactions: VariableTransaction[],
    pageMetadata?: PageMetadata
}

export interface TransactionReducerProps extends TransactionApi {
    transactionState: TransactionState,
}

const initialState: TransactionState = {
    error: undefined,
    isLoading: false,
    fixedTransactions: [],
    variableTransactions: [],
    pageMetadata: undefined
};

export const transactionReducer = (state: TransactionState = initialState, action: TransactionAction): TransactionState => {
    switch (action.type) {
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS:
            return {
                ...state,
                isLoading: false,
                error: undefined,
                variableTransactions: action.payload.embedded?.variableTransactionDToes || [],
                pageMetadata: action.payload.page
            };
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_SUCCESS: {
            const transactions = state.variableTransactions;
            transactions.splice(transactions.findIndex(transaction => transaction.id === action.payload.id), 1);
            transactions.push(action.payload);
            return {...state, isLoading: false, error: undefined, variableTransactions: transactions};
        }
        case TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_SUCCESS:
            return {
                ...state,
                isLoading: false,
                error: undefined,
                variableTransactions: state.variableTransactions.concat(action.payload)
            };
        case TransactionActionDefinition.CREATE_PRODUCT_SUCCESS:
        case TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_SUCCESS:
        case TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_SUCCESS:
            return {...state, isLoading: false, error: undefined};
        case TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_SUCCESS:
            return {
                ...state,
                isLoading: false,
                error: undefined,
                fixedTransactions: action.payload.embedded?.fixedTransactionDToes || []
            };
        case TransactionActionDefinition.LOAD_FIXED_TRANSACTION_SUCCESS: {
            if (state.fixedTransactions !== undefined && state.fixedTransactions.length > 0) {
                const transactions = state.fixedTransactions;
                transactions.splice(transactions.findIndex(transaction => transaction.id === action.payload.id), 1);
                transactions.push(action.payload);
                return {...state, isLoading: false, error: undefined, fixedTransactions: transactions};
            } else {
                return {...state, isLoading: false, error: undefined, fixedTransactions: [action.payload]};
            }
        }
        case TransactionActionDefinition.CREATE_FIXED_TRANSACTION_SUCCESS: {
            const transactions = state.fixedTransactions;
            transactions.push(action.payload);
            return {
                ...state,
                isLoading: false,
                error: undefined,
                fixedTransactions: transactions
            };
        }
        case TransactionActionDefinition.UPDATE_FIXED_TRANSACTION_SUCCESS:
        case TransactionActionDefinition.CREATE_FIXED_TRANSACTION_AMOUNT_SUCCESS:
            return {...state, isLoading: false, error: undefined};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST:
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_REQUEST:
        case TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_REQUEST:
        case TransactionActionDefinition.CREATE_PRODUCT_REQUEST:
        case TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_REQUEST:
        case TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_REQUEST:
        case TransactionActionDefinition.DELETE_PRODUCTS_REQUEST:
        case TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_REQUEST:
        case TransactionActionDefinition.LOAD_FIXED_TRANSACTION_REQUEST:
        case TransactionActionDefinition.CREATE_FIXED_TRANSACTION_REQUEST:
        case TransactionActionDefinition.UPDATE_FIXED_TRANSACTION_REQUEST:
        case TransactionActionDefinition.DELETE_FIXED_TRANSACTION_REQUEST:
        case TransactionActionDefinition.CREATE_FIXED_TRANSACTION_AMOUNT_REQUEST:
        case TransactionActionDefinition.DELETE_FIXED_TRANSACTION_AMOUNTS_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED:
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_FAILED:
        case TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_FAILED:
        case TransactionActionDefinition.CREATE_PRODUCT_FAILED:
        case TransactionActionDefinition.UPDATE_VARIABLE_TRANSACTION_FAILED:
        case TransactionActionDefinition.DELETE_VARIABLE_TRANSACTION_FAILED:
        case TransactionActionDefinition.DELETE_PRODUCTS_FAILED:
        case TransactionActionDefinition.LOAD_FIXED_TRANSACTIONS_FAILED:
        case TransactionActionDefinition.LOAD_FIXED_TRANSACTION_FAILED:
        case TransactionActionDefinition.CREATE_FIXED_TRANSACTION_FAILED:
        case TransactionActionDefinition.UPDATE_FIXED_TRANSACTION_FAILED:
        case TransactionActionDefinition.DELETE_FIXED_TRANSACTION_FAILED:
        case TransactionActionDefinition.CREATE_FIXED_TRANSACTION_AMOUNT_FAILED:
        case TransactionActionDefinition.DELETE_FIXED_TRANSACTION_AMOUNTS_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
