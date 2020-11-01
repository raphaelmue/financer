import {withTranslation, WithTranslation} from 'react-i18next';
import React                              from 'react';
import {TransactionReducerProps}          from '../../../../store/reducers/transaction.reducer';
import {UserReducerProps}                 from '../../../../store/reducers/user.reducers';
import {VariableTransaction}              from '../../../../.openapi/models';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './columns';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import * as api                           from '../../../../store/api/transaction.api';
import {tableTranslations}                from '../../../../translations/translations';
import {Button}                           from 'antd';
import {PlusOutlined}                     from '@ant-design/icons';
import {Link, Redirect}                   from 'react-router-dom';
import {PageContainer}                    from '@ant-design/pro-layout';


interface VariableTransactionListComponentProps extends WithTranslation, TransactionReducerProps, UserReducerProps {
}

interface VariableTransactionListComponentState {
    redirectToVariableTransactionDetails: number | undefined
}

class VariableTransactionList extends React.Component<VariableTransactionListComponentProps, VariableTransactionListComponentState> {

    constructor(props: VariableTransactionListComponentProps) {
        super(props);
        this.state = {
            redirectToVariableTransactionDetails: undefined
        };
        this.loadVariableTransactions();
    }

    loadVariableTransactions(page?: number) {
        if (this.props.userState.user) {
            this.props.dispatchLoadVariableTransactions({
                userId: this.props.userState.user.id,
                page: page
            });
        }
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
                    locale={tableTranslations()}
                    search={false}
                    pagination={false}
                    loading={this.props.transactionState.isLoading}
                    onRow={(data, index) => {
                        return {
                            onClick: () => {
                                this.setState({redirectToVariableTransactionDetails: data.id});
                            }
                        };
                    }}
                    rowClassName={'cursor: pointer;'}
                    toolBarRender={() => [
                        <Link to={'/transactions/variable/create'}>
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

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(VariableTransactionList));
