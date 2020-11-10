import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {Product}                          from '../../../../.openapi/models';
import {tableTranslations}                from '../../../../translations/translations';
import {Button, Space}                    from 'antd';
import {PlusOutlined, DeleteOutlined}     from '@ant-design/icons';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './columns';

interface ProductListComponentProps extends WithTranslation {
    openProductDialog: () => void,
    onDeleteProducts?: (productIds: number[]) => Promise<void>,
    products?: Product[]
}

interface ProductComponentState {
    selectedProductIds: number[]
}

class ProductList extends React.Component<ProductListComponentProps, ProductComponentState> {

    constructor(props: ProductListComponentProps) {
        super(props);

        this.state = {
            selectedProductIds: []
        };
    }

    onSelectionChange(selectedRowKeys: React.ReactText[]) {
        this.setState({
            selectedProductIds: selectedRowKeys.map(value => parseInt(value.toString()))
        });
    }

    onDeleteProduct() {
        if (this.props.onDeleteProducts) {
            this.props.onDeleteProducts(this.state.selectedProductIds)
                .then(() => this.setState({selectedProductIds: []}));
        }
    }

    render() {
        return (
            <ProTable<Product>
                headerTitle={this.props.t('Transaction.Products')}
                rowSelection={{
                    onChange: this.onSelectionChange.bind(this),
                    selectedRowKeys: this.state.selectedProductIds
                }}
                columns={columns()}
                dataSource={this.props.products || []}
                dateFormatter={'number'}
                locale={tableTranslations()}
                rowKey={'id'}
                search={false}
                pagination={false}
                loading={false}
                toolBarRender={() => [
                    <Space size={'small'}>
                        <Button
                            key="deleteProductButton"
                            style={{display: this.state.selectedProductIds.length > 0 ? 'initial' : 'none'}}
                            icon={<DeleteOutlined/>}
                            onClick={() => this.onDeleteProduct()}
                            danger>
                            {this.props.t('Form.Button.Delete')}
                        </Button>
                        <Button
                            key="newVariableTransactionButton"
                            type="primary"
                            icon={<PlusOutlined/>}
                            onClick={this.props.openProductDialog}>
                            {this.props.t('Form.Button.Add')}
                        </Button>
                    </Space>
                ]}>
            </ProTable>
        );
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(() => {
    return {};
}, mapDispatchToProps)(withTranslation()(ProductList));
