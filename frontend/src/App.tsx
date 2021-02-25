import * as React                            from 'react';
import 'antd/dist/antd.css';
import {HashRouter as Router, Route, Switch} from 'react-router-dom';
import Authentication                        from './components/authentication/Authentication';
import {configureI18N}                       from './translations/translations';
import Home                                  from './components/home/Home';
import Landing                               from './components/landing/Landing';

configureI18N();

export default class App extends React.Component {
    render(): JSX.Element {
        return (
            <Router>
                <Switch>
                    <Route path={'/landing'} component={Landing}/>
                    <Route path='/authentication' component={Authentication}/>
                    <Route path='/' component={Home}/>
                </Switch>
            </Router>
        );
    }
}
