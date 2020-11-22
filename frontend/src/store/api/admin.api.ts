import {AdminApi as Api, GetConfigurationRequest, UpdateConfigurationRequest} from '../../.openapi/apis';
import {Dispatch}                                                             from 'redux';
import {apiConfiguration}                                                     from './index';
import {ErrorMessage}                                                         from '../errorMessage';
import {AdminActionDefinition}                                                from '../actions/admin.actions';
import {AdminConfiguration}                                                   from '../../.openapi/models';

export const loadAdminConfiguration = (data: GetConfigurationRequest = {}, callback?: (adminConfiguration: AdminConfiguration) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_REQUEST,
            payload: data
        });
        let api = new Api(apiConfiguration());
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
        let api = new Api(apiConfiguration());
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

export interface AdminApi {
    dispatchLoadAdminConfiguration: (data: GetConfigurationRequest, callback?: (adminConfiguration: AdminConfiguration) => void) => void
    dispatchUpdateAdminConfiguration: (data: UpdateConfigurationRequest, callback?: (adminConfiguration: AdminConfiguration) => void) => void
}
