import {Dispatch}             from 'redux';
import {
    DeleteTokenAcceptEnum,
    DeleteTokenRequest,
    LoginUserRequest,
    RegisterUserRequest,
    User,
    UserApi
}                             from '../../.openapi';
import {apiConfiguration}     from './index';
import {UserActionDefinition} from '../actions/user.actions';
import {ErrorMessage}         from '../errorMessage';


export const loginUser = (loginData: LoginUserRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: UserActionDefinition.LOGIN_REQUEST,
            payload: loginData
        });
        if (loginData && loginData.email && loginData.password) {
            const api = new UserApi(apiConfiguration());
            ErrorMessage.resolveError(api.loginUser(loginData)
                .then((user: User) => dispatch({
                    type: UserActionDefinition.LOGIN_SUCCESS,
                    payload: user
                })), UserActionDefinition.REGISTER_FAILED, dispatch);
        }
    };
};

export const registerUser = (registeringData: RegisterUserRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: UserActionDefinition.REGISTER_REQUEST,
            payload: registeringData
        });
        if (registeringData && registeringData.registerUser) {
            const api = new UserApi(apiConfiguration());
            ErrorMessage.resolveError(api.registerUser(registeringData)
                .then((user: User) => dispatch({
                    type: UserActionDefinition.REGISTER_SUCCESS,
                    payload: user
                })), UserActionDefinition.REGISTER_FAILED, dispatch);
        }
    };
};

export const logoutUser = (logoutUserData: DeleteTokenRequest) => {
    return (dispatch: Dispatch) => {
        logoutUserData.accept = DeleteTokenAcceptEnum.ApplicationJson;
        dispatch({
            type: UserActionDefinition.LOGOUT_REQUEST,
            payload: logoutUserData
        });
        const api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.deleteToken(logoutUserData)
            .then(() => dispatch({
                type: UserActionDefinition.LOGOUT_SUCCESS
            })), UserActionDefinition.REGISTER_FAILED, dispatch);
    };
};
