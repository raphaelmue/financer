import {DatePicker, Form, Modal, notification} from 'antd';
import React                                   from 'react';
import {AppState}                              from '../../../../store/reducers/root.reducers';
import {connect}                               from 'react-redux';
import {withTranslation, WithTranslation}      from 'react-i18next';
import {DataDialog}                            from '../../form/modal/data/types';
import {BirthDate, Gender, Name, User}         from '../../../../.openapi';
import {UserReducerProps}                      from '../../../../store/reducers/user.reducers';
import {userDispatchMap}                       from '../../../../store/api/user.api';
import NameInput                               from '../../user/name/NameInput';
import SelectGender                            from '../../user/gender/SelectGender';
import moment                                  from 'moment';


interface UpdateProfileDialogComponentProps extends DataDialog<User>, WithTranslation<'default'>, UserReducerProps {
}

interface UpdateProfileDialogComponentState {
    name?: Name,
    birthDate?: BirthDate,
    gender?: Gender,
    confirmLoading: boolean
}

class UpdateProfileDialog extends React.Component<UpdateProfileDialogComponentProps, UpdateProfileDialogComponentState> {

    constructor(props: UpdateProfileDialogComponentProps) {
        super(props);

        this.state = {
            name: this.props.data?.name,
            birthDate: {birthDate: new Date(this.props.data?.birthDate.birthDate || '')},
            gender: this.props.data?.gender,
            confirmLoading: false,
        };
    }

    onSubmit() {
        this.setState({confirmLoading: true}, () => {
            this.updateProfile();
        });
    }

    updateProfile() {
        if (this.props.data) {
            this.props.dispatchUpdateUsersData({
                userId: this.props.data.id,
                updatePersonalInformation: {
                    name: this.state.name,
                    birthDate: this.state.birthDate,
                    gender: this.state.gender
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
                    name="updateUserData">
                    <NameInput name={this.state.name}
                               onChange={name => this.setState({name: name})}/>
                    <Form.Item
                        style={{marginBottom: 0}}>
                        <Form.Item
                            name="birthDate"
                            rules={[{required: true, message: 'Please input your Username!'}]}
                            style={{display: 'inline-block', width: 'calc(50% - 6px)'}}
                            initialValue={moment(this.state.birthDate?.birthDate)}>
                            <DatePicker
                                style={{width: '100%'}}
                                placeholder={this.props.t('birthDate')?.toString()}
                                onChange={(value, dateString: string) => this.setState({birthDate: {birthDate: new Date(dateString)}})}/>
                        </Form.Item>
                        <span
                            style={{display: 'inline-block', width: '12px', lineHeight: '32px', textAlign: 'center'}}/>
                        <Form.Item
                            name="gender"
                            rules={[{required: true, message: 'Please input your Username!'}]}
                            style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                            <SelectGender gender={this.state.gender}
                                          onChange={gender => this.setState({gender: gender})}/>
                        </Form.Item>
                    </Form.Item>
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

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(UpdateProfileDialog));
