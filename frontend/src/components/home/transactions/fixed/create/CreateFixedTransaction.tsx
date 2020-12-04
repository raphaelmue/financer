import React                                                                         from 'react';
import {transactionDispatchMap}                                                      from '../../../../../store/api/transaction.api';
import {connect}                                                                     from 'react-redux';
import {WithTranslation, withTranslation}                                            from 'react-i18next';
import {TransactionReducerProps}                                                     from '../../../../../store/reducers/transaction.reducer';
import {Redirect}                                                                    from 'react-router-dom';
import {PageContainer}                                                               from '@ant-design/pro-layout';
import StepForm, {FormStep}                                                          from '../../../../shared/form/step/StepForm';
import FixedTransactionDataForm, {FixedTransactionMetaData}                          from '../../../../shared/transaction/fixed/transactionData/FixedTransactionDataForm';
import AttachmentList
                                                                                     from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                                                                     from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';
import {CreateFixedTransaction as CreateFixedTransactionDTO, FixedTransactionAmount} from '../../../../../.openapi';
import FixedTransactionAmountList
                                                                                     from '../../../../shared/transaction/fixed/transactionAmounts/FixedTransactionAmountList';
import {notification}                                                                from 'antd';

interface CreateFixedTransactionComponentProps extends WithTranslation, TransactionReducerProps {
}

interface CreateFixedTransactionComponentState {
    fixedTransactionData: FixedTransactionMetaData,
    transactionAmounts: Set<FixedTransactionAmount>
    redirectToOverview: boolean,
    showFixedTransactionAmountDialog: boolean
    showAttachmentDialog: boolean
}

class CreateFixedTransaction extends React.Component<CreateFixedTransactionComponentProps, CreateFixedTransactionComponentState> {

    constructor(props: CreateFixedTransactionComponentProps) {
        super(props);

        this.state = {
            fixedTransactionData: {},
            transactionAmounts: new Set(),
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
            notification.success({
                message: this.props.t('Transaction.FixedTransactions'),
                description: this.props.t('Message.Transaction.FixedTransaction.CreatedFixedTransaction')
            });
            this.setState({redirectToOverview: true})
        });
    }

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
                    openFixedTransactionAmountDialog={() => this.setState({showFixedTransactionAmountDialog: true})}/>
            </div>)
    }, {
        title: this.props.t('Form.Step.CreateFixedTransaction.Attachments'),
        key: 'attachments',
        content: () =>
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
            </div>
    }];

    render() {
        if (this.state.redirectToOverview) {
            return <Redirect to={'/transactions/fixed'}/>;
        }

        return (
            <PageContainer><StepForm steps={this.steps} onSubmit={this.onSubmit.bind(this)}/></PageContainer>
        );
    }
}

export default connect(() => {
    return {};
}, transactionDispatchMap)(withTranslation()(CreateFixedTransaction));
