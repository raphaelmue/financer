import {ProColumns} from '@ant-design/pro-table';
import {Attachment} from '../../../../.openapi/models';
import i18next      from 'i18next';

export const columns = (): ProColumns<Attachment>[] => [
    {
        key: 'id',
        title: '#',
        dataIndex: 'id',
        valueType: 'index',

    }, {
        key: 'name',
        title: i18next.t('Transaction.Attachment.Name'),
        dataIndex: 'name',
        valueType: 'text',
    }, {
        key: 'uploadDate',
        title: i18next.t('Transaction.Attachment.UploadDate'),
        dataIndex: ['uploadDate'],
        valueType: 'date',
    }
];
