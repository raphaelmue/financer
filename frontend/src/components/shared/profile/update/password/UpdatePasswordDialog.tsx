import {Form, Input, Modal, notification} from 'antd';
import React                              from 'react';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {connect}                          from 'react-redux';
import {withTranslation, WithTranslation} from 'react-i18next';
import {DataDialog}                       from '../../../form/modal/data/types';
import {HashedPassword, User}             from '../../../../../.openapi';
import {UserReducerProps}                 from '../../../../../store/reducers/user.reducers';
import {userDispatchMap}                  from '../../../../../store/api/user.api';
import PasswordInput                      from '../../../user/password/PasswordInput';
import {LockOutlined}                     from '@ant-design/icons';
import {fieldIsRequiredRule}              from '../../../user/form/rules';


interface UpdatePasswordDialogComponentProps extends DataDialog<User>, WithTranslation<'default'>, UserReducerProps {
}

interface UpdatePasswordDialogComponentState {
    password?: string,
    updatedPassword?: HashedPassword
    confirmLoading: boolean
}

class UpdatePasswordDialog extends React.Component<UpdatePasswordDialogComponentProps, UpdatePasswordDialogComponentState> {

    constructor(props: UpdatePasswordDialogComponentProps) {
        super(props);

        this.state = {
            confirmLoading: false,
        };
    }

    onSubmit() {
        this.setState({confirmLoading: true}, () => {
            this.updateProfile();
        });
    }

    updateProfile() {
        if (this.props.data && this.state.password && this.state.updatedPassword) {
            this.props.dispatchUpdateUsersPassword({
                userId: this.props.data.id,
                updatePassword: {
                    password: this.state.password,
                    updatedPassword: this.state.updatedPassword
                }
            }, (user) => {
                this.setState({confirmLoading: false});
                notification.success({
                    message: this.props.t('Menu.Profile'),
                    description: this.props.t('Message.Profile.User.UpdatedProfile')
                });
                if (this.props.onSubmit) {
                    this.props.onSubmit(user);
                }
            });
        }
    }

    onCancel() {
        if (this.props.onCancel) {
            this.props.onCancel();
        }
    }

    render() {
        return (
            <Modal
                centered
                title={this.props.t('Menu.Profile')}
                visible={this.props.visible}
                okText={this.props.t('Form.Button.Submit')}
                cancelText={this.props.t('Form.Button.Cancel')}
                confirmLoading={this.state.confirmLoading}
                onOk={this.onSubmit.bind(this)}
                onCancel={this.onCancel.bind(this)}>
                <Form
                    layout={'horizontal'}
                    name="updatePassword">

                    <Form.Item
                        name="currentPassword"
                        rules={[fieldIsRequiredRule()]}>
                        <Input.Password
                            prefix={<LockOutlined className="site-form-item-icon"/>}
                            name="currentPassword"
                            placeholder={this.props.t('Profile.User.CurrentPassword')?.toString()}
                            onChange={event => this.setState({password: event.target.value})}/>
                    </Form.Item>

                    <PasswordInput
                        onChange={password => this.setState({updatedPassword: password})}/>
                </Form>
            </Modal>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(UpdatePasswordDialog));
