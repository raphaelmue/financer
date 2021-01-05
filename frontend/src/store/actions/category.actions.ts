import {Action}                                                                            from 'redux';
import {Category, CreateCategoryRequest, GetUsersCategoriesRequest, UpdateCategoryRequest} from '../../.openapi';
import {ErrorMessage}                                                                      from '../errorMessage';

export enum CategoryActionDefinition {
    LOAD_CATEGORIES_REQUEST = 'CATEGORIES:LOAD_CATEGORIES_REQUEST',
    LOAD_CATEGORIES_SUCCESS = 'CATEGORIES:LOAD_CATEGORIES_SUCCESS',
    LOAD_CATEGORIES_FAILED = 'CATEGORIES:LOAD_CATEGORIES_FAILED',
    CREATE_CATEGORY_REQUEST = 'CATEGORIES:CREATE_CATEGORY_REQUEST',
    CREATE_CATEGORY_SUCCESS = 'CATEGORIES:CREATE_CATEGORY_SUCCESS',
    CREATE_CATEGORY_FAILED = 'CATEGORIES:CREATE_CATEGORY_FAILED',
    UPDATE_CATEGORY_REQUEST = 'CATEGORIES:UPDATE_CATEGORY_REQUEST',
    UPDATE_CATEGORY_SUCCESS = 'CATEGORIES:UPDATE_CATEGORY_SUCCESS',
    UPDATE_CATEGORY_FAILED = 'CATEGORIES:UPDATE_CATEGORY_FAILED',
}

export type CategoryAction =
    LoadCategoriesRequestAction | LoadCategoriesSuccessAction | LoadCategoriesFailedAction
    | CreateCategoryRequestAction | CreateCategorySuccessAction | CreateCategoryFailedAction
    | UpdateCategoryRequestAction | UpdateCategorySuccessAction | UpdateCategoryFailedAction;

interface LoadCategoriesRequestAction extends Action {
    type: CategoryActionDefinition.LOAD_CATEGORIES_REQUEST,
    payload: GetUsersCategoriesRequest
}

interface LoadCategoriesSuccessAction extends Action {
    type: CategoryActionDefinition.LOAD_CATEGORIES_SUCCESS,
    payload: Category[]
}

interface LoadCategoriesFailedAction extends Action {
    type: CategoryActionDefinition.LOAD_CATEGORIES_FAILED,
    payload: ErrorMessage
}

interface CreateCategoryRequestAction extends Action {
    type: CategoryActionDefinition.CREATE_CATEGORY_REQUEST,
    payload: CreateCategoryRequest
}

interface CreateCategorySuccessAction extends Action {
    type: CategoryActionDefinition.CREATE_CATEGORY_SUCCESS,
    payload: Category
}

interface CreateCategoryFailedAction extends Action {
    type: CategoryActionDefinition.CREATE_CATEGORY_FAILED,
    payload: ErrorMessage
}

interface UpdateCategoryRequestAction extends Action {
    type: CategoryActionDefinition.UPDATE_CATEGORY_REQUEST,
    payload: UpdateCategoryRequest
}

interface UpdateCategorySuccessAction extends Action {
    type: CategoryActionDefinition.UPDATE_CATEGORY_SUCCESS,
    payload: Category
}

interface UpdateCategoryFailedAction extends Action {
    type: CategoryActionDefinition.UPDATE_CATEGORY_FAILED,
    payload: ErrorMessage
}
