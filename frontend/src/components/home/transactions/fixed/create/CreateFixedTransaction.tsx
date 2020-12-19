import React                                                from 'react';
import {transactionDispatchMap}                             from '../../../../../store/api/transaction.api';
import {connect}                                            from 'react-redux';
import {WithTranslation, withTranslation}                   from 'react-i18next';
import {TransactionReducerProps}                            from '../../../../../store/reducers/transaction.reducer';
import {Redirect}                                           from 'react-router-dom';
import {PageContainer}                                      from '@ant-design/pro-layout';
import StepForm, {FormStep}                                 from '../../../../shared/form/step/StepForm';
import FixedTransactionDataForm, {FixedTransactionMetaData} from '../../../../shared/transaction/fixed/transactionData/FixedTransactionDataForm';
import AttachmentList
                                                            from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                                            from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';
import {
    CreateFixedTransaction as CreateFixedTransactionDTO,
    CreateFixedTransactionAmount,
    FixedTransactionAmount
}                                                           from '../../../../../.openapi';
import FixedTransactionAmountList
                                                            from '../../../../shared/transaction/fixed/transactionAmounts/FixedTransactionAmountList';
import {notification}                                       from 'antd';
import {AppState}                                           from '../../../../../store/reducers/root.reducers';
import CreateFixedTransactionAmountDialog
                                                            from '../../../../shared/transaction/fixed/transactionAmounts/create/CreateFixedTransactionAmountDialog';

interface CreateFixedTransactionComponentProps extends WithTranslation<'default'>, TransactionReducerProps {
}

interface CreateFixedTransactionComponentState {
    fixedTransactionData: FixedTransactionMetaData,
    transactionAmounts: FixedTransactionAmount[]
    redirectToOverview: boolean,
    showFixedTransactionAmountDialog: boolean
    showAttachmentDialog: boolean
}

class CreateFixedTransaction extends React.Component<CreateFixedTransactionComponentProps, CreateFixedTransactionComponentState> {


    constructor(props: CreateFixedTransactionComponentProps) {
        super(props);

        this.state = {
            fixedTransactionData: {},
            transactionAmounts: [],
            redirectToOverview: false,
            showFixedTransactionAmountDialog: false,
            showAttachmentDialog: false
        };
    }

    onSubmit() {
        this.props.dispatchCreateFixedTransaction({
            createFixedTransaction: {
                ...this.state.fixedTransactionData as CreateFixedTransactionDTO,
                transactionAmounts: this.state.transactionAmounts
            }
        }, () => {
            this.setState({redirectToOverview: true}, () => notification.success({
                message: this.props.t('Transaction.FixedTransactions')?.toString(),
                description: this.props.t('Message.Transaction.FixedTransaction.CreatedFixedTransaction')?.toString()
            }));
        });
    }

    onCreateFixedTransaction = (fixedTransactionAmount: FixedTransactionAmount): Promise<void> => {
        return new Promise<void>(resolve => {
            fixedTransactionAmount.id = -this.state.transactionAmounts.length - 1;
            this.setState({
                transactionAmounts: [...this.state.transactionAmounts, fixedTransactionAmount],
                showFixedTransactionAmountDialog: false
            });
            resolve();
        });
    };

    transactionDataCondition = (): boolean => (
        this.state.fixedTransactionData.categoryId !== undefined
        && this.state.fixedTransactionData.timeRange !== undefined
        && this.state.fixedTransactionData.timeRange?.startDate !== undefined);

    steps: FormStep[] = [{
        key: 'transactionData',
        title: this.props.t('Form.Step.CreateFixedTransaction.TransactionData'),
        content: () => (
            <FixedTransactionDataForm
                onChange={fixedTransactionData => this.setState({fixedTransactionData: fixedTransactionData})}/>),
        condition: this.transactionDataCondition
    }, {
        key: 'amountData',
        disabled: this.state?.fixedTransactionData?.hasVariableAmounts || false,
        title: this.props.t('Form.Step.CreateFixedTransaction.AmountData'),
        content: () => (
            <div>
                <FixedTransactionAmountList
                    disabled={!this.state.fixedTransactionData.hasVariableAmounts}
                    fixedTransactionAmounts={this.state.transactionAmounts}
                    openFixedTransactionAmountDialog={() => this.setState({showFixedTransactionAmountDialog: true})}/>
                <CreateFixedTransactionAmountDialog
                    visible={this.state.showFixedTransactionAmountDialog}
                    onSubmit={this.onCreateFixedTransaction}
                    onCancel={() => this.setState({showFixedTransactionAmountDialog: false})}/>
            </div>)
    }, {
        title: this.props.t('Form.Step.CreateFixedTransaction.Attachments'),
        key: 'attachments',
        content: () => (
            <div>
                <AttachmentList
                    openAttachmentDialog={() => this.setState({showAttachmentDialog: true})}/>
                <CreateAttachmentDialog
                    visible={this.state?.showAttachmentDialog || false}
                    onCancel={() => this.setState({showAttachmentDialog: false})}
                    // onSubmit={(attachment: UploadFile[]) => this.setState({
                    //     attachments: this.state.attachments.concat(attachment),
                    //     showAttachmentDialog: false
                    // })}
                />
            </div>)
    }];

    render() {
        if (this.state.redirectToOverview) {
            return <Redirect to={'/transactions/fixed'}/>;
        }

        return (
            <PageContainer>
                <StepForm steps={this.steps}
                          loading={this.props.transactionState.isLoading}
                          onSubmit={this.onSubmit.bind(this)}/>
            </PageContainer>
        );
    }
}

export default connect((state: AppState) => {
    return {
        transactionState: state.transaction
    };
}, transactionDispatchMap)(withTranslation<'default'>()(CreateFixedTransaction));
