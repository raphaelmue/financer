import {ModalFuncProps} from 'antd/lib/modal';
import i18next          from 'i18next';

export const confirmDialogConfig = (title: string, content: string, onSubmit: () => Promise<void>): ModalFuncProps => {
    return {
        title: title,
        content: content,
        keyboard: true,
        centered: true,
        okText: i18next.t('Form.Button.Ok'),
        cancelText: i18next.t('Form.Button.Cancel'),
        onOk() {
            return onSubmit();
        }
    }
}
