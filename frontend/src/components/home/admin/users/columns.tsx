import {ProColumns} from '@ant-design/pro-table';
import {User}       from '../../../../.openapi';
import i18next      from 'i18next';
import React        from 'react';
import {Link}       from 'react-router-dom';

export const columns = (): ProColumns<User>[] => [
    {
        key: 'id',
        title: '#',
        dataIndex: 'id',
        valueType: 'index',
    }, {
        key: 'name',
        title: i18next.t('Profile.User.Name'),
        sortDirections: ['ascend', 'descend'],
        // eslint-disable-next-line react/display-name
        render: (dom, entity) => entity.name.firstName + ' ' + entity.name.surname,
    }, {
        key: 'email',
        title: i18next.t('Profile.User.Email'),
        // eslint-disable-next-line react/display-name
        render: (dom, entity) => (
            <Link to={'#'}
                  onClick={(event) => {
                      window.location.href = 'mailto:' + entity.email.emailAddress;
                      event.preventDefault();
                  }}>{entity.email.emailAddress}</Link>),
    }, {
        key: 'role',
        title: i18next.t('Profile.User.Roles'),
        // eslint-disable-next-line react/display-name
        render: (dom, entity) => entity.roles?.map(value => value.name).toString(),
    }
];
