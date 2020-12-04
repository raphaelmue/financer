import {ErrorMessage} from '../errorMessage';

export interface ReducerState {
    error: ErrorMessage | undefined;
    isLoading: boolean;
}
