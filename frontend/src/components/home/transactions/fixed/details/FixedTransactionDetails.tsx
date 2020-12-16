import React                                               from 'react';
import {withTranslation, WithTranslation}                  from 'react-i18next';
import {AppState}                                          from '../../../../../store/reducers/root.reducers';
import {connect}                                           from 'react-redux';
import {transactionDispatchMap}                            from '../../../../../store/api/transaction.api';
import {Button, Descriptions, notification, Result, Space} from 'antd';
import Text                                                from 'antd/lib/typography/Text';
import {PageContainer}                                     from '@ant-design/pro-layout';
import {FixedTransaction, FixedTransactionAmount}          from '../../../../../.openapi';
import {Link, Redirect, RouteComponentProps}               from 'react-router-dom';
import {TransactionReducerProps}                           from '../../../../../store/reducers/transaction.reducer';
import TimeRangeLabel
                                                           from '../../../../shared/transaction/timeRange/TimeRangeLabel';
import {DeleteOutlined, EditOutlined}                      from '@ant-design/icons';
import FixedTransactionStatusTag
                                                           from '../../../../shared/transaction/fixed/status/FixedTransactionStatusTag';
import AmountStatistics
                                                           from '../../../../shared/transaction/amount/amountStatistics/AmountStatistics';
import i18next                                             from 'i18next';
import ProCard                                             from '@ant-design/pro-card';
import FixedTransactionAmountList
                                                           from '../../../../shared/transaction/fixed/transactionAmounts/FixedTransactionAmountList';
import CreateFixedTransactionAmountDialog
                                                           from '../../../../shared/transaction/fixed/transactionAmounts/create/CreateFixedTransactionAmountDialog';
import AttachmentList
                                                           from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                                           from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';

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
            redirectToFixedTransactionOverview: false
        };
    }


    componentDidMount() {
        const {fixedTransactionId} = this.props.match.params;

        if (fixedTransactionId) {
            this.props.dispatchLoadFixedTransaction({transactionId: parseInt(fixedTransactionId)},
                fixedTransaction => {
                    this.setState({
                        fixedTransaction: fixedTransaction,
                        activeTab: fixedTransaction.hasVariableAmounts ? 'fixedTransactionAmountsTab' : 'attachmentsTab'
                    });
                });
        }
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
                            message: i18next.t('Transaction.FixedTransactionAmounts'),
                            description: i18next.t('Message.Transaction.FixedTransaction.CreatedFixedTransactionAmount')
                        });
                        resolve();
                    });
                }
            });

    });

    render() {
        if (this.state.redirectToFixedTransactionOverview) {
            return <Redirect to={'/transactions/fixed'}/>;
        }

        if (!this.props.transactionState.isLoading && this.state.fixedTransaction === undefined) {
            return (
                <Result
                    status="404"
                    title="Not found"
                    subTitle="Sorry, the page you visited does not exist."
                    extra={<Link to={'/'}><Button type="primary">Back Home</Button></Link>}/>);
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
                                        icon={<EditOutlined/>}>
                                    {this.props.t('Form.Button.Edit')}
                                </Button>
                                <Button id={'deleteFixedTransaction'}
                                        danger
                                        type={'primary'}
                                        icon={<DeleteOutlined/>}>
                                    {this.props.t('Form.Button.Delete')}
                                </Button>
                            </Space>}
                        extraContent={
                            <AmountStatistics
                                data={this.state.fixedTransaction.hasVariableAmounts ?
                                    this.state.fixedTransaction.transactionAmounts[this.state.fixedTransaction.transactionAmounts.length - 1].amount :
                                    this.state.fixedTransaction.amount}/>}
                        tabList={[{
                            tab: i18next.t('Transaction.FixedTransactionAmounts'),
                            disabled: !this.state.fixedTransaction.hasVariableAmounts,
                            key: 'fixedTransactionAmountsTab',
                        }, {
                            tab: i18next.t('Transaction.Attachments'),
                            key: 'attachmentsTab',
                        }]}
                        tabActiveKey={this.state.activeTab}
                        onTabChange={activeKey => (this.setState({activeTab: activeKey}))}>

                        <ProCard collapsed={!(this.state.activeTab === 'fixedTransactionAmountsTab')}>
                            <FixedTransactionAmountList
                                fixedTransactionAmounts={this.state.fixedTransaction?.transactionAmounts || []}
                                openFixedTransactionAmountDialog={() => this.setState({showFixedTransactionAmountDialog: true})}/>
                            <CreateFixedTransactionAmountDialog
                                visible={this.state.showFixedTransactionAmountDialog}
                                onSubmit={this.onCreateFixedTransactionAmount}
                                onCancel={() => this.setState({showFixedTransactionAmountDialog: false})}/>
                        </ProCard>

                        <ProCard collapsed={!(this.state.activeTab === 'attachmentsTab')}>
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
