import {withTranslation, WithTranslation} from 'react-i18next';
import React                              from 'react';
import {TransactionReducerProps}          from '../../../../../store/reducers/transaction.reducer';
import {VariableTransaction}              from '../../../../../.openapi';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './columns';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {connect}                          from 'react-redux';
import {Button, Space}                    from 'antd';
import {PlusOutlined}                     from '@ant-design/icons';
import {Link, Redirect}                   from 'react-router-dom';
import {transactionDispatchMap}           from '../../../../../store/api/transaction.api';
import {UserState}                        from '../../../../../store/reducers/user.reducers';


interface VariableTransactionListComponentProps extends WithTranslation<'default'>, TransactionReducerProps {
    simpleView?: boolean,
    userState: UserState
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
            pageSize: props.simpleView ? 5 : 20
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
            <ProTable<VariableTransaction>
                columns={columns()}
                dataSource={this.props.transactionState.variableTransactions}
                rowKey={'id'}
                onLoad={() => this.loadVariableTransactions()}
                dateFormatter={'number'}
                headerTitle={this.props.simpleView ? false : undefined}
                options={this.props.simpleView ? false : {}}
                search={false}
                pagination={this.props.simpleView ? false : {
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
                toolBarRender={this.props.simpleView ? undefined : () => [
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
                ]}
                rowClassName={'cursor: pointer'}>
            </ProTable>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
        ,
        transactionState: state.transaction
    };
};

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation<'default'>()(VariableTransactionList));
