import {ReducerState} from './reducers';
import {User}         from '../.openapi/models';
import {LoginAction}  from '../actions/user.actions';

export interface UserReducerState extends ReducerState {
    user: User | null;
}

export interface UserReducerProps {
    user: UserReducerState
}

const initialState: UserReducerState = {
    user: null,
    error: '',
    isLoading: false
}

const userReducer = (state = initialState, action: LoginAction) => {
    switch (action.type) {
        case 'LOGIN_REQUEST':
            return {...state, isLoading: true}
        case 'LOGIN_SUCCESS':
            return {...state, isLoading: false, user: action.user}
        case 'LOGIN_FAILED':
            return {...state, isLoading: false, error: action.error}
        default:
            return state;
    }
}

export default userReducer;