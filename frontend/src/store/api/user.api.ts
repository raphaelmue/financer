import {bindActionCreators, Dispatch} from 'redux';
import {
    DeleteTokenRequest,
    LoginUserRequest,
    RegisterUserRequest,
    UpdateUsersSettingsRequest,
    User,
    UserApi as Api
}                                     from '../../.openapi';
import {apiConfiguration}             from './index';
import {UserActionDefinition}         from '../actions/user.actions';
import {ErrorMessage}                 from '../errorMessage';


export const loginUser = (loginData: LoginUserRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: UserActionDefinition.LOGIN_REQUEST,
            payload: loginData
        });
        if (loginData && loginData.email && loginData.password) {
            const api = new Api(apiConfiguration());
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
            const api = new Api(apiConfiguration());
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
        dispatch({
            type: UserActionDefinition.LOGOUT_REQUEST,
            payload: logoutUserData
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.deleteToken(logoutUserData)
            .then(() => dispatch({
                type: UserActionDefinition.LOGOUT_SUCCESS
            })), UserActionDefinition.REGISTER_FAILED, dispatch);
    };
};

export const updateUsersSettings = (data: UpdateUsersSettingsRequest, callback?: (user: User) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: UserActionDefinition.UPDATE_USERS_SETTINGS_REQUEST,
            payload: data
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.updateUsersSettings(data)
            .then((user) => {
                dispatch({
                    type: UserActionDefinition.UPDATE_USERS_SETTINGS_SUCCESS,
                    payload: user
                });
                if (callback) callback(user);
            }), UserActionDefinition.UPDATE_USERS_SETTINGS_FAILED, dispatch);
    };
};

export interface UserApi {
    dispatchLoginUser: (loginData: LoginUserRequest) => void,
    dispatchRegisterUser: (registeringData: RegisterUserRequest) => void,
    dispatchLogoutUser: (logoutUserData: DeleteTokenRequest) => void,
    dispatchUpdateUsersSettings: (data: UpdateUsersSettingsRequest, callback?: (user: User) => void) => void
}

export const userDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoginUser: loginUser,
    dispatchRegisterUser: registerUser,
    dispatchLogoutUser: logoutUser,
    dispatchUpdateUsersSettings: updateUsersSettings
}, dispatch);
