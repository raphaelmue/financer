import * as React from "react";
import 'antd/dist/antd.css';
import {Button, Checkbox, Form, Input, Layout, message, Space, Typography} from "antd";
import {Link} from "react-router-dom";
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {UserApi} from '../.openapi'

const {Content} = Layout;
const {Title} = Typography;

interface LoginProps {
}

interface LoginState {
    email: '';
    password: '';
    errorMessage: ''
}

export default class Login extends React.Component<LoginProps, LoginState> {

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as LoginState)
    }

    _handleLogin() {
        let api = new UserApi()
        api.loginUser({
            email: this.state.email,
            password: this.state.password
        }).then(user => {
            console.log("User successfully logged in.", user);
        }).catch(error => {
            console.log(error)
            if (error.code == 403) {
                message.error("Invalid credentials");
            }
        }).finally(() => {

        });
    }

    render() {
        return (
            <Layout className="space-align-container">
                <Content className="space-align-block" style={{textAlign: 'center', padding: "25vh"}}>
                    <Space>
                        <Form
                            layout={"horizontal"}
                            name="normal_login"
                            className="login-form"
                            initialValues={{remember: true}}
                            style={{height: '100vh'}}
                            onFinish={this._handleLogin.bind(this)}>
                            <Form.Item>
                                <Typography>
                                    <Title>Financer</Title>
                                </Typography>
                            </Form.Item>
                            <Form.Item
                                name="username"
                                rules={[{required: true, message: 'Please input your Username!'}]}>
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                                       type={"email"}
                                       name="email"
                                       placeholder="E-Mail"
                                       onChange={this.onChange}/>
                            </Form.Item>
                            <Form.Item
                                name="password"
                                rules={[{required: true, message: 'Please input your Password!'}]}>
                                <Input
                                    prefix={<LockOutlined className="site-form-item-icon"/>}
                                    type="password"
                                    name="password"
                                    placeholder="Password"
                                    onChange={this.onChange}/>
                            </Form.Item>
                            <Form.Item>
                                <Form.Item name="remember" valuePropName="checked" noStyle>
                                    <Checkbox>Remember me</Checkbox>
                                </Form.Item>

                                <Link className="login-form-forgot" to={"/"}>
                                    Forgot password
                                </Link>
                            </Form.Item>

                            <Form.Item>
                                <Button type="primary" htmlType="submit" className="login-form-button">
                                    login
                                </Button>
                            </Form.Item>
                            <Form.Item>
                                <Link to={"/register"}>register</Link>
                            </Form.Item>
                        </Form>
                    </Space>
                </Content>
            </Layout>
        )
    }
}