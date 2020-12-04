import {DeleteTokenRequest, LoginUserRequest, RegisterUserRequest} from '../../.openapi/apis';
import {User}                                                      from '../../.openapi/models';
import {Action}                                                    from 'redux';
import {ErrorMessage}                                              from '../errorMessage';

export enum UserActionDefinition {
    LOGIN_REQUEST = 'AUTHENTICATION:LOGIN_REQUEST',
    LOGIN_SUCCESS = 'AUTHENTICATION:LOGIN_SUCCESS',
    LOGIN_FAILED = 'AUTHENTICATION:LOGIN_FAILED',
    REGISTER_REQUEST = 'AUTHENTICATION:REGISTER_REQUEST',
    REGISTER_SUCCESS = 'AUTHENTICATION:REGISTER_SUCCESS',
    REGISTER_FAILED = 'AUTHENTICATION:REGISTER_FAILED',
    LOGOUT_REQUEST = 'AUTHENTICATION:LOGOUT_REQUEST',
    LOGOUT_SUCCESS = 'AUTHENTICATION:LOGOUT_SUCCESS',
    LOGOUT_FAILED = 'AUTHENTICATION:LOGOUT_FAILED'
}

export type UserAction =
    LoginRequestAction | LoginSuccessAction | LoginFailedAction
    | RegisterRequestAction | RegisterSuccessAction | RegisterFailedAction
    | LogoutRequestAction | LogoutSuccessAction | LogoutFailedAction;

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
