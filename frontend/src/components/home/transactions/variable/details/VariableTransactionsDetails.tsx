import {withTranslation, WithTranslation}                         from 'react-i18next';
import React                                                      from 'react';
import {Link, Redirect, RouteComponentProps}                      from 'react-router-dom';
import {AppState}                                                 from '../../../../../store/reducers/root.reducers';
import {connect}                                                  from 'react-redux';
import {transactionDispatchMap}                                   from '../../../../../store/api/transaction.api';
import {TransactionReducerProps}                                  from '../../../../../store/reducers/transaction.reducer';
import {CreateProduct, VariableTransaction}                       from '../../../../../.openapi';
import {Button, Descriptions, Modal, notification, Result, Space} from 'antd';
import {DeleteOutlined, EditOutlined}                             from '@ant-design/icons';
import {PageContainer}                                            from '@ant-design/pro-layout';
import Text                                                       from 'antd/lib/typography/Text';
import i18next                                                    from 'i18next';
import AmountStatistics
                                                                  from '../../../../shared/transaction/amount/amountStatistics/AmountStatistics';
import ProductList
                                                                  from '../../../../shared/transaction/product/ProductList';
import ValueDateLabel
                                                                  from '../../../../shared/transaction/valueDate/valueDateLabel/ValueDateLabel';
import ProCard                                                    from '@ant-design/pro-card';
import AttachmentList
                                                                  from '../../../../shared/transaction/attachment/AttachmentList';
import CreateProductDialog
                                                                  from '../../../../shared/transaction/product/create/CreateProductDialog';
import CreateAttachmentDialog
                                                                  from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';
import {confirmDialogConfig}                                      from '../../../../shared/form/modal/confirm/config';
import UpdateVariableTransactionDialog
                                                                  from '../../../../shared/transaction/variable/update/UpdateVariableTransactionDialog';

const {Item} = Descriptions;

interface RouteProps {
    variableTransactionId?: string
}

interface VariableTransactionsDetailsComponentProps extends RouteComponentProps<RouteProps>,
    WithTranslation, TransactionReducerProps {
}

interface VariableTransactionsDetailsComponentState {
    variableTransaction: VariableTransaction | undefined,
    activeTab: string,
    showProductDialog: boolean,
    showAttachmentDialog: boolean,
    showUpdateVariableTransactionDialog: boolean
    redirectToVariableTransactionList: boolean
}

class VariableTransactionsDetails extends React.Component<VariableTransactionsDetailsComponentProps,
    VariableTransactionsDetailsComponentState> {

    constructor(props: VariableTransactionsDetailsComponentProps) {
        super(props);
        this.state = {
            variableTransaction: undefined,
            activeTab: 'productsTab',
            showProductDialog: false,
            showAttachmentDialog: false,
            showUpdateVariableTransactionDialog: false,
            redirectToVariableTransactionList: false
        };
    }

    componentDidMount() {
        const {variableTransactionId} = this.props.match.params;

        if (variableTransactionId) {
            this.props.dispatchLoadVariableTransaction({transactionId: parseInt(variableTransactionId)},
                variableTransaction => {
                    this.setState({variableTransaction: variableTransaction});
                });
        }
    }

    onDeleteVariableTransaction() {
        Modal.confirm(confirmDialogConfig(
            this.props.t('Transaction.VariableTransaction'),
            this.props.t('Message.Transaction.VariableTransaction.ConfirmDeleteVariableTransaction'),
            () => new Promise<void>(
                resolve => this.props.dispatchDeleteVariableTransaction({transactionId: this.state.variableTransaction?.id!},
                    () => this.setState({redirectToVariableTransactionList: true},
                        () => {
                            resolve();
                            notification.success({
                                message: this.props.t('Transaction.VariableTransaction'),
                                description: this.props.t('Message.Transaction.VariableTransaction.DeletedVariableTransaction')
                            });
                        }
                    ))
            )
        ));
    }

    onDeleteProducts(productIds: number[]): Promise<void> {
        return new Promise<void>((resolveConfirm => Modal.confirm(confirmDialogConfig(
            this.props.t('Transaction.Products'),
            this.props.t('Message.Transaction.VariableTransaction.Product.ConfirmDeleteProducts'),
            () => new Promise<void>(resolveDispatch => {
                this.props.dispatchDeleteProducts({
                    transactionId: this.state.variableTransaction?.id!,
                    productIds: productIds
                }, () => {
                    let variableTransaction: VariableTransaction = this.state.variableTransaction!;
                    variableTransaction.products = new Set([...variableTransaction.products!].filter(
                        (product) => productIds.indexOf(product.id) < 0));
                    this.setState({variableTransaction: variableTransaction});
                    notification.success({
                        message: this.props.t('Transaction.Products'),
                        description: this.props.t('Message.Transaction.VariableTransaction.Product.DeletedProducts')
                    });
                    resolveDispatch();
                    resolveConfirm();
                });
            })
        ))));
    }

    onCreateProduct(product: CreateProduct): Promise<void> {
        return new Promise<void>((resolve) => {
            this.props.dispatchCreateProduct({
                transactionId: this.state.variableTransaction?.id!,
                createProduct: product
            }, newProduct => {
                notification.success({
                    message: this.props.t('Transaction.VariableTransaction'),
                    description: this.props.t('Message.Transaction.VariableTransaction.CreatedProduct')
                });
                this.setState({
                    variableTransaction: {
                        ...this.state.variableTransaction!,
                        products: new Set([...this.state.variableTransaction?.products!, newProduct])
                    },
                    showProductDialog: false
                }, () => resolve());
            });
        });
    }

    render() {
        if (this.state.redirectToVariableTransactionList) {
            return <Redirect to={'/transactions/variable'}/>;
        }

        if (!this.props.transactionState.isLoading && this.state.variableTransaction === undefined) {
            return (
                <Result
                    status="404"
                    title="Not found"
                    subTitle="Sorry, the page you visited does not exist."
                    extra={<Link to={'/'}><Button type="primary">Back Home</Button></Link>}/>);
        } else {
            if (this.state.variableTransaction === undefined) {
                return (<PageContainer
                    loading={this.props.transactionState.isLoading}/>);
            } else {
                return (
                    <PageContainer
                        content={(
                            <Descriptions column={2}>
                                <Item label={this.props.t('Transaction.ValueDate')}>
                                    <Text strong={true}>
                                        <ValueDateLabel valueDate={this.state.variableTransaction?.valueDate}/>
                                    </Text>
                                </Item>
                                <Item label={this.props.t('Transaction.Vendor')}>
                                    {this.state.variableTransaction?.vendor}
                                </Item>
                                <Item label={this.props.t('Transaction.Category.Name')}>
                                    <Text strong={true}>{this.state.variableTransaction?.category.name}</Text>
                                </Item>
                                <Item label={this.props.t('Transaction.Description')}>
                                    {this.state.variableTransaction?.description}
                                </Item>
                            </Descriptions>)}
                        extra={
                            <Space size={'small'}>
                                <Button icon={<EditOutlined/>}
                                        onClick={() => this.setState({showUpdateVariableTransactionDialog: true})}>
                                    {this.props.t('Form.Button.Edit')}
                                </Button>
                                <Button danger
                                        type={'primary'}
                                        icon={<DeleteOutlined/>}
                                        onClick={this.onDeleteVariableTransaction.bind(this)}>
                                    {this.props.t('Form.Button.Delete')}
                                </Button>
                            </Space>}
                        extraContent={
                            <AmountStatistics
                                data={this.state.variableTransaction?.totalAmount}/>}
                        tabList={[{
                            tab: i18next.t('Transaction.Products'),
                            key: 'productsTab',
                        }, {
                            tab: i18next.t('Transaction.Attachments'),
                            key: 'attachmentsTab',
                        }]}
                        onTabChange={activeKey => (this.setState({activeTab: activeKey}))}>
                        <ProCard collapsed={!(this.state.activeTab === 'productsTab')}>
                            <ProductList
                                products={[...this.state.variableTransaction?.products!]}
                                openProductDialog={() => this.setState({showProductDialog: true})}
                                onDeleteProducts={products => this.onDeleteProducts(products)}/>
                            <CreateProductDialog
                                visible={this.state.showProductDialog || false}
                                onCancel={() => this.setState({showProductDialog: false})}
                                onSubmit={product => this.onCreateProduct(product)}/>
                        </ProCard>
                        <ProCard collapsed={!(this.state.activeTab === 'attachmentsTab')}>
                            <AttachmentList
                                attachments={[...this.state.variableTransaction?.attachments!]}
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
                        <UpdateVariableTransactionDialog
                            visible={this.state.showUpdateVariableTransactionDialog}
                            variableTransaction={this.state.variableTransaction}
                            onSubmit={variableTransaction => this.setState({
                                showUpdateVariableTransactionDialog: false,
                                variableTransaction: variableTransaction
                            })}
                            onCancel={() => this.setState({showUpdateVariableTransactionDialog: false})}/>
                    </PageContainer>
                );
            }
        }
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        transactionState: state.transaction
    };
};

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation()(VariableTransactionsDetails));
