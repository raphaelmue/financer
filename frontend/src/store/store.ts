import {composeWithDevTools}          from 'redux-devtools-extension';
import {applyMiddleware, createStore} from 'redux';
import appReducer                     from './reducers/root.reducers';
import thunkMiddleware                from 'redux-thunk';
import logger                         from 'redux-logger';
import i18next                        from 'i18next';

const composeEnhancers = composeWithDevTools({
    // Specify name here, actionsBlacklist, actionsCreators and other options if needed
});

const persistedState = localStorage.getItem('reduxState')

const store = createStore(
    appReducer,
    persistedState ? JSON.parse(persistedState) : {},
    composeEnhancers(applyMiddleware(
        thunkMiddleware,
        logger
    )));

store.subscribe(() => {
    localStorage.setItem('reduxState', JSON.stringify(store.getState()))
    i18next.changeLanguage(store.getState().user.user?.settings?.LANGUAGE?.value || 'en')
})

export default store;
