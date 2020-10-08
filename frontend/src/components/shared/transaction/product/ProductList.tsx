import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {Product}                          from '../../../../.openapi/models';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {tableTranslations}                from '../../../../translations/translations';
import {Button}                           from 'antd';
import {PlusOutlined}                     from '@ant-design/icons';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './colums';

interface ProductListComponentProps extends WithTranslation {
    products: Product[]
}

interface ProductComponentState {
}

class ProductList extends React.Component<ProductListComponentProps, ProductComponentState> {

    openCreateProductDialog() {

    }

    render() {
        return (
            <ProTable<Product>
                headerTitle={this.props.t('Transaction.Products')}
                rowSelection={{}}
                columns={columns()}
                dataSource={this.props.products}
                dateFormatter={'number'}
                locale={tableTranslations()}
                rowKey={'id'}
                search={false}
                pagination={false}
                loading={false}
                toolBarRender={() => [
                    <Button
                        key="newVariableTransactionButton"
                        type="primary"
                        icon={<PlusOutlined/>}
                        onClick={this.openCreateProductDialog}>
                        {this.props.t('Form.Button.Add')}
                    </Button>
                ]}>
            </ProTable>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {};
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(ProductList));