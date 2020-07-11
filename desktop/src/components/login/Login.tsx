import * as React                                                 from 'react';
import 'antd/dist/antd.css';
import {Button, Checkbox, Form, Input, Layout, Space, Typography} from 'antd';
import {Link, Redirect}                                           from 'react-router-dom';
import {LockOutlined, UserOutlined}                               from '@ant-design/icons';
import * as action                                                from '../../api/user.api';
import {connect}                                                  from 'react-redux';
import {AppState}                                                 from '../../reducers/root.reducers';
import {UserReducerProps}                                         from '../../reducers/user.reducers';
import {bindActionCreators, Dispatch}                             from 'redux';
import {LoginUserRequest}                                         from '../../.openapi/apis';
import {WithTranslation, withTranslation}                         from 'react-i18next';

const {Content} = Layout;
const {Title, Text} = Typography;

interface LoginComponentProps extends WithTranslation, UserReducerProps {
    login: (loginData: LoginUserRequest) => void
}

interface LoginComponentState {
    email: '';
    password: '';
    rememberMe: true;
}

class Login extends React.Component<LoginComponentProps, LoginComponentState> {

    handleChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as LoginComponentState)
    }

    _handleSubmit() {
        this.props.login({
            email: this.state.email,
            password: this.state.password
        })
    }

    render() {
        if (this.props.user.user) {
            return <Redirect to={'/'}/>
        }

        return (
            <Layout className="space-align-container">
                <Content className="space-align-block" style={{textAlign: 'center', padding: '25vh'}}>
                    <Space>
                        <Form
                            layout={'horizontal'}
                            name="normal_login"
                            className="login-form"
                            onFinish={this._handleSubmit.bind(this)}>
                            <Form.Item>
                                <Typography>
                                    <Title>Financer</Title>
                                </Typography>
                            </Form.Item>
                            <Space direction={'vertical'} size={'middle'}>
                                <Text type={'danger'}>{this.props.user.error}</Text>
                            </Space>
                            <Form.Item
                                name="username"
                                rules={[{required: true, message: 'Please input your Username!'}]}>
                                <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                                       type={'email'}
                                       name="email"
                                       placeholder={this.props.t('email')}
                                       onChange={this.handleChange}/>
                            </Form.Item>
                            <Form.Item
                                name="password"
                                rules={[{required: true, message: 'Please input your Password!'}]}>
                                <Input
                                    prefix={<LockOutlined className="site-form-item-icon"/>}
                                    type="password"
                                    name="password"
                                    placeholder={this.props.t('password')}
                                    onChange={this.handleChange}/>
                            </Form.Item>
                            <Form.Item>
                                <Form.Item name="remember" valuePropName="checked" noStyle>
                                    <Checkbox>{this.props.t('rememberMe')}</Checkbox>
                                </Form.Item>
                            </Form.Item>

                            <Form.Item>
                                <Button type="primary" htmlType="submit" className="login-form-button"
                                        loading={this.props.user.isLoading}>
                                    {this.props.t('login')}
                                </Button>
                            </Form.Item>
                            <Form.Item>
                                <Link to={'/register'}>{this.props.t('register')}</Link>
                            </Form.Item>
                        </Form>
                    </Space>
                </Content>
            </Layout>
        )
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        user: state.user
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    login: action.loginUser
}, dispatch)

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Login))