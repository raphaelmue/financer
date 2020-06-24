import * as React from "react";
import 'antd/dist/antd.css';
import {Layout} from "antd";

interface HomeProps {
}

interface HomeState {
    email: '';
    password: '';
    errorMessage: ''
}

export default class Home extends React.Component<HomeProps, HomeState> {

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as HomeState)
    }

    render() {
        return (
            <Layout>
            </Layout>
        )
    }
}