import {
    AdminApi as Api,
    GetConfigurationRequest,
    GetUsersRequest,
    UpdateConfigurationRequest
}                                     from '../../.openapi/apis';
import {bindActionCreators, Dispatch} from 'redux';
import {apiConfiguration}             from './index';
import {ErrorMessage}                 from '../errorMessage';
import {AdminActionDefinition}        from '../actions/admin.actions';
import {AdminConfiguration, User}     from '../../.openapi/models';

export const loadAdminConfiguration = (data: GetConfigurationRequest = {}, callback?: (adminConfiguration: AdminConfiguration) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_REQUEST,
            payload: data
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.getConfiguration(data)
            .then((configuration) => {
                dispatch({
                    type: AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_SUCCESS,
                    payload: configuration
                });
                if (callback) callback(configuration);
            }), AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_FAILED, dispatch);
    };
};

export const updateAdminConfiguration = (data: UpdateConfigurationRequest, callback?: (adminConfiguration: AdminConfiguration) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_REQUEST,
            payload: data
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.updateConfiguration(data)
            .then((configuration) => {
                dispatch({
                    type: AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_SUCCESS,
                    payload: configuration
                });
                if (callback) callback(configuration);
            }), AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_FAILED, dispatch);
    };
};

export const loadUsers = (data: GetUsersRequest, callback?: (users: User[]) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: AdminActionDefinition.LOAD_USERS_REQUEST,
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.getUsers(data)
            .then((users) => {
                dispatch({
                    type: AdminActionDefinition.LOAD_USERS_SUCCESS,
                    payload: users
                });
                if (callback) callback(users.embedded?.userDToes || []);
            }), AdminActionDefinition.LOAD_USERS_FAILED, dispatch);
    };
};

export interface AdminApi {
    dispatchLoadAdminConfiguration: (data: GetConfigurationRequest, callback?: (adminConfiguration: AdminConfiguration) => void) => void,
    dispatchUpdateAdminConfiguration: (data: UpdateConfigurationRequest, callback?: (adminConfiguration: AdminConfiguration) => void) => void,
    dispatchLoadUsers: (data: GetUsersRequest, callback?: (users: User[]) => void) => void
}

export const adminDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadAdminConfiguration: loadAdminConfiguration,
    dispatchUpdateAdminConfiguration: updateAdminConfiguration,
    dispatchLoadUsers: loadUsers
}, dispatch);

