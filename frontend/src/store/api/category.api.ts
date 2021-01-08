import {
    Category,
    CategoryApi as Api,
    CreateCategoryRequest, DeleteCategoryRequest,
    GetUsersCategoriesRequest,
    UpdateCategoryRequest,
    UserApi
}                                     from '../../.openapi';
import {bindActionCreators, Dispatch} from 'redux';
import {CategoryActionDefinition}     from '../actions/category.actions';
import {apiConfiguration}             from './index';
import {ErrorMessage}                 from '../errorMessage';

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

export const createCategory = (data: CreateCategoryRequest, callback?: (category: Category) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: CategoryActionDefinition.CREATE_CATEGORY_REQUEST,
            payload: data
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.createCategory(data)
            .then((category: Category) => {
                dispatch({
                    type: CategoryActionDefinition.CREATE_CATEGORY_SUCCESS,
                    payload: category
                });
                if (callback) callback(category);
            }), CategoryActionDefinition.CREATE_CATEGORY_FAILED, dispatch);
    };
};

export const updateCategory = (data: UpdateCategoryRequest, callback?: (category: Category) => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: CategoryActionDefinition.UPDATE_CATEGORY_REQUEST,
            payload: data
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.updateCategory(data)
            .then((category: Category) => {
                dispatch({
                    type: CategoryActionDefinition.UPDATE_CATEGORY_SUCCESS,
                    payload: category
                });
                if (callback) callback(category);
            }), CategoryActionDefinition.UPDATE_CATEGORY_FAILED, dispatch);
    };
};

export const deleteCategory = (data: DeleteCategoryRequest, callback?: () => void) => {
    return (dispatch: Dispatch) => {
        dispatch({
            type: CategoryActionDefinition.DELETE_CATEGORY_REQUEST,
            payload: data
        });
        const api = new Api(apiConfiguration());
        ErrorMessage.resolveError(api.deleteCategory(data)
            .then(() => {
                dispatch({
                    type: CategoryActionDefinition.DELETE_CATEGORY_SUCCESS,
                });
                if (callback) callback();
            }), CategoryActionDefinition.DELETE_CATEGORY_FAILED, dispatch);
    };
};

export interface CategoryApi {
    dispatchLoadCategories: (data: GetUsersCategoriesRequest, callback?: (categories: Category[]) => void) => void,
    dispatchCreateCategory: (data: CreateCategoryRequest, callback?: (category: Category) => void) => void,
    dispatchUpdateCategory: (data: UpdateCategoryRequest, callback?: (category: Category) => void) => void,
    dispatchDeleteCategory: (data: DeleteCategoryRequest, callback?: () => void) => void
}

export const categoryDispatchMap = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadCategories: loadCategories,
    dispatchCreateCategory: createCategory,
    dispatchUpdateCategory: updateCategory,
    dispatchDeleteCategory: deleteCategory
}, dispatch);
