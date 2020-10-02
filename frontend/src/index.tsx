import * as ReactDOM from 'react-dom';
import App           from './App';
import React         from 'react';
import {Provider}    from 'react-redux';
import store         from "./store/store";

require('dotenv').config()

ReactDOM.render(
    <Provider store={store}>
        <App/>
    </Provider>, document.getElementById('root'));
