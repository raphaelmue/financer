import {ProColumns}          from '@ant-design/pro-table';
import {VariableTransaction} from '../../../../.openapi/models';
import i18next               from 'i18next';

export const columns = (): ProColumns<VariableTransaction>[] => [
    {
        key: 'id',
        title: '#',
        dataIndex: 'id',
        valueType: 'index',
    }, {
        key: 'valueDate',
        title: i18next.t('Transaction.ValueDate'),
        dataIndex: ['valueDate', 'date'],
        valueType: 'date',
        sortOrder: 'descend'
    }, {
        key: 'category',
        title: i18next.t('Transaction.Category.Name'),
        dataIndex: ['category', 'name'],
        valueType: 'text',
    }, {
        key: 'vendor',
        title: i18next.t('Transaction.Vendor'),
        dataIndex: 'vendor',
        valueType: 'text',
    }, {
        key: 'description',
        title: i18next.t('Transaction.Description'),
        dataIndex: 'description',
        valueType: 'text',
    }
];