import {BalanceHistory, GetUsersBalanceHistoryRequest} from '../../.openapi';
import {Action}                                        from 'redux';
import {ErrorMessage}                                  from '../errorMessage';

export enum StatisticActionDefinition {
    LOAD_BALANCE_HISTORY_REQUEST = 'STATISTICS:LOAD_BALANCE_HISTORY_REQUEST',
    LOAD_BALANCE_HISTORY_SUCCESS = 'STATISTICS:LOAD_BALANCE_HISTORY_SUCCESS',
    LOAD_BALANCE_HISTORY_FAILED = 'STATISTICS:LOAD_BALANCE_HISTORY_FAILED'
}

export type StatisticAction =
    LoadBalanceHistoryRequestAction | LoadBalanceHistorySuccessAction | LoadBalanceHistoryFailedAction;


interface LoadBalanceHistoryRequestAction extends Action {
    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_REQUEST,
    payload: GetUsersBalanceHistoryRequest
}

interface LoadBalanceHistorySuccessAction extends Action {
    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_SUCCESS,
    payload: BalanceHistory
}

interface LoadBalanceHistoryFailedAction extends Action {
    type: StatisticActionDefinition.LOAD_BALANCE_HISTORY_FAILED,
    payload: ErrorMessage
}
