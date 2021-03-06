import React                                                      from 'react';
import {withTranslation, WithTranslation}                         from 'react-i18next';
import {AppState}                                                 from '../../../../../store/reducers/root.reducers';
import {connect}                                                  from 'react-redux';
import {transactionDispatchMap}                                   from '../../../../../store/api/transaction.api';
import {Button, Descriptions, Modal, notification, Result, Space} from 'antd';
import Text                                                       from 'antd/lib/typography/Text';
import {PageContainer}                                            from '@ant-design/pro-layout';
import {Amount, FixedTransaction, FixedTransactionAmount}         from '../../../../../.openapi';
import {Link, Redirect, RouteComponentProps}                      from 'react-router-dom';
import {TransactionReducerProps}                                  from '../../../../../store/reducers/transaction.reducer';
import TimeRangeLabel
                                                                  from '../../../../shared/transaction/timeRange/TimeRangeLabel';
import {DeleteOutlined, EditOutlined}                             from '@ant-design/icons';
import FixedTransactionStatusTag
                                                                  from '../../../../shared/transaction/fixed/status/FixedTransactionStatusTag';
import AmountStatistics
                                                                  from '../../../../shared/transaction/amount/amountStatistics/AmountStatistics';
import i18next                                                    from 'i18next';
import ProCard                                                    from '@ant-design/pro-card';
import FixedTransactionAmountList
                                                                  from '../../../../shared/transaction/fixed/transactionAmounts/FixedTransactionAmountList';
import CreateFixedTransactionAmountDialog
                                                                  from '../../../../shared/transaction/fixed/transactionAmounts/create/CreateFixedTransactionAmountDialog';
import AttachmentList
                                                                  from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                                                  from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';
import {confirmDialogConfig}                                      from '../../../../shared/form/modal/confirm/config';
import UpdateFixedTransactionDialog
                                                                  from '../../../../shared/transaction/fixed/update/UpdateFixedTransactionDialog';
import FixedTransactionAmountHistoryChart
                                                                  from '../../../../shared/statistics/fixedTransaction/fixedTransactionAmount/FixedTransactionAmountHistoryChart';

const {Item} = Descriptions;

interface RouteProps {
    fixedTransactionId?: string
}

interface FixedTransactionDetailsComponentProps extends RouteComponentProps<RouteProps>, WithTranslation<'default'>, TransactionReducerProps {
}

interface FixedTransactionDetailsComponentState {
    fixedTransaction?: FixedTransaction,
    activeTab: string,
    showFixedTransactionAmountDialog: boolean,
    showAttachmentDialog: boolean,
    showUpdateFixedTransactionDialog: boolean,
    redirectToFixedTransactionOverview: boolean
}

class FixedTransactionDetails extends React.Component<FixedTransactionDetailsComponentProps, FixedTransactionDetailsComponentState> {

    constructor(props: FixedTransactionDetailsComponentProps) {
        super(props);

        this.state = {
            fixedTransaction: undefined,
            activeTab: 'fixedTransactionAmountsTab',
            showFixedTransactionAmountDialog: false,
            showAttachmentDialog: false,
            showUpdateFixedTransactionDialog: false,
            redirectToFixedTransactionOverview: false
        };
    }


    componentDidMount() {
        const {fixedTransactionId} = this.props.match.params;

        if (fixedTransactionId) {
            console.log(fixedTransactionId);
            this.props.dispatchLoadFixedTransaction({transactionId: parseInt(fixedTransactionId)},
                fixedTransaction => {
                    this.setState({
                        fixedTransaction: fixedTransaction,
                        activeTab: fixedTransaction.hasVariableAmounts ? 'fixedTransactionAmountsTab' : 'attachmentsTab'
                    });
                });
        }
    }

    onDeleteFixedTransaction() {
        Modal.confirm(confirmDialogConfig(
            this.props.t('Transaction.VariableTransaction')?.toString() || '',
            this.props.t('Message.Transaction.VariableTransaction.ConfirmDeleteVariableTransaction')?.toString() || '',
            () => new Promise<void>(resolve => {
                if (this.state.fixedTransaction) {
                    this.props.dispatchDeleteFixedTransaction({transactionId: this.state.fixedTransaction.id}, () => {
                        this.setState({redirectToFixedTransactionOverview: true}, () => {
                            notification.success({
                                message: this.props.t('Transaction.FixedTransactions'),
                                description: this.props.t('Message.Transaction.FixedTransaction.DeletedFixedTransaction')
                            });
                            resolve();
                        });
                    });
                }
            }))
        );
    }

    onCreateFixedTransactionAmount = (fixedTransactionAmount: FixedTransactionAmount) => new Promise<void>(resolve => {
        if (this.state.fixedTransaction !== undefined)
            this.props.dispatchCreateFixedTransactionAmount({
                transactionId: this.state.fixedTransaction.id,
                createFixedTransactionAmount: fixedTransactionAmount
            }, (fixedTransactionAmount) => {
                if (this.state.fixedTransaction !== undefined) {
                    this.setState({
                        fixedTransaction: {
                            ...this.state.fixedTransaction,
                            transactionAmounts: [...this.state.fixedTransaction.transactionAmounts, fixedTransactionAmount]
                        }, showFixedTransactionAmountDialog: false
                    }, () => {
                        notification.success({
                            message: this.props.t('Transaction.FixedTransactionAmounts'),
                            description: this.props.t('Message.Transaction.FixedTransaction.CreatedFixedTransactionAmount')
                        });
                        resolve();
                    });
                }
            });
    });

    onDeleteFixedTransactionAmounts = (fixedTransactionAmountIds: number[]) => new Promise<void>((resolveConfirm => Modal.confirm(confirmDialogConfig(
        this.props.t('Transaction.FixedTransactionAmounts')?.toString() || '',
        this.props.t('Message.Transaction.FixedTransaction.FixedTransactionAmount.ConfirmDeleteFixedTransactionAmounts')?.toString() || '',
        () => new Promise<void>(resolveDispatch => {
            if (this.state.fixedTransaction?.id) {
                this.props.dispatchDeleteFixedTransactionAmounts({
                    transactionId: this.state.fixedTransaction.id,
                    fixedTransactionAmountIds: fixedTransactionAmountIds
                }, () => {
                    const fixedTransaction = this.state.fixedTransaction;
                    if (fixedTransaction) {
                        fixedTransaction.transactionAmounts = fixedTransaction.transactionAmounts.filter(
                            (fixedTransactionAmount) => fixedTransactionAmountIds.filter(id => id === fixedTransactionAmount.id).length <= 0);
                        this.setState({fixedTransaction: fixedTransaction});
                        notification.success({
                            message: this.props.t('Transaction.FixedTransactionAmounts'),
                            description: this.props.t('Message.Transaction.FixedTransaction.FixedTransactionAmount.DeletedFixedTransactionAmounts')
                        });
                        resolveDispatch();
                        resolveConfirm();
                    }
                });
            }
        })
    ))));

    getAmount(): Amount {
        if (this.state.fixedTransaction) {
            if (this.state.fixedTransaction.hasVariableAmounts) {
                if (this.state.fixedTransaction.transactionAmounts.length > 0) {
                    return this.state.fixedTransaction.transactionAmounts[0].amount;
                }
            } else {
                return this.state.fixedTransaction.amount;
            }
        }
        return {amount: 0};
    }

    render() {
        if (this.state.redirectToFixedTransactionOverview) {
            return <Redirect to={'/transactions/fixed'}/>;
        }

        if (!this.props.transactionState.isLoading && this.state.fixedTransaction === undefined) {
            return (
                <PageContainer>
                    <Result
                        status="404"
                        title="Not found"
                        subTitle="Sorry, the page you visited does not exist."
                        extra={<Link to={'/'}><Button type="primary">Back Home</Button></Link>}/>)
                </PageContainer>);
        } else {
            if (this.state.fixedTransaction === undefined) {
                return <PageContainer loading/>;
            } else {
                return (
                    <PageContainer
                        content={
                            <Descriptions column={2}>
                                <Item label={this.props.t('Transaction.Category.Name')}>
                                    <Text strong={true}>{this.state.fixedTransaction.category.name}</Text>
                                </Item>
                                <Item label={this.props.t('Transaction.FixedTransaction.Product')}>
                                    {this.state.fixedTransaction.product}
                                </Item>
                                <Item label={this.props.t('Transaction.ValueDate')}>
                                    <Text strong={true}>
                                        <TimeRangeLabel timeRange={this.state.fixedTransaction.timeRange}/>
                                    </Text>
                                </Item>
                                <Item label={this.props.t('Transaction.Vendor')}>
                                    {this.state.fixedTransaction.vendor}
                                </Item>
                                <Item label={this.props.t('Transaction.FixedTransaction.Status')}>
                                    <FixedTransactionStatusTag
                                        isActive={this.state.fixedTransaction.active}
                                        hasVariableAmounts={this.state.fixedTransaction.hasVariableAmounts}/>
                                </Item>
                                <Item label={this.props.t('Transaction.Description')}>
                                    {this.state.fixedTransaction.description}
                                </Item>
                            </Descriptions>}
                        extra={
                            <Space size={'small'}>
                                <Button id={'editFixedTransaction'}
                                        icon={<EditOutlined/>}
                                        onClick={() => this.setState({showUpdateFixedTransactionDialog: true})}>
                                    {this.props.t('Form.Button.Edit')}
                                </Button>
                                <Button id={'deleteFixedTransaction'}
                                        danger
                                        type={'primary'}
                                        icon={<DeleteOutlined/>}
                                        onClick={() => this.onDeleteFixedTransaction()}>
                                    {this.props.t('Form.Button.Delete')}
                                </Button>
                            </Space>}
                        extraContent={
                            <AmountStatistics data={this.getAmount()}/>}
                        tabList={[{
                            tab: i18next.t('Transaction.FixedTransactionAmounts'),
                            disabled: !this.state.fixedTransaction.hasVariableAmounts,
                            key: 'fixedTransactionAmountsTab',
                        }, {
                            tab: i18next.t('Transaction.Attachments'),
                            key: 'attachmentsTab',
                        }, {
                            tab: this.props.t('Menu.Statistics'),
                            disabled: !this.state.fixedTransaction.hasVariableAmounts,
                            key: 'statisticTab'
                        }]}
                        tabActiveKey={this.state.activeTab}
                        onTabChange={activeKey => (this.setState({activeTab: activeKey}))}>

                        <ProCard collapsed={(this.state.activeTab !== 'fixedTransactionAmountsTab')}>
                            <FixedTransactionAmountList
                                fixedTransactionAmounts={this.state.fixedTransaction?.transactionAmounts || []}
                                openFixedTransactionAmountDialog={() => this.setState({showFixedTransactionAmountDialog: true})}
                                onDeleteFixedTransactionAmounts={this.onDeleteFixedTransactionAmounts}/>
                            <CreateFixedTransactionAmountDialog
                                visible={this.state.showFixedTransactionAmountDialog}
                                onSubmit={this.onCreateFixedTransactionAmount}
                                onCancel={() => this.setState({showFixedTransactionAmountDialog: false})}/>
                        </ProCard>

                        <ProCard collapsed={(this.state.activeTab !== 'attachmentsTab')}>
                            <AttachmentList
                                attachments={this.state.fixedTransaction.attachments || []}
                                openAttachmentDialog={() => this.setState({showAttachmentDialog: true})}/>
                            <CreateAttachmentDialog
                                visible={this.state?.showAttachmentDialog || false}
                                onCancel={() => this.setState({showAttachmentDialog: false})}
                                // onSubmit={(attachment: UploadFile[]) => this.setState({
                                //     attachments: this.state.attachments.concat(attachment),
                                //     showAttachmentDialog: false
                                // })}
                            />
                        </ProCard>

                        <ProCard collapsed={(this.state.activeTab !== 'statisticTab')}>
                            <FixedTransactionAmountHistoryChart
                                data={this.state.fixedTransaction.transactionAmounts}/>
                        </ProCard>

                        <UpdateFixedTransactionDialog
                            visible={this.state.showUpdateFixedTransactionDialog}
                            onSubmit={data => this.setState({
                                showUpdateFixedTransactionDialog: false,
                                fixedTransaction: data
                            })}
                            onCancel={() => this.setState({showUpdateFixedTransactionDialog: false})}
                            data={this.state.fixedTransaction}/>
                    </PageContainer>
                );
            }
        }
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user,
        transactionState: state.transaction
    };
};

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation<'default'>()(FixedTransactionDetails));
