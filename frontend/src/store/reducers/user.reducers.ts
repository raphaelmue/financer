import {ReducerState}                     from './reducers';
import {User}                             from '../../.openapi';
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
        case UserActionDefinition.UPDATE_USERS_PASSWORD_SUCCESS:
            return {...state, isLoading: false, error: undefined};
        case UserActionDefinition.GET_USER_SUCCESS:
            if (action.payload.id === state.user?.id) {
                return {...state, isLoading: false, error: undefined, user: action.payload};
            }
            return {...state, isLoading: false, error: undefined};
        case UserActionDefinition.UPDATE_USERS_SETTINGS_SUCCESS:
            return {...state, isLoading: false, user: action.payload, error: undefined};
        case UserActionDefinition.UPDATE_USERS_DATA_SUCCESS:
            if (action.payload.id === state.user?.id) {
                return {...state, isLoading: false, error: undefined, user: action.payload};
            }
            return {...state, isLoading: false, error: undefined};
        case UserActionDefinition.LOGIN_REQUEST:
        case UserActionDefinition.REGISTER_REQUEST:
        case UserActionDefinition.LOGOUT_REQUEST:
        case UserActionDefinition.GET_USER_REQUEST:
        case UserActionDefinition.UPDATE_USERS_PASSWORD_REQUEST:
        case UserActionDefinition.UPDATE_USERS_SETTINGS_REQUEST:
        case UserActionDefinition.UPDATE_USERS_DATA_REQUEST:
            return {...state, isLoading: true};
        case UserActionDefinition.LOGIN_FAILED:
        case UserActionDefinition.REGISTER_FAILED:
        case UserActionDefinition.LOGOUT_FAILED:
        case UserActionDefinition.GET_USER_FAILED:
        case UserActionDefinition.UPDATE_USERS_PASSWORD_FAILED:
        case UserActionDefinition.UPDATE_USERS_SETTINGS_FAILED:
        case UserActionDefinition.UPDATE_USERS_DATA_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};

export default userReducer;
