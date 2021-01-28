import {
    GetCategoryDistributionRequest,
    GetUsersBalanceHistoryRequest,
    StatisticsApi
}                                     from '../../.openapi';
import {bindActionCreators, Dispatch} from 'redux';
import {apiConfiguration}             from './index';
import {ErrorMessage}                 from '../errorMessage';
import {StatisticActionDefinition}    from '../actions/statistic.actions';
import {BalanceHistory}               from '../../.openapi/models/BalanceHistory';
import {CategoryDistribution}         from '../../.openapi/models/CategoryDistribution';

const loadBalanceHistory = (data: GetUsersBalanceHistoryRequest, callback?: (balanceHistory: BalanceHistory) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_REQUEST,
            payload: data
        });
        const api = new StatisticsApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersBalanceHistory(data)
            .then((balanceHistory: BalanceHistory) => {
                dispatch({
                    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_SUCCESS,
                    payload: balanceHistory
                });
                if (callback) callback(balanceHistory);
            }), StatisticActionDefinition.LOAD_BALANCE_HISTORY_FAILED, dispatch);
    };
};

const loadCategoryDistribution = (data: GetCategoryDistributionRequest, callback?: (balanceHistory: CategoryDistribution) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_REQUEST,
            payload: data
        });
        const api = new StatisticsApi(apiConfiguration());
        ErrorMessage.resolveError(api.getCategoryDistribution(data)
            .then((balanceHistory: CategoryDistribution) => {
                dispatch({
                    type: StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_SUCCESS,
                    payload: balanceHistory
                });
                if (callback) callback(balanceHistory);
            }), StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_FAILED, dispatch);
    };
};

export interface StatisticApi {
    dispatchLoadBalanceHistory: typeof loadBalanceHistory,
    dispatchLoadCategoryDistribution: typeof loadCategoryDistribution
}

export const statisticDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadBalanceHistory: loadBalanceHistory,
    dispatchLoadCategoryDistribution: loadCategoryDistribution
}, dispatch);
