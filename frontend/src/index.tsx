import * as ReactDOM                  from 'react-dom';
import App                            from './App';
import React                          from 'react';
import {applyMiddleware, createStore} from 'redux';
import rootReducer                    from './reducers/root.reducers';
import {Provider}                     from 'react-redux';
import {composeWithDevTools}          from 'redux-devtools-extension';
import thunkMiddleware                from 'redux-thunk';
import logger                         from 'redux-logger';

require('dotenv').config()

const composeEnhancers = composeWithDevTools({
    // Specify name here, actionsBlacklist, actionsCreators and other options if needed
});

const persistedState = localStorage.getItem('reduxState')

let store = createStore(
    rootReducer,
    persistedState ? JSON.parse(persistedState) : {},
    composeEnhancers(applyMiddleware(
        thunkMiddleware,
        logger
    )));

store.subscribe(() => {
    localStorage.setItem('reduxState', JSON.stringify(store.getState()))
})

ReactDOM.render(
    <Provider store={store}>
        <App/>
    </Provider>, document.getElementById('root'));