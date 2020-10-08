import {ProColumns} from '@ant-design/pro-table';
import {Product}    from '../../../../.openapi/models';
import i18next      from 'i18next';

export const columns = (): ProColumns<Product>[] => [
    {
        key: 'id',
        title: '#',
        dataIndex: 'id',
        valueType: 'index',

    }, {
        key: 'name',
        title: i18next.t('Transaction.Product.Name'),
        dataIndex: 'name',
        valueType: 'text',
    }, {
        key: 'quantity',
        title: i18next.t('Transaction.Product.Quantity'),
        dataIndex: ['quantity', 'numberOfItems'],
        valueType: 'digit',
    }, {
        key: 'amount',
        title: i18next.t('Transaction.Amount'),
        dataIndex: ['amount', 'amount'],
        valueType: 'money',
    }
];