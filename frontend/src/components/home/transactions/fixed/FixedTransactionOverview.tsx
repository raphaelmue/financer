import React                              from 'react';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {PageContainer}                    from '@ant-design/pro-layout';
import CategoryTree                       from '../../../shared/category/tree/CategoryTree';
import ProCard                            from '@ant-design/pro-card';
import {Button, Row}                      from 'antd';
import ProList                            from '@ant-design/pro-list';
import {FixedTransaction}                 from '../../../../.openapi';
import {transactionDispatchMap}           from '../../../../store/api/transaction.api';
import {UserReducerProps}                 from '../../../../store/reducers/user.reducers';
import {TransactionReducerProps}          from '../../../../store/reducers/transaction.reducer';
import {RequestData}                      from '@ant-design/pro-table';
import {metas}                            from './metas';
import {PlusOutlined}                     from '@ant-design/icons';
import {Link}                             from 'react-router-dom';

interface FixedTransactionOverviewComponentProps extends WithTranslation<'default'>, UserReducerProps, TransactionReducerProps {
}

interface FixedTransactionOverviewComponentState {
    selectedCategoryId: number | undefined,
    fixedTransactions: FixedTransaction[],
    page: number,
    pageSize: number
}

class FixedTransactionOverview extends React.Component<FixedTransactionOverviewComponentProps, FixedTransactionOverviewComponentState> {

    constructor(props: FixedTransactionOverviewComponentProps) {
        super(props);
        this.state = {
            selectedCategoryId: undefined,
            fixedTransactions: [],
            page: 0,
            pageSize: 20
        };
    }

    loadFixedTransactions(page = 0, pageSize = 20): Promise<RequestData<FixedTransaction>> {
        return new Promise<RequestData<FixedTransaction>>(resolve => {
            if (this.state.selectedCategoryId && this.props.userState.user?.id) {
                this.props.dispatchLoadFixedTransactions({
                    userId: this.props.userState.user.id,
                    categoryId: this.state.selectedCategoryId,
                    onlyActive: false,
                    page: page,
                    size: pageSize
                }, (fixedTransactions) => {
                    console.log(fixedTransactions);
                    this.setState({fixedTransactions: fixedTransactions});
                    resolve({
                        data: fixedTransactions,
                        success: true
                    });
                });
            }
        });
    }

    onSelectCategory(categoryId?: number) {
        if (categoryId) {
            this.setState({selectedCategoryId: categoryId}, () => this.loadFixedTransactions());
        }
    }

    render() {
        return (
            <PageContainer>
                <Row>
                    <ProCard colSpan={8} bordered>
                        <CategoryTree
                            onSelect={this.onSelectCategory.bind(this)}/>
                    </ProCard>
                    <ProCard colSpan={16} bordered>
                        <ProList<FixedTransaction>
                            headerTitle={this.props.t('Transaction.FixedTransactions')}
                            dataSource={this.state.fixedTransactions}
                            loading={this.props.transactionState.isLoading}
                            rowKey={'id'}
                            pagination={{
                                current: this.state.page + 1,
                                pageSize: this.state.pageSize,
                            }}
                            toolBarRender={() => [
                                <Link key={'createFixedTransactionLink'}
                                      to={'/transactions/fixed/create'}>
                                    <Button
                                        id={'newFixedTransactionButton'}
                                        key={'newFixedTransactionButton'}
                                        type="primary"
                                        icon={<PlusOutlined/>}>
                                        {this.props.t('Form.Button.New')}
                                    </Button>
                                </Link>]}
                            metas={metas()}
                            request={(params) => this.loadFixedTransactions(params.current, params.pageSize)}/>
                    </ProCard>
                </Row>
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

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation<'default'>()(FixedTransactionOverview));
