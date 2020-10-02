import {composeWithDevTools}          from "redux-devtools-extension";
import {applyMiddleware, createStore} from "redux";
import rootReducer                    from "./reducers/root.reducers";
import thunkMiddleware                from "redux-thunk";
import logger                         from "redux-logger";

const composeEnhancers = composeWithDevTools({
    // Specify name here, actionsBlacklist, actionsCreators and other options if needed
});

const persistedState = localStorage.getItem('reduxState')

const store = createStore(
    rootReducer,
    persistedState ? JSON.parse(persistedState) : {},
    composeEnhancers(applyMiddleware(
        thunkMiddleware,
        logger
    )));

store.subscribe(() => {
    localStorage.setItem('reduxState', JSON.stringify(store.getState()))
})

export default store;
