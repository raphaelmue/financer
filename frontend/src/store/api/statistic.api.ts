import {BalanceHistory, GetUsersBalanceHistoryRequest, StatisticsApi} from '../../.openapi';
import {bindActionCreators, Dispatch}                                 from 'redux';
import {apiConfiguration}                                             from './index';
import {ErrorMessage}                                                 from '../errorMessage';
import {StatisticActionDefinition}                                    from '../actions/statistic.actions';

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

export interface StatisticApi {
    dispatchLoadBalanceHistory: typeof loadBalanceHistory
}

export const statisticDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadBalanceHistory: loadBalanceHistory
}, dispatch);
