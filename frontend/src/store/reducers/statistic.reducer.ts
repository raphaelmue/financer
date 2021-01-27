import {ReducerState}                               from './reducers';
import {StatisticApi}                               from '../api/statistic.api';
import {StatisticAction, StatisticActionDefinition} from '../actions/statistic.actions';
import {BalanceHistory}                             from '../../.openapi';

export interface StatisticState extends ReducerState {
    balanceHistory: BalanceHistory | undefined
}

export interface StatisticReducerProps extends StatisticApi {
    statisticState: StatisticState
}

const initialState: StatisticState = {
    isLoading: false,
    error: undefined,
    balanceHistory: undefined
};

export const statisticReducer = (state: StatisticState = initialState, action: StatisticAction) => {
    switch (action.type) {
        case StatisticActionDefinition.LOAD_BALANCE_HISTORY_SUCCESS:
            return {...state, isLoading: true, balanceHistory: action.payload};
        case StatisticActionDefinition.LOAD_BALANCE_HISTORY_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case StatisticActionDefinition.LOAD_BALANCE_HISTORY_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
