import {Category}                                 from '../../.openapi/models';
import {CategoryAction, CategoryActionDefinition} from '../actions/category.actions';
import {ReducerState}                             from './reducers';
import {CategoryApi}                              from '../api/category.api';

export interface CategoryState extends ReducerState {
    categories: Category[];
}

export interface CategoryReducerProps extends CategoryApi {
    categoryState: CategoryState;
}

const initialState: CategoryState = {
    isLoading: false,
    error: undefined,
    categories: []
};

export const categoryReducer = (state: CategoryState = initialState, action: CategoryAction) => {
    switch (action.type) {
        case CategoryActionDefinition.LOAD_CATEGORIES_SUCCESS:
            return {...state, isLoading: false, error: undefined, categories: action.payload};
        case CategoryActionDefinition.CREATE_CATEGORY_SUCCESS:
        case CategoryActionDefinition.UPDATE_CATEGORY_SUCCESS:
            return {...state, isLoading: false, error: undefined};
        case CategoryActionDefinition.LOAD_CATEGORIES_REQUEST:
        case CategoryActionDefinition.CREATE_CATEGORY_REQUEST:
        case CategoryActionDefinition.UPDATE_CATEGORY_REQUEST:
            return {...state, isLoading: true, error: undefined};
        case CategoryActionDefinition.LOAD_CATEGORIES_FAILED:
        case CategoryActionDefinition.CREATE_CATEGORY_FAILED:
        case CategoryActionDefinition.UPDATE_CATEGORY_FAILED:
            return {...state, isLoading: false, error: action.payload};
        default:
            return state;
    }
};
