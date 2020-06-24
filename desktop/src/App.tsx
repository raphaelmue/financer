import * as React from 'react';
import 'antd/dist/antd.css';
import {HashRouter as Router, Route, Switch} from "react-router-dom";
import Register from "./views/Register";
import Login from "./views/Login";
import {configureI18N} from "./translations/translations";

configureI18N();

export default class App extends React.Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/register' component={Register}/>
                    <Route path='/' component={Login}/>
                </Switch>
            </Router>
        )
    }
}