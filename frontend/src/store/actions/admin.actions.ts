import {Action}                                                                                  from 'redux';
import {AdminConfiguration, GetConfigurationRequest, PagedModelUser, UpdateConfigurationRequest} from '../../.openapi';
import {ErrorMessage}                                                                            from '../errorMessage';

export enum AdminActionDefinition {
    LOAD_ADMIN_CONFIGURATION_REQUEST = 'ADMIN:LOAD_ADMIN_CONFIGURATION_REQUEST',
    LOAD_ADMIN_CONFIGURATION_SUCCESS = 'ADMIN:LOAD_ADMIN_CONFIGURATION_SUCCESS',
    LOAD_ADMIN_CONFIGURATION_FAILED = 'ADMIN:LOAD_ADMIN_CONFIGURATION_FAILED',
    UPDATE_ADMIN_CONFIGURATION_REQUEST = 'ADMIN:UPDATE_ADMIN_CONFIGURATION_REQUEST',
    UPDATE_ADMIN_CONFIGURATION_SUCCESS = 'ADMIN:UPDATE_ADMIN_CONFIGURATION_SUCCESS',
    UPDATE_ADMIN_CONFIGURATION_FAILED = 'ADMIN:UPDATE_ADMIN_CONFIGURATION_FAILED',
    LOAD_USERS_REQUEST = 'ADMIN:LOAD_USERS_REQUEST',
    LOAD_USERS_SUCCESS = 'ADMIN:LOAD_USERS_SUCCESS',
    LOAD_USERS_FAILED = 'ADMIN:LOAD_USERS_FAILED',
}

export type AdminAction =
    LoadAdminConfigurationRequestAction
    | LoadAdminConfigurationSuccessAction
    | LoadAdminConfigurationFailedAction
    | UpdateAdminConfigurationRequestAction
    | UpdateAdminConfigurationSuccessAction
    | UpdateAdminConfigurationFailedAction
    | LoadUsersRequestAction
    | LoadUsersSuccessAction
    | LoadUsersFailedAction;

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

interface LoadUsersRequestAction extends Action {
    type: AdminActionDefinition.LOAD_USERS_REQUEST,
}

interface LoadUsersSuccessAction extends Action {
    type: AdminActionDefinition.LOAD_USERS_SUCCESS,
    payload: PagedModelUser
}

interface LoadUsersFailedAction extends Action {
    type: AdminActionDefinition.LOAD_USERS_FAILED,
    payload: ErrorMessage
}

