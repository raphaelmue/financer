import React                              from 'react';
import {FixedTransactionAmount}           from '../../../../../.openapi';
import ProTable                           from '@ant-design/pro-table';
import {columns}                          from './columns';
import {Button}                           from 'antd';
import {DeleteOutlined, PlusOutlined}     from '@ant-design/icons';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';

interface FixedTransactionAmountListComponentProps extends WithTranslation<'default'> {
    openFixedTransactionAmountDialog: () => void,
    onDeleteFixedTransactionAmounts?: (productIds: number[]) => Promise<void>,
    fixedTransactionAmounts?: FixedTransactionAmount[]
    disabled?: boolean,
}

interface FixedTransactionAmountListComponentState {
    selectedFixedTransactionAmountIds: number[]
}

class FixedTransactionAmountList extends React.Component<FixedTransactionAmountListComponentProps, FixedTransactionAmountListComponentState> {
    constructor(props: FixedTransactionAmountListComponentProps) {
        super(props);

        this.state = {
            selectedFixedTransactionAmountIds: []
        };
    }

    onSelectionChange(selectedRowKeys: React.ReactText[]) {
        this.setState({
            selectedFixedTransactionAmountIds: selectedRowKeys.map(value => parseInt(value.toString()))
        });
    }

    onDeleteProduct() {
        if (this.props.onDeleteFixedTransactionAmounts) {
            this.props.onDeleteFixedTransactionAmounts(this.state.selectedFixedTransactionAmountIds)
                .then(() => this.setState({selectedFixedTransactionAmountIds: []}));
        }
    }

    render() {
        return (
            <ProTable<FixedTransactionAmount>
                headerTitle={this.props.t('Transaction.FixedTransactionAmounts')}
                rowSelection={{
                    onChange: this.onSelectionChange.bind(this),
                    selectedRowKeys: this.state?.selectedFixedTransactionAmountIds || []
                }}
                columns={columns()}
                dataSource={this.props.fixedTransactionAmounts || []}
                dateFormatter={'number'}
                rowKey={'id'}
                search={false}
                pagination={false}
                loading={false}
                toolBarRender={() => [
                    <Button
                        id={'deleteFixedTransactionAmountButton'}
                        key={'deleteProductButton'}
                        style={{display: this.state.selectedFixedTransactionAmountIds.length > 0 ? 'initial' : 'none'}}
                        icon={<DeleteOutlined/>}
                        onClick={() => this.onDeleteProduct()}
                        danger>
                        {this.props.t('Form.Button.Delete')}
                    </Button>,
                    <Button
                        id={'newFixedTransactionAmountButton'}
                        key={'newVariableTransactionButton'}
                        disabled={this.props.disabled}
                        type="primary"
                        icon={<PlusOutlined/>}
                        onClick={this.props.openFixedTransactionAmountDialog}>
                        {this.props.t('Form.Button.Add')}
                    </Button>
                ]}>
            </ProTable>
        );
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(() => {
    return {};
}, mapDispatchToProps)(withTranslation<'default'>()(FixedTransactionAmountList));
