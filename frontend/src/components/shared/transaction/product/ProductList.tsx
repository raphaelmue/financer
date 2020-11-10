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
    onDeleteProduct?: (products: Product[]) => void,
    products?: Product[]
}

interface ProductComponentState {
    selectedProducts: Product[]
}

class ProductList extends React.Component<ProductListComponentProps, ProductComponentState> {

    constructor(props: ProductListComponentProps) {
        super(props);

        this.state = {
            selectedProducts: []
        };
    }

    onSelectionChange(selectedRowKeys: React.ReactText[], selectedRows: Product[]) {
        this.setState({
            selectedProducts: selectedRows
        });
    }

    render() {
        return (
            <ProTable<Product>
                headerTitle={this.props.t('Transaction.Products')}
                rowSelection={{
                    onChange: this.onSelectionChange.bind(this)
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
                            // type="primary"
                            style={{display: this.state.selectedProducts.length > 0 ? 'initial' : 'none'}}
                            icon={<DeleteOutlined/>}
                            onClick={() => {
                                if (this.props.onDeleteProduct) this.props.onDeleteProduct(this.state.selectedProducts);
                            }}
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
