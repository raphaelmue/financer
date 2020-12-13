import * as React                            from 'react';
import 'antd/dist/antd.css';
import {HashRouter as Router, Route, Switch} from 'react-router-dom';
import Authentication                        from './components/authentication/Authentication';
import {configureI18N}                       from './translations/translations';
import Home                                  from './components/home/Home';

configureI18N();

export default class App extends React.Component {
    render(): JSX.Element {
        return (
            <Router>
                <Switch>
                    <Route path='/authentication' component={Authentication}/>
                    <Route path='/' component={Home}/>
                </Switch>
            </Router>
        );
    }
}
