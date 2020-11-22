import {Action}                                              from 'redux';
import {GetConfigurationRequest, UpdateConfigurationRequest} from '../../.openapi/apis';
import {ErrorMessage}                                        from '../errorMessage';
import {AdminConfiguration}                                  from '../../.openapi/models';

export enum AdminActionDefinition {
    LOAD_ADMIN_CONFIGURATION_REQUEST = 'ADMIN:LOAD_ADMIN_CONFIGURATION_REQUEST',
    LOAD_ADMIN_CONFIGURATION_SUCCESS = 'ADMIN:LOAD_ADMIN_CONFIGURATION_SUCCESS',
    LOAD_ADMIN_CONFIGURATION_FAILED = 'ADMIN:LOAD_ADMIN_CONFIGURATION_FAILED',
    UPDATE_ADMIN_CONFIGURATION_REQUEST = 'ADMIN:UPDATE_ADMIN_CONFIGURATION_REQUEST',
    UPDATE_ADMIN_CONFIGURATION_SUCCESS = 'ADMIN:UPDATE_ADMIN_CONFIGURATION_SUCCESS',
    UPDATE_ADMIN_CONFIGURATION_FAILED = 'ADMIN:UPDATE_ADMIN_CONFIGURATION_FAILED'
}

export type AdminAction =
    LoadAdminConfigurationRequestAction
    | LoadAdminConfigurationSuccessAction
    | LoadAdminConfigurationFailedAction
    | UpdateAdminConfigurationRequestAction
    | UpdateAdminConfigurationSuccessAction
    | UpdateAdminConfigurationFailedAction;

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

interface UpdateAdminConfigurationRequestAction extends Action {
    type: AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_REQUEST,
    payload: UpdateConfigurationRequest
}

interface UpdateAdminConfigurationSuccessAction extends Action {
    type: AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_SUCCESS,
    payload: AdminConfiguration
}

interface UpdateAdminConfigurationFailedAction extends Action {
    type: AdminActionDefinition.UPDATE_ADMIN_CONFIGURATION_FAILED,
    payload: ErrorMessage
}

