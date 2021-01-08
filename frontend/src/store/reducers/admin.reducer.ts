import {ReducerState}                       from './reducers';
import {
    AdminConfiguration,
    AdminConfigurationDefaultCurrencyEnum,
    AdminConfigurationDefaultLanguageEnum,
    PageMetadata
}                                           from '../../.openapi';
import {AdminApi}                           from '../api/admin.api';
import {AdminAction, AdminActionDefinition} from '../actions/admin.actions';

export interface AdminState extends ReducerState {
    configuration: AdminConfiguration,
    pageMetadata?: PageMetadata
}

export interface AdminReducerProps extends AdminApi {
    adminState: AdminState
}

const initialState: AdminState = {
    isLoading: false,
    error: undefined,
    configuration: {
        defaultLanguage: AdminConfigurationDefaultLanguageEnum.En,
        defaultCurrency: AdminConfigurationDefaultCurrencyEnum.USD,
    },
    pageMetadata: undefined
};

export const adminReducer = (state: AdminState = initialState, action: AdminAction) => {
    switch (action.type) {
        case AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_SUCCESS:
        case AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_SUCCESS:
            return {...state, isLoading: false, error: undefined, configuration: action.payload};
        case AdminActionDefinition.LOAD_USERS_SUCCESS:
            return {...state, isLoading: false, error: undefined, pageMetadata: action.payload.page};
        case AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_REQUEST:
        case AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_REQUEST:
        case AdminActionDefinition.LOAD_USERS_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_FAILED:
        case AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_FAILED:
        case AdminActionDefinition.LOAD_USERS_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
