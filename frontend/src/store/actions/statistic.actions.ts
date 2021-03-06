import {
    DataSet,
    GetCategoryDistributionRequest,
    GetUsersBalanceHistoryRequest,
    GetVariableTransactionCountHistoryRequest
}               from '../../.openapi';
import {Action} from 'redux';
import {ErrorMessage}                                                           from '../errorMessage';

export enum StatisticActionDefinition {
    LOAD_BALANCE_HISTORY_REQUEST = 'STATISTICS:LOAD_BALANCE_HISTORY_REQUEST',
    LOAD_BALANCE_HISTORY_SUCCESS = 'STATISTICS:LOAD_BALANCE_HISTORY_SUCCESS',
    LOAD_BALANCE_HISTORY_FAILED = 'STATISTICS:LOAD_BALANCE_HISTORY_FAILED',
    LOAD_CATEGORY_DISTRIBUTION_REQUEST = 'STATISTICS:LOAD_CATEGORY_DISTRIBUTION_REQUEST',
    LOAD_CATEGORY_DISTRIBUTION_SUCCESS = 'STATISTICS:LOAD_CATEGORY_DISTRIBUTION_SUCCESS',
    LOAD_CATEGORY_DISTRIBUTION_FAILED = 'STATISTICS:LOAD_CATEGORY_DISTRIBUTION_FAILED',
    LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_REQUEST = 'STATISTICS:LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_REQUEST',
    LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_SUCCESS = 'STATISTICS:LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_SUCCESS',
    LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_FAILED = 'STATISTICS:LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_FAILED'
}

export type StatisticAction =
    LoadBalanceHistoryRequestAction
    | LoadBalanceHistorySuccessAction
    | LoadBalanceHistoryFailedAction
    | LoadCategoryDistributionRequestAction
    | LoadCategoryDistributionSuccessAction
    | LoadCategoryDistributionFailedAction
    | LoadVariableTransactionCountHistoryRequestAction
    | LoadVariableTransactionCountHistorySuccessAction
    | LoadVariableTransactionCountHistoryFailedAction;

interface LoadBalanceHistoryRequestAction extends Action {
    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_REQUEST,
    payload: GetUsersBalanceHistoryRequest
}

interface LoadBalanceHistorySuccessAction extends Action {
    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_SUCCESS,
    payload: DataSet
}

interface LoadBalanceHistoryFailedAction extends Action {
    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_FAILED,
    payload: ErrorMessage
}

interface LoadCategoryDistributionRequestAction extends Action {
    type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_REQUEST,
    payload: GetCategoryDistributionRequest
}

interface LoadCategoryDistributionSuccessAction extends Action {
    type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_SUCCESS,
    payload: DataSet
}

interface LoadCategoryDistributionFailedAction extends Action {
    type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_FAILED,
    payload: ErrorMessage
}

interface LoadVariableTransactionCountHistoryRequestAction extends Action {
    type: StatisticActionDefinition.LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_REQUEST,
    payload: GetVariableTransactionCountHistoryRequest
}

interface LoadVariableTransactionCountHistorySuccessAction extends Action {
    type: StatisticActionDefinition.LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_SUCCESS,
    payload: DataSet
}

interface LoadVariableTransactionCountHistoryFailedAction extends Action {
    type: StatisticActionDefinition.LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_FAILED,
    payload: ErrorMessage
}
