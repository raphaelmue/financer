import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {TransactionReducerProps}      from '../../../../store/reducers/transaction.reducer';
import {UserReducerProps}             from '../../../../store/reducers/user.reducers';
import {VariableTransaction}          from '../../../../.openapi/models';
import ProTable                       from '@ant-design/pro-table';
import {columns}                      from './columns';
import {AppState}                     from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import {connect}                      from 'react-redux';
import * as api                       from '../../../../store/api/transaction.api';
import {Button}                       from 'antd';
import {PlusOutlined}                 from '@ant-design/icons';
import {Link, Redirect}               from 'react-router-dom';
import {PageContainer}                from '@ant-design/pro-layout';


interface VariableTransactionListComponentProps extends WithTranslation<'default'>, TransactionReducerProps, UserReducerProps {
}

interface VariableTransactionListComponentState {
    redirectToVariableTransactionDetails: number | undefined,
    page: number,
    pageSize: number
}

class VariableTransactionList extends React.Component<VariableTransactionListComponentProps, VariableTransactionListComponentState> {

    constructor(props: VariableTransactionListComponentProps) {
        super(props);
        this.state = {
            redirectToVariableTransactionDetails: undefined,
            page: 0,
            pageSize: 20
        };
        this.loadVariableTransactions();
    }

    loadVariableTransactions() {
        if (this.props.userState.user) {
            this.props.dispatchLoadVariableTransactions({
                userId: this.props.userState.user.id,
                page: this.state.page,
                size: this.state.pageSize
            });
        }
    }

    onPaginationChange(page: number, pageSize?: number) {
        this.setState({
            page: page - 1,
            pageSize: pageSize || 20
        }, () => this.loadVariableTransactions());
    }

    render() {
        if (this.state.redirectToVariableTransactionDetails !== undefined) {
            return <Redirect to={'/transactions/variable/' + this.state.redirectToVariableTransactionDetails}/>;
        }

        return (
            <PageContainer>
                <ProTable<VariableTransaction>
                    columns={columns()}
                    dataSource={this.props.transactionState.variableTransactions}
                    rowKey={'id'}
                    onLoad={() => this.loadVariableTransactions()}
                    dateFormatter={'number'}
                    search={false}
                    pagination={{
                        total: this.props.transactionState.pageMetadata?.totalElements || 0,
                        size: 'default',
                        current: this.state.page + 1,
                        pageSize: this.state.pageSize,
                        onChange: this.onPaginationChange.bind(this)
                    }}
                    loading={this.props.transactionState.isLoading}
                    onRow={(data) => {
                        return {
                            onClick: () => {
                                this.setState({redirectToVariableTransactionDetails: data.id});
                            }
                        };
                    }}
                    rowClassName={'cursor: pointer'}
                    toolBarRender={() => [
                        <Link key={"linkToCreateVariableTransaction"}
                              to={'/transactions/variable/create'}>
                            <Button
                                key="newVariableTransactionButton"
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
        userState: state.user,
        transactionState: state.transaction
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadVariableTransactions: api.loadVariableTransactions
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(VariableTransactionList));
