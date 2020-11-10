import i18next from 'i18next';
import {Rule}  from 'antd/es/form';

export const fieldIsRequiredRule = (): Rule => {
    return {
        required: true,
        message: i18next.t('ErrorMessage.Form.FieldRequired')
    };
};
