import {i18n} from 'i18next';
import {Rule} from 'antd/es/form';

export const fieldIsRequiredRule = (i18n: i18n): Rule => {
    return {
        required: true,
        message: i18n.t('ErrorMessage.Form.FieldRequired')
    };
};