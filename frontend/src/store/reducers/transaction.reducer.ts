import {VariableTransaction}                            from '../../.openapi/models';
import {ReducerState}                                   from './reducers';
import {TransactionAction, TransactionActionDefinition} from '../actions/transaction.actions';
import {TransactionApi}                                 from '../api/transaction.api';

export interface TransactionState extends ReducerState {
    variableTransactions: VariableTransaction[],
}

export interface TransactionReducerProps extends TransactionApi {
    transactionState: TransactionState,
}

const initialState: TransactionState = {
    error: undefined,
    isLoading: false,
    variableTransactions: []
};

export const transactionReducer = (state: TransactionState = initialState, action: TransactionAction) => {
    switch (action.type) {
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_SUCCESS:
            return {...state, isLoading: false, error: undefined, variableTransactions: action.payload};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTIONS_FAILED:
            return {...state, isLoading: false, error: action.payload};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_SUCCESS:
            let transactions = state.variableTransactions;
            transactions.splice(transactions.findIndex(transaction => transaction.id === action.payload.id), 1);
            transactions.push(action.payload);
            return {...state, isLoading: false, error: undefined, variableTransactions: transactions};
        case TransactionActionDefinition.LOAD_VARIABLE_TRANSACTION_FAILED:
            return {...state, isLoading: false, error: action.payload};
        case TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_SUCCESS:
            return {
                ...state,
                isLoading: false,
                error: undefined,
                variableTransactions: state.variableTransactions.concat(action.payload)
            };
        case TransactionActionDefinition.CREATE_VARIABLE_TRANSACTION_FAILED:
            return {...state, isLoading: false, error: action.payload};
        case TransactionActionDefinition.CREATE_PRODUCT_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case TransactionActionDefinition.CREATE_PRODUCT_SUCCESS:
            return {...state, isLoading: false, error: undefined};
        case TransactionActionDefinition.CREATE_PRODUCT_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
