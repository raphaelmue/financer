import {ReducerState}                     from './reducers';
import {User}                             from '../../.openapi/models';
import {UserAction, UserActionDefinition} from '../actions/user.actions';
import {UserApi}                          from '../api/user.api';

export interface UserReducerState extends ReducerState {
    user: User | undefined;
}

export interface UserReducerProps extends UserApi {
    userState: UserReducerState
}

const initialState: UserReducerState = {
    user: undefined,
    error: undefined,
    isLoading: false
};

const userReducer = (state: UserReducerState = initialState, action: UserAction) => {
    switch (action.type) {
        case UserActionDefinition.LOGIN_SUCCESS:
            return {...state, isLoading: false, user: action.payload, error: undefined};
        case UserActionDefinition.REGISTER_SUCCESS:
            return {...state, isLoading: false, user: action.payload, error: undefined};
        case UserActionDefinition.LOGOUT_SUCCESS:
            return {...state, isLoading: false, user: undefined, error: undefined};
        case UserActionDefinition.UPDATE_USERS_SETTINGS_SUCCESS:
            return {...state, isLoading: false, user: action.payload, error: undefined};
        case UserActionDefinition.LOGIN_REQUEST:
        case UserActionDefinition.REGISTER_REQUEST:
        case UserActionDefinition.LOGOUT_REQUEST:
        case UserActionDefinition.UPDATE_USERS_SETTINGS_REQUEST:
            return {...state, isLoading: true};
        case UserActionDefinition.LOGIN_FAILED:
        case UserActionDefinition.REGISTER_FAILED:
        case UserActionDefinition.LOGOUT_FAILED:
        case UserActionDefinition.UPDATE_USERS_SETTINGS_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};

export default userReducer;
