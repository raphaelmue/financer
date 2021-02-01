import {ReducerState}                               from './reducers';
import {StatisticApi}                               from '../api/statistic.api';
import {StatisticAction, StatisticActionDefinition} from '../actions/statistic.actions';
import {DataSet}                                    from '../../.openapi';

export interface StatisticState extends ReducerState {
    balanceHistory: DataSet | undefined,
    categoryDistribution: DataSet | undefined
}

export interface StatisticReducerProps extends StatisticApi {
    statisticState: StatisticState
}

const initialState: StatisticState = {
    isLoading: false,
    error: undefined,
    balanceHistory: undefined,
    categoryDistribution: undefined
};

export const statisticReducer = (state: StatisticState = initialState, action: StatisticAction) => {
    switch (action.type) {
        case StatisticActionDefinition.LOAD_BALANCE_HISTORY_SUCCESS:
            return {...state, isLoading: true, balanceHistory: action.payload};
        case StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_SUCCESS:
            return {...state, isLoading: true, categoryDistribution: action.payload};
        case StatisticActionDefinition.LOAD_BALANCE_HISTORY_REQUEST:
        case StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case StatisticActionDefinition.LOAD_BALANCE_HISTORY_FAILED:
        case StatisticActionDefinition.LOAD_CATEGORY_DISTRIBUTION_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
