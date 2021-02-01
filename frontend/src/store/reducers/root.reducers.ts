import {combineReducers}      from 'redux';
import userReducer            from './user.reducers';
import {transactionReducer}   from './transaction.reducer';
import {categoryReducer}      from './category.reducer';
import {UserActionDefinition} from '../actions/user.actions';
import {adminReducer}         from './admin.reducer';
import {statisticReducer}     from './statistic.reducer';

const appReducer = combineReducers({
    user: userReducer,
    transaction: transactionReducer,
    category: categoryReducer,
    admin: adminReducer,
    statistic: statisticReducer
});

const rootReducer = (state: any, action: any) => {
    if (action.type === (UserActionDefinition.LOGOUT_SUCCESS || UserActionDefinition.LOGOUT_FAILED)) {
        state = undefined;
    }
    return appReducer(state, action);
};

export type AppState = ReturnType<typeof rootReducer>

export default rootReducer;
