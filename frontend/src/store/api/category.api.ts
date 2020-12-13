import {Category, GetUsersCategoriesRequest, UserApi} from '../../.openapi';
import {Dispatch}                                     from 'redux';
import {CategoryActionDefinition}                     from '../actions/category.actions';
import {apiConfiguration}                             from './index';
import {ErrorMessage}                                 from '../errorMessage';

export const loadCategories = (data: GetUsersCategoriesRequest, callback?: (categories: Category[]) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: CategoryActionDefinition.LOAD_CATEGORIES_REQUEST,
            payload: data
        });
        const api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersCategories(data)
            .then((categories: Category[]) => {
                dispatch({
                    type: CategoryActionDefinition.LOAD_CATEGORIES_SUCCESS,
                    payload: categories
                });
                if (callback) callback(categories);
            }), CategoryActionDefinition.LOAD_CATEGORIES_FAILED, dispatch);
    };
};

export interface CategoryApi {
    dispatchLoadCategories: (data: GetUsersCategoriesRequest, callback?: (categories: Category[]) => void) => void
}
