import {GetUsersCategoriesRequest, UserApi} from '../../.openapi/apis';
import {Dispatch}                           from 'redux';
import {CategoryActionDefinition}           from '../actions/category.actions';
import {apiConfiguration}                   from './index';
import {ErrorMessage}                       from '../errorMessage';
import {Category}                           from '../../.openapi/models';

export const loadCategories = (data: GetUsersCategoriesRequest) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: CategoryActionDefinition.LOAD_CATEGORIES_REQUEST,
            payload: data
        });
        let api = new UserApi(apiConfiguration());
        ErrorMessage.resolveError(api.getUsersCategories(data)
            .then((categories: Category[]) => dispatch({
                type: CategoryActionDefinition.LOAD_CATEGORIES_SUCCESS,
                payload: categories
            })), CategoryActionDefinition.LOAD_CATEGORIES_FAILED, dispatch);
    };
};

export interface CategoryApi {
    dispatchLoadCategories: (data: GetUsersCategoriesRequest) => void
}
