import {ProColumns}   from '@ant-design/pro-table';
import {Token}        from '../../../.openapi';
import i18next        from 'i18next';
import ValueDateLabel from '../../shared/transaction/valueDate/valueDateLabel/ValueDateLabel';
import React          from 'react';


export const columns = (): ProColumns<Token>[] => [
    {
        key: 'id',
        title: '#',
        dataIndex: 'id',
        valueType: 'index',
    }, {
        key: 'ipAddress',
        title: i18next.t('Profile.User.Token.IPAddress'),
        dataIndex: 'ipAddress'
    }, {
        key: 'expireDate',
        title: i18next.t('Profile.User.Token.ExpireDate'),
        dataIndex: ['expireDate', 'expireDate'],
        valueType: 'date',
        // eslint-disable-next-line react/display-name
        render: (dom, entity): JSX.Element => {
            if (entity.expireDate?.expireDate) {
                return (<ValueDateLabel valueDate={{date: entity.expireDate.expireDate}}/>);
            }
            return <span/>;
        }
    }, {
        key: 'operatingSystem',
        title: i18next.t('Profile.User.Token.OperatingSystem'),
        dataIndex: ['operatingSystem', 'operatingSystem'],
    },
];
