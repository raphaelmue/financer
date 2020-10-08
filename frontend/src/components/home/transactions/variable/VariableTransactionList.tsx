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
import {Link}                             from 'react-router-dom';


interface VariableTransactionListComponentProps extends WithTranslation, TransactionReducerProps, UserReducerProps {
}

interface VariableTransactionListComponentState {
}

class VariableTransactionList extends React.Component<VariableTransactionListComponentProps, VariableTransactionListComponentState> {

    constructor(props: VariableTransactionListComponentProps) {
        super(props);
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
        return (
            <ProTable<VariableTransaction>
                columns={columns()}
                dataSource={this.props.transactionState.variableTransactions}
                onLoad={() => this.loadVariableTransactions()}
                dateFormatter={'number'}
                locale={tableTranslations()}
                search={false}
                pagination={false}
                rowKey={'id'}
                loading={this.props.transactionState.isLoading}
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