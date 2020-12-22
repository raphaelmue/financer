import {
    DeleteTokenRequest,
    GetUserRequest,
    LoginUserRequest,
    RegisterUserRequest,
    UpdateUsersSettingsRequest,
    User
}                     from '../../.openapi';
import {Action}       from 'redux';
import {ErrorMessage} from '../errorMessage';

export enum UserActionDefinition {
    LOGIN_REQUEST = 'AUTHENTICATION:LOGIN_REQUEST',
    LOGIN_SUCCESS = 'AUTHENTICATION:LOGIN_SUCCESS',
    LOGIN_FAILED = 'AUTHENTICATION:LOGIN_FAILED',
    REGISTER_REQUEST = 'AUTHENTICATION:REGISTER_REQUEST',
    REGISTER_SUCCESS = 'AUTHENTICATION:REGISTER_SUCCESS',
    REGISTER_FAILED = 'AUTHENTICATION:REGISTER_FAILED',
    LOGOUT_REQUEST = 'AUTHENTICATION:LOGOUT_REQUEST',
    LOGOUT_SUCCESS = 'AUTHENTICATION:LOGOUT_SUCCESS',
    LOGOUT_FAILED = 'AUTHENTICATION:LOGOUT_FAILED',
    GET_USER_REQUEST = 'USER:GET_USER_REQUEST',
    GET_USER_SUCCESS = 'USER:GET_USER_SUCCESS',
    GET_USER_FAILED = 'USER:GET_USER_FAILED',
    UPDATE_USERS_SETTINGS_REQUEST = 'USER:UPDATE_USERS_SETTINGS_REQUEST',
    UPDATE_USERS_SETTINGS_SUCCESS = 'USER:UPDATE_USERS_SETTINGS_SUCCESS',
    UPDATE_USERS_SETTINGS_FAILED = 'USER:UPDATE_USERS_SETTINGS_FAILED'
}

export type UserAction =
    LoginRequestAction | LoginSuccessAction | LoginFailedAction
    | RegisterRequestAction | RegisterSuccessAction | RegisterFailedAction
    | LogoutRequestAction | LogoutSuccessAction | LogoutFailedAction
    | GetUserRequestAction | GetUserSuccessAction | GetUserFailedAction
    | UpdateUsersSettingsRequestAction | UpdateUsersSettingsSuccessAction | UpdateUsersSettingsFailedAction;

interface LoginRequestAction extends Action {
    type: UserActionDefinition.LOGIN_REQUEST;
    payload: LoginUserRequest;
}

interface LoginSuccessAction extends Action {
    type: UserActionDefinition.LOGIN_SUCCESS;
    payload: User
}

interface LoginFailedAction extends Action {
    type: UserActionDefinition.LOGIN_FAILED;
    payload: ErrorMessage;
}

interface RegisterRequestAction extends Action {
    type: UserActionDefinition.REGISTER_REQUEST,
    payload: RegisterUserRequest
}

interface RegisterSuccessAction extends Action {
    type: UserActionDefinition.REGISTER_SUCCESS,
    payload: User;
}

interface RegisterFailedAction extends Action {
    type: UserActionDefinition.REGISTER_FAILED,
    payload: ErrorMessage;
}

interface LogoutRequestAction extends Action {
    type: UserActionDefinition.LOGOUT_REQUEST,
    payload: DeleteTokenRequest
}

interface LogoutSuccessAction extends Action {
    type: UserActionDefinition.LOGOUT_SUCCESS
}

interface LogoutFailedAction extends Action {
    type: UserActionDefinition.LOGOUT_FAILED,
    payload: ErrorMessage;
}

interface GetUserRequestAction extends Action {
    type: UserActionDefinition.GET_USER_REQUEST,
    payload: GetUserRequest
}

interface GetUserSuccessAction extends Action {
    type: UserActionDefinition.GET_USER_SUCCESS,
    payload: User
}

interface GetUserFailedAction extends Action {
    type: UserActionDefinition.GET_USER_FAILED,
    payload: ErrorMessage;
}

interface UpdateUsersSettingsRequestAction extends Action {
    type: UserActionDefinition.UPDATE_USERS_SETTINGS_REQUEST,
    payload: UpdateUsersSettingsRequest
}

interface UpdateUsersSettingsSuccessAction extends Action {
    type: UserActionDefinition.UPDATE_USERS_SETTINGS_SUCCESS,
    payload: User
}

interface UpdateUsersSettingsFailedAction extends Action {
    type: UserActionDefinition.UPDATE_USERS_SETTINGS_FAILED,
    payload: ErrorMessage;
}
