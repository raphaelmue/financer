import {combineReducers}    from 'redux';
import userReducer          from './user.reducers';
import {transactionReducer} from './transaction.reducer';
import {categoryReducer}    from './category.reducer';

const rootReducer = combineReducers({
    user: userReducer,
    transaction: transactionReducer,
    category: categoryReducer
});

export type AppState = ReturnType<typeof rootReducer>

export default rootReducer;
