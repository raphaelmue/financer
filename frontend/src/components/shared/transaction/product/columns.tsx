import {ProColumns} from '@ant-design/pro-table';
import {Product}    from '../../../../.openapi/models';
import i18next      from 'i18next';
import AmountLabel  from '../amount/amountLabel/AmountLabel';
import React        from 'react';

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
        // eslint-disable-next-line react/display-name
        render: (dom: React.ReactNode, entity) => (<AmountLabel amount={entity.amount} />)
    }, {
        key: 'totalAmount',
        title: i18next.t('Transaction.TotalAmount'),
        dataIndex: ['totalAmount', 'amount'],
        valueType: 'money',
        // eslint-disable-next-line react/display-name
        render: (dom: React.ReactNode, entity) => (<AmountLabel amount={entity.totalAmount} />)
    }
];
