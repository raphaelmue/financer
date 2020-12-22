import * as React                         from 'react';
import {Button, Descriptions, Result}     from 'antd';
import {WithTranslation, withTranslation} from 'react-i18next';
import {AppState}                         from '../../../store/reducers/root.reducers';
import {userDispatchMap}                  from '../../../store/api/user.api';
import {connect}                          from 'react-redux';
import {UserReducerProps}                 from '../../../store/reducers/user.reducers';
import {PageContainer}                    from '@ant-design/pro-layout';
import {DeleteOutlined, EditOutlined}     from '@ant-design/icons';
import ProCard                            from '@ant-design/pro-card';
import {Token, User}                      from '../../../.openapi';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './columns';
import {Link, RouteComponentProps}        from 'react-router-dom';
import ValueDateLabel                     from '../../shared/transaction/valueDate/valueDateLabel/ValueDateLabel';

const {Item} = Descriptions;

interface RouteProps {
    userId?: string
}

interface ProfileComponentProps extends RouteComponentProps<RouteProps>, WithTranslation<'default'>, UserReducerProps {
}

interface ProfileComponentState {
    user?: User,
    selectedTokenIds: number[]
}

class Profile extends React.Component<ProfileComponentProps, ProfileComponentState> {

    constructor(props: ProfileComponentProps) {
        super(props);
        this.state = {
            user: props.userState.user,
            selectedTokenIds: []
        };

        const {userId} = this.props.match.params;
        if (userId) {
            this.getUser(parseInt(userId));
        }
    }

    getUser(userId: number) {
        this.props.dispatchGetUser({userId: userId}, user => {
            this.setState({user: user});
        });
    }

    onSelectionChange(selectedRowKeys: React.ReactText[]) {
        this.setState({
            selectedTokenIds: selectedRowKeys.map(value => parseInt(value.toString()))
        });
    }

    onDeleteTokens() {
        this.state.selectedTokenIds.forEach(value => {
            if (this.state.user !== undefined) {
                this.props.dispatchDeleteToken({
                    userId: this.state.user?.id,
                    tokenId: value
                });
            }
        });
    }

    render() {
        if (!this.props.userState.isLoading && this.state.user === undefined) {
            return (
                <PageContainer>
                    <Result
                        status="404"
                        title="Not found"
                        subTitle="Sorry, the page you visited does not exist."
                        extra={<Link to={'/'}><Button type="primary">Back Home</Button></Link>}/>
                </PageContainer>);
        } else {
            if (this.state.user === undefined) {
                return (<PageContainer
                    loading={this.props.userState.isLoading}/>);
            } else {
                return (
                    <PageContainer
                        content={(
                            <Descriptions column={2}>
                                <Item label={this.props.t('Profile.User.Name')}>
                                    {this.state.user.name.firstName} {this.state.user.name.surname}
                                </Item>
                                <Item label={this.props.t('Profile.User.BirthDate')}>
                                    <ValueDateLabel
                                        valueDate={{date: this.state.user?.birthDate.birthDate || new Date()}}/>
                                </Item>
                                <Item label={this.props.t('Profile.User.Email')}>
                                    {this.state.user.email.emailAddress}
                                </Item>
                                <Item label={this.props.t('Profile.User.Gender.Gender')}>
                                    {this.props.t('Profile.User.Gender.' + this.state.user.gender.gender)}
                                </Item>
                            </Descriptions>)}
                        extra={
                            <Button icon={<EditOutlined/>}>
                                {this.props.t('Form.Button.Edit')}
                            </Button>}
                        tabList={[{
                            key: 'devicesTab',
                            tab: this.props.t('Profile.User.Devices')
                        }]}>
                        <ProCard>
                            <ProTable<Token>
                                rowSelection={{
                                    onChange: this.onSelectionChange.bind(this),
                                    selectedRowKeys: this.state.selectedTokenIds
                                }}
                                columns={columns()}
                                dataSource={this.state.user.tokens || []}
                                dateFormatter={'number'}
                                rowKey={'id'}
                                search={false}
                                pagination={false}
                                loading={false}
                                toolBarRender={() => [
                                    <Button
                                        key={'deleteProductButton'}
                                        id={'deleteProductButton'}
                                        style={{display: this.state.selectedTokenIds.length > 0 ? 'initial' : 'none'}}
                                        icon={<DeleteOutlined/>}
                                        onClick={() => this.onDeleteTokens()}
                                        danger>
                                        {this.props.t('Form.Button.Delete')}
                                    </Button>,
                                ]}/>
                        </ProCard>
                    </PageContainer>
                );
            }
        }
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Profile));
