import {Dispatch}                                from 'redux';
import {loginFailed, loginRequest, loginSuccess} from '../actions/user.actions';
import {LoginUserRequest, UserApi}               from '../.openapi';
import {apiConfiguration}                        from './index';

const api = new UserApi(apiConfiguration);
console.log(apiConfiguration.basePath)

export const loginUser = (loginData: LoginUserRequest) => {
    return (dispatch: Dispatch) => {
        dispatch(loginRequest(loginData));
        if (loginData && loginData.email && loginData.password) {
            api.loginUser(loginData)
                .then((user) => dispatch(loginSuccess(user)))
                .catch((reason) => handleError(reason, dispatch))
        } else {
            dispatch(loginFailed('Error'))
        }
    }
}

function handleError(reason: any, dispatch: Dispatch) {
    console.log('reason', reason)
    if (reason === 'TypeError: Failed to Fetch') {
        dispatch(loginFailed('Cannot connect to server.'));
        return;
    }
    switch (reason.status) {
        case 403:
            dispatch(loginFailed('You are not allowed to perform this action.'));
            break;
        default:
            dispatch(loginFailed('An unexpected error occurred. Please contact the support for more information.'))
    }
}