import {ReducerState}                       from './reducers';
import {AdminConfiguration}                 from '../../.openapi/models';
import {AdminApi}                           from '../api/admin.api';
import {AdminAction, AdminActionDefinition} from '../actions/admin.actions';

export interface AdminState extends ReducerState {
    configuration: AdminConfiguration
}

export interface AdminReducerProps extends AdminApi {
    adminState: AdminState
}

const initialState: AdminState = {
    isLoading: false,
    error: undefined,
    configuration: {
        defaultLanguage: 'en',
        defaultCurrency: 'USD'
    }
};

export const adminReducer = (state: AdminState = initialState, action: AdminAction) => {
    switch (action.type) {
        case AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_SUCCESS:
            return {...state, isLoading: false, error: undefined, configuration: action.payload};
        case AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
