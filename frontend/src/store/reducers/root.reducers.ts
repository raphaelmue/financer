import {combineReducers}    from 'redux';
import userReducer          from './user.reducers';
import {transactionReducer} from './transaction.reducer';

const rootReducer = combineReducers({
    user: userReducer,
    transaction: transactionReducer
});

export type AppState = ReturnType<typeof rootReducer>

export default rootReducer;