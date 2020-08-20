import {LoginUserRequest} from '../.openapi/apis';
import {User}             from '../.openapi/models';

export type LoginAction =
    | { type: 'LOGIN_REQUEST'; input: LoginUserRequest }
    | { type: 'LOGIN_SUCCESS'; user: User }
    | { type: 'LOGIN_FAILED'; error: string };

// action creators
export function loginRequest(input: LoginUserRequest): LoginAction {
    return {type: 'LOGIN_REQUEST', input};
}

export function loginSuccess(user: User): LoginAction {
    return {type: 'LOGIN_SUCCESS', user};
}

export function loginFailed(error: string): LoginAction {
    console.error(error)
    return {type: 'LOGIN_FAILED', error};
}