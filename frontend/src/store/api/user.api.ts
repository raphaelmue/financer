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
import {UserActionDefinition} from "../actions/user.actions";
import {ErrorMessage}         from "../errorMessage";

const api = new UserApi(apiConfiguration);

export const loginUser = (loginData: LoginUserRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: UserActionDefinition.LOGIN_REQUEST,
            payload: loginData
        });
        if (loginData && loginData.email && loginData.password) {
            api.loginUser(loginData)
                .then((user) => dispatch({
                    type: UserActionDefinition.LOGIN_SUCCESS,
                    payload: user
                }))
                .catch((reason) => dispatch({
                    type: UserActionDefinition.LOGIN_FAILED,
                    payload: ErrorMessage.createErrorMessage(reason)
                }))
        } else {
            dispatch({
                type: UserActionDefinition.LOGIN_FAILED,
                payload: "Error"
            })
        }
    }
}

export const registerUser = (registeringData: RegisterUserRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: UserActionDefinition.REGISTER_REQUEST,
            payload: registeringData
        });
        if (registeringData && registeringData.registerUser) {
            api.registerUser(registeringData)
                .then((user: User) => dispatch({
                    type: UserActionDefinition.REGISTER_SUCCESS,
                    payload: user
                }))
                .catch((reason) => dispatch({
                    type: UserActionDefinition.REGISTER_FAILED,
                    payload: ErrorMessage.createErrorMessage(reason)
                }))
        } else {
            dispatch({
                type: UserActionDefinition.REGISTER_FAILED,
                payload: "Error"
            })
        }
    }
}

export const logoutUser = (logoutUserData: DeleteTokenRequest) => {
    return (dispatch: Dispatch) => {
        logoutUserData.accept = DeleteTokenAcceptEnum.ApplicationJson
        dispatch({
            type: UserActionDefinition.LOGOUT_REQUEST,
            payload: logoutUserData
        });
        api.deleteToken(logoutUserData)
            .then(() => dispatch({
                type: UserActionDefinition.LOGOUT_SUCCESS
            }))
            .catch((reason) => dispatch({
                type: UserActionDefinition.LOGOUT_FAILED,
                payload: ErrorMessage.createErrorMessage(reason)
            }));
    }
}
