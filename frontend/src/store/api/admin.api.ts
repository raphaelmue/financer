import {AdminApi as Api, GetConfigurationRequest} from '../../.openapi/apis';
import {Dispatch}                                 from 'redux';
import {apiConfiguration}                         from './index';
import {ErrorMessage}                             from '../errorMessage';
import {AdminActionDefinition}                    from '../actions/admin.actions';

export const loadAdminConfiguration = (data: GetConfigurationRequest = {}) => {
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
            }), AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_FAILED, dispatch);
    };
};

export interface AdminApi {
    dispatchLoadAdminConfiguration: (data: GetConfigurationRequest) => void
}
