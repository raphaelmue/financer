import {withTranslation, WithTranslation} from 'react-i18next';
import React                              from 'react';
import {User}                             from '../../../../.openapi';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './columns';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {connect}                          from 'react-redux';
import {Button}                           from 'antd';
import {PlusOutlined}                     from '@ant-design/icons';
import {Link, Redirect}                   from 'react-router-dom';
import {PageContainer}                    from '@ant-design/pro-layout';
import {adminDispatchMap}                 from '../../../../store/api/admin.api';
import {AdminReducerProps}                from '../../../../store/reducers/admin.reducer';


interface UserManagementComponentProps extends WithTranslation<'default'>, AdminReducerProps {
}

interface UserManagementComponentState {
    users: User[],
    page: number,
    pageSize: number
    redirectToUserDetails: number | undefined,
}

class UserManagement extends React.Component<UserManagementComponentProps, UserManagementComponentState> {

    constructor(props: UserManagementComponentProps) {
        super(props);
        this.state = {
            users: [],
            page: 0,
            pageSize: 20,
            redirectToUserDetails: undefined
        };
        this.loadUsers();
    }

    loadUsers() {
        this.props.dispatchLoadUsers({
            page: this.state.page,
            size: this.state.pageSize
        }, (users) => {
            this.setState({users: users});
        });
    }

    onPaginationChange(page: number, pageSize?: number) {
        this.setState({
            page: page - 1,
            pageSize: pageSize || 20
        }, () => this.loadUsers());
    }

    render() {
        if (this.state.redirectToUserDetails !== undefined) {
            return <Redirect to={'/profile/' + this.state.redirectToUserDetails}/>;
        }

        return (
            <PageContainer>
                <ProTable<User>
                    columns={columns()}
                    dataSource={this.state.users}
                    rowKey={'id'}
                    onLoad={() => this.loadUsers()}
                    dateFormatter={'number'}
                    search={false}
                    pagination={{
                        total: this.props.adminState.pageMetadata?.totalElements || 0,
                        size: 'default',
                        current: this.state.page + 1,
                        pageSize: this.state.pageSize,
                        onChange: this.onPaginationChange.bind(this)
                    }}
                    loading={this.props.adminState.isLoading}
                    onRow={(data) => {
                        return {
                            onClick: () => {
                                this.setState({redirectToUserDetails: data.id});
                            }
                        };
                    }}
                    rowClassName={'cursor: pointer'}
                    toolBarRender={() => [
                        <Link key={'linkToCreateVariableTransaction'}
                              to={'/transactions/variable/create'}>
                            <Button
                                id={'createVariableTransactionButton'}
                                key={'createVariableTransactionButton'}
                                type="primary"
                                icon={<PlusOutlined/>}>
                                {this.props.t('Form.Button.New')}
                            </Button>
                        </Link>
                    ]}>
                </ProTable>
            </PageContainer>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        adminState: state.admin
    };
};

export default connect(mapStateToProps, adminDispatchMap)(withTranslation<'default'>()(UserManagement));
