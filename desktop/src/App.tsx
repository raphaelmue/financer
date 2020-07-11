import * as React                            from 'react';
import 'antd/dist/antd.css';
import {HashRouter as Router, Route, Switch} from 'react-router-dom';
import Register                              from './components/register/Register';
import Login                                 from './components/login/Login';
import {configureI18N}                       from './translations/translations';
import Home                                  from './components/home/Home';

configureI18N();

export default class App extends React.Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/register' component={Register}/>
                    <Route path='/login' component={Login}/>
                    <Route path='/' component={Home}/>
                </Switch>
            </Router>
        )
    }
}