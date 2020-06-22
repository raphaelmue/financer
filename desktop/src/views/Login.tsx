import * as React from "react";
import 'antd/dist/antd.css';
import {Button, Checkbox, Form, Input, Layout, Space, Typography} from "antd";
import {Link} from "react-router-dom";
import {FormInstance} from 'antd/lib/form';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {UserApi} from "../.openapi/";

const {Content} = Layout;
const {Title} = Typography;

interface LoginProps {
}

interface LoginState {
    email: '';
    password: '';
}

export default class Login extends React.Component<LoginProps, LoginState> {
    private formRef: React.RefObject<FormInstance>;

    constructor(props: object) {
        super(props);

        this.formRef = React.createRef<FormInstance>();
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as LoginState)
    }

    _handleLogin() {
        console.log(this.state);

        let api = new UserApi()
        api.loginUser(this.state.email, this.state.password)
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

                                <a className="login-form-forgot" href="">
                                    Forgot password
                                </a>
                            </Form.Item>

                            <Form.Item>
                                <Button type="primary" htmlType="submit" className="login-form-button">
                                    Log in
                                </Button>
                            </Form.Item>
                            <Form.Item>
                                Or <Link to={"/register"}>register now!</Link>
                            </Form.Item>
                        </Form>
                    </Space>
                </Content>
            </Layout>
        )
    }
}