import {BirthDate, Gender, Name}                from '../../../.openapi/models';
import * as React                               from 'react';
import {FormInstance}                           from 'antd/lib/form';
import {withTranslation, WithTranslation}       from 'react-i18next';
import {UserReducerProps}                       from '../../../store/reducers/user.reducers';
import {RegisterUserRequest}                    from '../../../.openapi/apis';
import {AppState}                               from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}           from 'redux';
import * as action                              from '../../../store/api/user.api';
import {connect}                                from 'react-redux';
import {Button, Form, Input, Space, Typography} from 'antd';
import {LockOutlined, UserOutlined}             from '@ant-design/icons';

const {Text} = Typography;

interface RegisterComponentProps extends WithTranslation, UserReducerProps {
    dispatchRegister: (registerData: RegisterUserRequest) => void
}

interface RegisterComponentState {
    email: '';
    name: Name;
    password: '';
    repeatedPassword: '';
    birthDate: BirthDate;
    gender: Gender
}

class Register extends React.Component<RegisterComponentProps, RegisterComponentState> {
    private formRef: React.RefObject<FormInstance>;

    constructor(props: RegisterComponentProps) {
        super(props);
        this.formRef = React.createRef<FormInstance>();
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as RegisterComponentState);
    };

    _handleSubmit() {

    }

    render() {
        return (
            <Form
                layout={'horizontal'}
                name="normal_login"
                className="login-form"
                onFinish={this._handleSubmit.bind(this)}>
                <Space direction={'vertical'} size={'middle'}>
                    <Text type={'danger'}>{this.props.userState.error?.message}</Text>
                </Space>
                <Form.Item
                    name="name"
                    style={{marginBottom: 0}}>
                    <Form.Item
                        name="name"
                        rules={[{required: true, message: 'Please input your Username!'}]}
                        style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                        <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                               type={'text'}
                               name="firstName"
                               placeholder={this.props.t('firstName')}
                               onChange={this.onChange}/>
                    </Form.Item>
                    <span style={{display: 'inline-block', width: '12px', lineHeight: '32px', textAlign: 'center'}}>
                    </span>
                    <Form.Item
                        name="surname"
                        rules={[{required: true, message: 'Please input your Username!'}]}
                        style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                        <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                               type={'text'}
                               name="surname"
                               placeholder={this.props.t('surname')}
                               onChange={this.onChange}/>
                    </Form.Item>
                </Form.Item>
                <Form.Item
                    name="email"
                    rules={[{required: true, message: 'Please input your Username!'}]}>
                    <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                           type={'email'}
                           name="email"
                           placeholder={this.props.t('email')}
                           onChange={this.onChange}/>
                </Form.Item>
                <Form.Item
                    name="password"
                    rules={[{required: true, message: 'Please input your Password!'}]}>
                    <Input
                        prefix={<LockOutlined className="site-form-item-icon"/>}
                        type="password"
                        name="password"
                        placeholder={this.props.t('password')}
                        onChange={this.onChange}/>
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" className="login-form-button"
                            loading={this.props.userState.isLoading}>
                        {this.props.t('register')}
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
    dispatchRegister: action.registerUser
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Register));
