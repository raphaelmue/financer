import * as React from 'react';
import 'antd/dist/antd.css';
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import Login from "./views/Login";
import {render} from "react-dom";

export default class App extends React.Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' component={Login}/>
                </Switch>
            </Router>
        )
    }
}

render(<App />, document.getElementById('root'));
