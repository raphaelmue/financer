import {Action}                  from 'redux';
import {GetConfigurationRequest} from '../../.openapi/apis';
import {ErrorMessage}            from '../errorMessage';
import {AdminConfiguration}      from '../../.openapi/models';

export enum AdminActionDefinition {
    LOAD_ADMIN_CONFIGURATION_REQUEST = 'ADMIN:LOAD_ADMIN_CONFIGURATION_REQUEST',
    LOAD_ADMIN_CONFIGURATION_SUCCESS = 'ADMIN:LOAD_ADMIN_CONFIGURATION_SUCCESS',
    LOAD_ADMIN_CONFIGURATION_FAILED = 'ADMIN:LOAD_ADMIN_CONFIGURATION_FAILED'
}

export type AdminAction =
    LoadAdminConfigurationRequestAction
    | LoadAdminConfigurationSuccessAction
    | LoadAdminConfigurationFailedAction;

interface LoadAdminConfigurationRequestAction extends Action {
    type: AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_REQUEST,
    payload: GetConfigurationRequest
}

interface LoadAdminConfigurationSuccessAction extends Action {
    type: AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_SUCCESS,
    payload: AdminConfiguration
}

interface LoadAdminConfigurationFailedAction extends Action {
    type: AdminActionDefinition.LOAD_ADMIN_CONFIGURATION_FAILED,
    payload: ErrorMessage
}
