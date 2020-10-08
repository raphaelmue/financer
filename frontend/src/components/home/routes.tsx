import {MenuDataItem, Route}                                                           from '@ant-design/pro-layout/es/typings';
import i18next                                                                         from 'i18next';
import {AreaChartOutlined, DollarOutlined, SettingOutlined, TagOutlined, UserOutlined} from '@ant-design/icons';
import React                                                                           from 'react';

export const route = (): Route => [{
    path: '/',
    name: i18next.t('Menu.Home'),
    component: './dashboard/Dashboard',
    routes: [
        {
            path: '/dashboard',
            component: './dashboard/Dashboard',
            name: i18next.t('Menu.Dashboard')
        }, {
            path: '/transactions',
            name: i18next.t('Menu.Transactions'),
            routes: [
                {
                    path: '/variable',
                    name: i18next.t('Menu.VariableTransactions'),
                }, {
                    path: '/fixed',
                    name: i18next.t('Menu.FixedTransactions'),
                }
            ]
        }, {
            path: '/categories',
            name: i18next.t('Menu.Categories'),
            icon: <TagOutlined/>,
        }, {
            path: '/profile',
            component: './profile/Profile',
            name: i18next.t('Menu.Profile'),
            icon: <UserOutlined/>
        }, {
            path: '/settings',
            name: i18next.t('Menu.Settings'),
            icon: <SettingOutlined/>,
        }
    ]
}];

export const menuData = (): MenuDataItem[] => [
    {
        path: '/dashboard',
        name: i18next.t('Menu.Dashboard'),
        icon: <AreaChartOutlined/>,
        component: './dashboard/Dashboard'
    }, {
        path: '/transactions',
        name: i18next.t('Menu.Transactions'),
        icon: <DollarOutlined/>,
        children: [
            {
                path: '/transactions/variable',
                name: i18next.t('Menu.VariableTransactions')
            }, {
                path: '/transactions/fixed',
                name: i18next.t('Menu.FixedTransactions')
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
