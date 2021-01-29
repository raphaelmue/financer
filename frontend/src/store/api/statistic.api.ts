import {
    DataSet,
    GetCategoryDistributionRequest,
    GetUsersBalanceHistoryRequest,
    GetVariableTransactionCountHistoryRequest,
    StatisticsApi
}                                     from '../../.openapi';
import {bindActionCreators, Dispatch} from 'redux';
import {apiConfiguration}             from './index';
import {ErrorMessage}                 from '../errorMessage';
import {StatisticActionDefinition}    from '../actions/statistic.actions';

const loadBalanceHistory = (data: GetUsersBalanceHistoryRequest, callback?: (balanceHistory: DataSet) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_REQUEST,
            payload: data
        });
        const api = new StatisticsApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersBalanceHistory(data)
            .then((balanceHistory: DataSet) => {
                dispatch({
                    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_SUCCESS,
                    payload: balanceHistory
                });
                if (callback) callback(balanceHistory);
            }), StatisticActionDefinition.LOAD_BALANCE_HISTORY_FAILED, dispatch);
    };
};

const loadCategoryDistribution = (data: GetCategoryDistributionRequest, callback?: (balanceHistory: DataSet) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_REQUEST,
            payload: data
        });
        const api = new StatisticsApi(apiConfiguration());
        ErrorMessage.resolveError(api.getCategoryDistribution(data)
            .then((balanceHistory: DataSet) => {
                dispatch({
                    type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_SUCCESS,
                    payload: balanceHistory
                });
                if (callback) callback(balanceHistory);
            }), StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_FAILED, dispatch);
    };
};

const loadVariableTransactionCountHistory = (data: GetVariableTransactionCountHistoryRequest, callback?: (balanceHistory: DataSet) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: StatisticActionDefinition.LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_REQUEST,
            payload: data
        });
        const api = new StatisticsApi(apiConfiguration());
        ErrorMessage.resolveError(api.getVariableTransactionCountHistory(data)
            .then((balanceHistory: DataSet) => {
                dispatch({
                    type: StatisticActionDefinition.LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_SUCCESS,
                    payload: balanceHistory
                });
                if (callback) callback(balanceHistory);
            }), StatisticActionDefinition.LOAD_VARIABLE_TRANSACTION_COUNT_HISTORY_FAILED, dispatch);
    };
};


export interface StatisticApi {
    dispatchLoadBalanceHistory: typeof loadBalanceHistory,
    dispatchLoadCategoryDistribution: typeof loadCategoryDistribution,
    dispatchLoadVariableTransactionCountHistory: typeof loadVariableTransactionCountHistory
}

export const statisticDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadBalanceHistory: loadBalanceHistory,
    dispatchLoadCategoryDistribution: loadCategoryDistribution,
    dispatchLoadVariableTransactionCountHistory: loadVariableTransactionCountHistory
}, dispatch);
