import {ProColumns}             from '@ant-design/pro-table';
import i18next                  from 'i18next';
import React                    from 'react';
import {FixedTransactionAmount} from '../../../../../.openapi';
import AmountLabel              from '../../amount/amountLabel/AmountLabel';

export const columns = (): ProColumns<FixedTransactionAmount>[] => [
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
    }, {
        key: 'amount',
        title: i18next.t('Transaction.Amount'),
        dataIndex: ['amount', 'amount'],
        valueType: 'money',
        // eslint-disable-next-line react/display-name
        render: (dom, entity) => (<AmountLabel amount={entity.amount}/>)
    }
];
