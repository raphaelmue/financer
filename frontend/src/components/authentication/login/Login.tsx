import {withTranslation, WithTranslation} from 'react-i18next';
import {UserReducerProps}                 from '../../../store/reducers/user.reducers';
import {LoginUserRequest}                 from '../../../.openapi/apis';
import * as React                               from 'react';
import {Button, Form, Input, Space, Typography} from 'antd';
import {LockOutlined, UserOutlined}             from '@ant-design/icons';
import {AppState}                         from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import * as action                        from '../../../store/api/user.api';
import {connect}                          from 'react-redux';

const {Text} = Typography;

interface LoginComponentProps extends WithTranslation, UserReducerProps {
    login: (loginData: LoginUserRequest) => void
}

interface LoginComponentState {
    email: '';
    password: '';
    rememberMe: true;
}

class Login extends React.Component <LoginComponentProps, LoginComponentState> {

    handleChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as LoginComponentState);
    };

    _handleSubmit() {
        this.props.login({
            email: this.state.email,
            password: this.state.password
        });
    }

    render() {
        return (
            <Form
                layout={'horizontal'}
                name="login"
                className="login-form"
                onFinish={this._handleSubmit.bind(this)}>
                <Space direction={'vertical'} size={'middle'}>
                    <Text type={'danger'}>{this.props.userState.error?.message}</Text>
                </Space>
                <Form.Item
                    name="email"
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
                    <Input.Password
                        prefix={<LockOutlined className="site-form-item-icon"/>}
                        name="password"
                        placeholder={this.props.t('password')}
                        onChange={this.handleChange}/>
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" className="login-form-button"
                            loading={this.props.userState.isLoading}>
                        {this.props.t('login')}
                    </Button>
                </Form.Item>
            </Form>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    login: action.loginUser
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Login));
