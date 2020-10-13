import {Action}                    from 'redux';
import {GetUsersCategoriesRequest} from '../../.openapi/apis';
import {Category}                  from '../../.openapi/models';
import {ErrorMessage}              from '../errorMessage';

export enum CategoryActionDefinition {
    LOAD_CATEGORIES_REQUEST = 'CATEGORIES:LOAD_CATEGORIES_REQUEST',
    LOAD_CATEGORIES_SUCCESS = 'CATEGORIES:LOAD_CATEGORIES_SUCCESS',
    LOAD_CATEGORIES_FAILED = 'CATEGORIES:LOAD_CATEGORIES_FAILED',
}

export type CategoryAction =
    LoadCategoriesRequestAction | LoadCategoriesSuccessAction | LoadCategoriesFailedAction

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
