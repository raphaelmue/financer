import {ProColumns}          from '@ant-design/pro-table';
import {VariableTransaction} from '../../../../.openapi/models';
import i18next               from 'i18next';
import AmountLabel           from '../../../shared/transaction/amount/amountLabel/AmountLabel';
import React                 from 'react';

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
        sortDirections: ['ascend', 'descend'],
        sorter: (a, b) => a.valueDate.date.getMilliseconds() - b.valueDate.date.getMilliseconds(),
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
    }, {
        key: 'amount',
        title: i18next.t('Transaction.Amount'),
        dataIndex: ['totalAmount', 'amount'],
        valueType: 'money',
        render: (dom, entity) => (<AmountLabel amount={entity.totalAmount}/>)
    }
];
