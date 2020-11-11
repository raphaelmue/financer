import * as ReactDOM      from 'react-dom';
import App                from './App';
import React              from 'react';
import {Provider}         from 'react-redux';
import store              from './store/store';
import {ConfigProvider}                       from 'antd';
import {getCurrentIntlType, getCurrentLocale} from './translations/translations';
import {IntlProvider}                         from '@ant-design/pro-table';

require('dotenv').config();

ReactDOM.render(
    <Provider store={store}>
        <ConfigProvider locale={getCurrentLocale()}>
            <IntlProvider value={{intl: getCurrentIntlType()}}>
                <App/>
            </IntlProvider>
        </ConfigProvider>
    </Provider>, document.getElementById('root'));
