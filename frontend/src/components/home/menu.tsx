import {MenuDataItem}                                                                  from '@ant-design/pro-layout/es/typings';
import i18next                                                                         from 'i18next';
import {AreaChartOutlined, DollarOutlined, SettingOutlined, TagOutlined, UserOutlined} from '@ant-design/icons';
import React                                                                           from 'react';

export default (): MenuDataItem[] => [
    {
        path: '/dashboard',
        name: i18next.t('Menu.Dashboard'),
        icon: <AreaChartOutlined/>,
        component: './dashboard/Dashboard'
    }, {
        path: '/transactions',
        name: i18next.t('Menu.Transaction.Transactions'),
        icon: <DollarOutlined/>,
        children: [
            {
                path: '/transactions/variable',
                name: i18next.t('Menu.Transaction.VariableTransactions'),
                hideChildrenInMenu: true,
                children: [
                    {
                        path: '/transactions/variable/create',
                        name: i18next.t('Menu.Transaction.CreateVariableTransaction'),
                    }
                ]
            }, {
                path: '/transactions/fixed',
                name: i18next.t('Menu.Transaction.FixedTransactions')
            }
        ]
    }, {
        name: i18next.t('Menu.Account'),
        icon: <UserOutlined/>,
        children: [
            {
                path: '/categories',
                name: i18next.t('Menu.Categories'),
                icon: <TagOutlined/>,
            }, {
                path: '/profile',
                name: i18next.t('Menu.Profile'),
                icon: <UserOutlined/>
            }, {
                path: '/settings',
                name: i18next.t('Menu.Settings'),
                icon: <SettingOutlined/>
            }
        ]
    }
];
