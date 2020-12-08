import {BirthDate, Gender, HashedPassword, Name, RegisterUserRequest} from '../../../.openapi';
import * as React                                                     from 'react';
import {FormInstance}                     from 'antd/lib/form';
import {WithTranslation, withTranslation} from 'react-i18next';
import {UserReducerProps}                 from '../../../store/reducers/user.reducers';
import {AppState}                                                     from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}                                 from 'redux';
import * as action                                                    from '../../../store/api/user.api';
import {connect}                                                      from 'react-redux';
import {Button, DatePicker, Form, Input, Space, Typography}           from 'antd';
import {UserOutlined}                                                 from '@ant-design/icons';
import SelectGender                                                   from '../../shared/user/gender/SelectGender';
import PasswordInput                                                  from '../../shared/user/password/PasswordInput';
import NameInput                                                      from '../../shared/user/name/NameInput';
import {fieldIsRequiredRule}                                          from '../../shared/user/form/rules';

const {Text} = Typography;

interface RegisterComponentProps extends WithTranslation<'default'>, UserReducerProps {
    dispatchRegister: (registerData: RegisterUserRequest) => void
}

interface RegisterComponentState {
    email: string | undefined;
    name: Name | undefined;
    password: HashedPassword | undefined,
    birthDate: BirthDate | undefined;
    gender: Gender | undefined;
}

class Register extends React.Component<RegisterComponentProps, RegisterComponentState> {
    formRef: React.RefObject<FormInstance>;

    constructor(props: RegisterComponentProps) {
        super(props);
        this.formRef = React.createRef<FormInstance>();
        this.state = {
            email: undefined,
            name: undefined,
            password: undefined,
            birthDate: undefined,
            gender: undefined
        };
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as RegisterComponentState);
    };

    _handleSubmit() {
        if (this.state.email !== undefined && this.state.name !== undefined && this.state.password !== undefined) {
            this.props.dispatchRegister({
                registerUser: {
                    name: this.state.name,
                    email: {emailAddress: this.state.email},
                    password: this.state.password,
                    gender: this.state.gender,
                    birthDate: this.state.birthDate
                }
            });
        }
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
                <NameInput onChange={name => this.setState({name: name})}/>
                <Form.Item
                    name="email"
                    rules={[fieldIsRequiredRule()]}>
                    <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                           type={'email'}
                           name="email"
                           placeholder={this.props.t('email')}
                           onChange={this.onChange}/>
                </Form.Item>
                <Form.Item
                    style={{marginBottom: 0}}>
                    <Form.Item
                        name="birthDate"
                        rules={[{required: true, message: 'Please input your Username!'}]}
                        style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                        <DatePicker
                            style={{width: '100%'}}
                            placeholder={this.props.t('birthDate')}
                            onChange={(value, dateString: string) => this.setState({birthDate: {birthDate: new Date(dateString)}})}/>
                    </Form.Item>
                    <span style={{display: 'inline-block', width: '12px', lineHeight: '32px', textAlign: 'center'}}>
                    </span>
                    <Form.Item
                        name="gender"
                        rules={[{required: true, message: 'Please input your Username!'}]}
                        style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                        <SelectGender onChange={gender => this.setState({gender: gender})}/>
                    </Form.Item>
                </Form.Item>
                <PasswordInput onChange={password => this.setState({password: password})}/>
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

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(Register));
