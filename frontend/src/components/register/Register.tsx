import * as React                                       from 'react';
import 'antd/dist/antd.css';
import {Button, Form, Input, Layout, Space, Typography} from 'antd';
import {Link}                                           from 'react-router-dom';
import {FormInstance}                                   from 'antd/lib/form';
import {LockOutlined, UserOutlined}                     from '@ant-design/icons';
import {BirthDate, Gender, Name}                        from '../../.openapi';

const {Content} = Layout;
const {Title, Text} = Typography;

interface RegisterProps {
}

interface RegisterState {
    email: '';
    name: Name;
    password: '';
    repeatedPassword: '';
    birthDate: BirthDate;
    gender: Gender
}

export default class Register extends React.Component<RegisterProps, RegisterState> {
    private formRef: React.RefObject<FormInstance>;

    constructor(props: object) {
        super(props);

        this.formRef = React.createRef<FormInstance>();
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as RegisterState)
    }

    _handleLogin() {
        console.log(this.state);
    }

    render() {
        return (
            <Layout className="space-align-container">
                <Content className="space-align-block" style={{textAlign: 'center', padding: '25vh'}}>
                    <Space>
                        <Form
                            layout={'horizontal'}
                            name="normal_login"
                            className="login-form"
                            initialValues={{remember: true}}
                            style={{height: '100vh'}}
                            onFinish={this._handleLogin.bind(this)}>
                            <Form.Item>
                                <Typography>
                                    <Title>Financer</Title>
                                    <Text>Register here</Text>
                                </Typography>
                            </Form.Item>
                            <Form.Item
                                rules={[{required: true, message: 'Please input your Username!'}]}>
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                                       type={'text'}
                                       name="firstName"
                                       placeholder="First Name"
                                       onChange={this.onChange}
                                       style={{display: 'inline-block', width: 'calc(50% - 8px)', margin: '0 8px'}}/>
                            </Form.Item>
                            <Form.Item
                                rules={[{required: true, message: 'Please input your email address!'}]}>
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                                       type={'text'}
                                       name="surname"
                                       placeholder="Surname"
                                       onChange={this.onChange}
                                       style={{display: 'inline-block', width: 'calc(50% - 8px)', margin: '0 8px'}}/>
                            </Form.Item>
                            <Form.Item
                                rules={[{required: true, message: 'Please input your Username!'}]}>
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                                       type={'email'}
                                       name="email"
                                       placeholder="E-Mail"
                                       onChange={this.onChange}/>
                            </Form.Item>
                            <Form.Item
                                rules={[{required: true, message: 'Please input your Password!'}]}>
                                <Input
                                    prefix={<LockOutlined className="site-form-item-icon"/>}
                                    type="password"
                                    name="password"
                                    placeholder="Password"
                                    onChange={this.onChange}/>
                            </Form.Item>
                            <Form.Item
                                rules={[{required: true, message: 'Please input your Password!'}]}>
                                <Input
                                    prefix={<LockOutlined className="site-form-item-icon"/>}
                                    type="password"
                                    name="repeatedPassword"
                                    placeholder="Repeated Password"
                                    onChange={this.onChange}/>
                            </Form.Item>
                            <Form.Item
                                name="birthDate"
                                rules={[{required: true, message: 'Please input your Password!'}]}>
                                <Input
                                    prefix={<LockOutlined className="site-form-item-icon"/>}
                                    type="date"
                                    name="birthDate"
                                    placeholder="Birth Date"
                                    onChange={this.onChange}/>
                            </Form.Item>

                            <Form.Item>
                                <Button type="primary" htmlType="submit" className="login-form-button">
                                    Register
                                </Button>
                            </Form.Item>
                            <Form.Item>
                                Or <Link to={'/'}>Login</Link>
                            </Form.Item>
                        </Form>
                    </Space>
                </Content>
            </Layout>
        )
    }
}