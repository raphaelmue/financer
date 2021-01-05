import * as React                                                 from 'react';
import {Button, Descriptions, notification, Result, Space, Tag}   from 'antd';
import {WithTranslation, withTranslation}                         from 'react-i18next';
import {AppState}                                                 from '../../../store/reducers/root.reducers';
import {userDispatchMap}                                          from '../../../store/api/user.api';
import {connect}                                                  from 'react-redux';
import {UserReducerProps}                                         from '../../../store/reducers/user.reducers';
import {PageContainer}                                            from '@ant-design/pro-layout';
import {DeleteOutlined, EditOutlined, LockOutlined, PlusOutlined} from '@ant-design/icons';
import ProCard                                                    from '@ant-design/pro-card';
import {Category, Token, User}                                    from '../../../.openapi';
import ProTable                                                   from '@ant-design/pro-table';
import {columns}                                                  from './columns';
import {Link, RouteComponentProps}                                from 'react-router-dom';
import ValueDateLabel
                                                                  from '../../shared/transaction/valueDate/valueDateLabel/ValueDateLabel';
import UpdateProfileDialog
                                                                  from '../../shared/profile/update/UpdateProfileDialog';
import UpdatePasswordDialog
                                                                  from '../../shared/profile/update/password/UpdatePasswordDialog';
import CategoryTree                                               from '../../shared/category/tree/CategoryTree';
import CreateOrUpdateCategoryDialog
                                                                  from '../../shared/category/createOrUpdate/CreateOrUpdateCategoryDialog';
import CategoryUtil                                               from '../../shared/category/util';
import {CategoryReducerProps}                                     from '../../../store/reducers/category.reducer';

const {Item} = Descriptions;

interface RouteProps {
    userId?: string
}

interface ProfileComponentProps extends RouteComponentProps<RouteProps>, WithTranslation<'default'>, UserReducerProps, CategoryReducerProps {
}

interface ProfileComponentState {
    user?: User,
    activeTab: string,
    selectedCategoryId?: number
    selectedTokenIds: number[],
    showCategoryDialog: boolean,
    showUpdateProfileDialog: boolean,
    showUpdatePasswordDialog: boolean
}

class Profile extends React.Component<ProfileComponentProps, ProfileComponentState> {

    constructor(props: ProfileComponentProps) {
        super(props);
        this.state = {
            user: props.userState.user,
            activeTab: 'categoriesTab',
            selectedTokenIds: [],
            showCategoryDialog: false,
            showUpdateProfileDialog: false,
            showUpdatePasswordDialog: false
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

    createOrUpdateCategory(category: Category): Promise<void> {
        return new Promise<void>(resolve => {
            if (category.id < 0) {
                this.props.dispatchCreateCategory({
                    createCategory: {
                        name: category.name,
                        categoryClass: category.categoryClass,
                        parentId: category.parentId
                    }
                }, () => {
                    notification.success({
                        message: this.props.t('Menu.Categories'),
                        description: this.props.t('Message.Category.CreatedCategory')
                    });
                    resolve();
                });
            } else {
                this.props.dispatchUpdateCategory({
                    categoryId: category.id,
                    updateCategory: {
                        name: category.name,
                        categoryClass: category.categoryClass,
                        parentId: category.parentId
                    }
                }, () => {
                    notification.success({
                        message: this.props.t('Menu.Categories'),
                        description: this.props.t('Message.Category.CreatedCategory')
                    });
                    resolve();
                });
            }
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

    getSelectedCategory(): Category | undefined {
        if (this.state.selectedCategoryId) {
            return CategoryUtil.getCategoryById(this.props.categoryState.categories, this.state.selectedCategoryId);
        }
        return undefined;
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
                                    <Space>
                                        {this.state.user.email.emailAddress}
                                        {this.state.user.verified ?
                                            <Tag color={'success'}>{this.props.t('Profile.User.Verified')}</Tag> :
                                            <Tag color={'warning'}>{this.props.t('Profile.User.NotVerified')}</Tag>}
                                    </Space>
                                </Item>
                                <Item label={this.props.t('Profile.User.Gender.Gender')}>
                                    {this.props.t('Profile.User.Gender.' + this.state.user.gender.gender)}
                                </Item>
                            </Descriptions>)}
                        extra={
                            <Space>
                                <Button id={'updateProfileButton'}
                                        icon={<EditOutlined/>}
                                        onClick={() => this.setState({showUpdateProfileDialog: true})}>
                                    {this.props.t('Form.Button.Edit')}
                                </Button>
                                <Button id={'updatePasswordButton'}
                                        icon={<LockOutlined/>}
                                        onClick={() => this.setState({showUpdatePasswordDialog: true})}>
                                    {this.props.t('Form.Button.Profile.User.UpdatePassword')}
                                </Button>
                            </Space>}
                        tabList={[{
                            key: 'categoriesTab',
                            tab: this.props.t('Menu.Categories')
                        }, {
                            key: 'devicesTab',
                            tab: this.props.t('Profile.User.Devices')
                        }]}
                        tabActiveKey={this.state.activeTab}
                        onTabChange={activeKey => this.setState({activeTab: activeKey})}>
                        <ProCard collapsed={!(this.state.activeTab === 'devicesTab')}>
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

                        <ProCard
                            title={this.props.t('Menu.Categories')}
                            extra={
                                <Space>
                                    <Button id={'editCategory'}
                                            disabled={this.state.selectedCategoryId === undefined}
                                            icon={<EditOutlined/>}
                                            onClick={() => this.setState({showCategoryDialog: true})}>
                                        {this.props.t('Form.Button.Edit')}
                                    </Button>
                                    <Button id={'deleteCategory'}
                                            disabled={this.state.selectedCategoryId === undefined}
                                            danger
                                            type={'primary'}
                                            icon={<DeleteOutlined/>}>
                                        {this.props.t('Form.Button.Delete')}
                                    </Button>
                                    <Button id={'createCategory'}
                                            type={'primary'}
                                            icon={<PlusOutlined/>}
                                            onClick={() => this.setState({showCategoryDialog: true})}>
                                        {this.props.t('Form.Button.New')}
                                    </Button>
                                </Space>}
                            collapsed={!(this.state.activeTab === 'categoriesTab')}>

                            <CategoryTree
                                onSelect={categoryId => this.setState({selectedCategoryId: categoryId})}/>

                        </ProCard>

                        <UpdateProfileDialog
                            data={this.state.user}
                            visible={this.state.showUpdateProfileDialog}
                            onSubmit={data => this.setState({user: data, showUpdateProfileDialog: false})}
                            onCancel={() => this.setState({showUpdateProfileDialog: false})}/>

                        <UpdatePasswordDialog
                            data={this.state.user}
                            visible={this.state.showUpdatePasswordDialog}
                            onSubmit={data => this.setState({user: data, showUpdatePasswordDialog: false})}
                            onCancel={() => this.setState({showUpdatePasswordDialog: false})}/>

                        <CreateOrUpdateCategoryDialog
                            data={this.getSelectedCategory()}
                            visible={this.state.showCategoryDialog}
                            onSubmit={this.createOrUpdateCategory}
                            onCancel={() => this.setState({showCategoryDialog: false})}/>


                    </PageContainer>
                );
            }
        }
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user,
        categoryState: state.category
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Profile));
