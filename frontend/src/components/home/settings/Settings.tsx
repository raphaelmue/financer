import {withTranslation, WithTranslation}     from 'react-i18next';
import React                                  from 'react';
import {PageContainer}                        from '@ant-design/pro-layout';
import ProCard                                from '@ant-design/pro-card';
import {Divider, Form, Modal, Select, Switch} from 'antd';
import {fieldIsRequiredRule}                  from '../../shared/user/form/rules';
import {UserReducerProps}                     from '../../../store/reducers/user.reducers';
import {AppState}                             from '../../../store/reducers/root.reducers';
import {connect}                              from 'react-redux';
import {userDispatchMap}                      from '../../../store/api/user.api';
import {confirmDialogConfig}                  from '../../shared/form/modal/confirm/config';

const {Option} = Select;

interface SettingsComponentProps extends WithTranslation<'default'>, UserReducerProps {
}

interface SettingsComponentState {
    language: string,
    currency: string
}

class Settings extends React.Component<SettingsComponentProps, SettingsComponentState> {

    constructor(props: SettingsComponentProps) {
        super(props);
    }

    updateUsersSettings(changedSettings: { language?: string, currency?: string, theme?: string, changeAmountSignAutomatically?: boolean }): void {
        if (this.props.userState.user?.id) {
            let reloadNecessary = false;
            let settings: { [key: string]: string; } = {};
            if (changedSettings.language) {
                settings = {...settings, LANGUAGE: changedSettings.language};
                reloadNecessary = true;
            }
            if (changedSettings.currency) {
                settings = {...settings, CURRENCY: changedSettings.currency};
            }
            if (changedSettings.theme) {
                settings = {...settings, THEME: changedSettings.theme};
                reloadNecessary = true;
            }
            if (changedSettings.changeAmountSignAutomatically !== undefined) {
                settings = {
                    ...settings,
                    CHANGE_AMOUNT_SIGN_AUTOMATICALLY: changedSettings.changeAmountSignAutomatically.toString()
                };
            }
            this.props.dispatchUpdateUsersSettings({
                userId: this.props.userState.user.id,
                updateSettings: {settings: settings}
            }, () => {
                if (reloadNecessary) this.openReloadPageDialog();
            });
        }
    }

    openReloadPageDialog() {
        Modal.confirm(confirmDialogConfig(
            this.props.t('Menu.Settings')?.toString() || '',
            this.props.t('Message.Profile.Settings.ConfirmReload')?.toString() || '',
            () => new Promise(resolve => {
                window.location.reload();
                resolve();
            })
        ));
    }

    render() {
        return (
            <PageContainer loading={this.props.userState.isLoading}>
                <ProCard bordered>
                    <Form name={'settings'}
                          labelAlign={'left'} labelCol={{span: 6}}
                          wrapperCol={{span: 16}}>
                        <Divider orientation={'left'}>{this.props.t('Profile.Settings.General')}</Divider>
                        <Form.Item
                            name={'language'}
                            label={this.props.t('Profile.Settings.Language')}
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.props.userState.user?.settings?.LANGUAGE?.value}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option?.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                                onChange={value => this.updateUsersSettings({language: value.toString()})}>
                                <Option value={'en'}>English</Option>
                                <Option value={'de'}>Deutsch</Option>
                            </Select>
                        </Form.Item>
                        <Form.Item
                            name={'currency'}
                            label={this.props.t('Profile.Settings.Currency')}
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.props.userState.user?.settings?.CURRENCY?.value}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option?.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                                onChange={value => this.updateUsersSettings({currency: value.toString()})}>
                                <Option value={'EUR'}>EUR</Option>
                                <Option value={'USD'}>USD</Option>
                            </Select>
                        </Form.Item>
                        <Form.Item
                            name={'changeAmountSignAutomatically'}
                            label={this.props.t('Profile.Settings.ChangeAmountSignAutomatically')}
                            rules={[fieldIsRequiredRule()]}>
                            <Switch
                                checked={(this.props.userState.user?.settings?.CHANGE_AMOUNT_SIGN_AUTOMATICALLY?.value === 'true')}
                                onChange={checked => this.updateUsersSettings({changeAmountSignAutomatically: checked})}/>
                        </Form.Item>

                        <Divider orientation={'left'}>{this.props.t('Profile.Settings.Appearance')}</Divider>
                        <Form.Item
                            name={'theme'}
                            label={this.props.t('Profile.Settings.Theme')}
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.props.userState.user?.settings?.THEME?.value || 'light'}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option?.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                                onChange={value => this.updateUsersSettings({theme: value.toString()})}>
                                <Option value={'light'}>{this.props.t('Profile.Settings.LightTheme')}</Option>
                                <Option value={'dark'}>{this.props.t('Profile.Settings.DarkTheme')}</Option>
                            </Select>
                        </Form.Item>
                    </Form>
                </ProCard>
            </PageContainer>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Settings));
