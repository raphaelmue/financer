import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React, {RefObject}                 from 'react';
import {Form, Modal}                      from 'antd';
import {CreateAttachment}                 from '../../../../../.openapi';
import {FormInstance}                     from 'antd/lib/form';
import Dragger                            from 'antd/lib/upload/Dragger';
import {InboxOutlined}                    from '@ant-design/icons';
import {UploadChangeParam, UploadFile}    from 'antd/lib/upload/interface';

interface CreateAttachmentDialogComponentProps extends WithTranslation<'default'> {
    visible: boolean
    onSubmit?: (files: CreateAttachment[]) => void,
    onCancel?: () => void
}

interface CreateAttachmentDialogComponentState {
    visible: boolean,
    files: UploadFile[]
}

const initialState: CreateAttachmentDialogComponentState = {
    visible: false,
    files: []
};

class CreateAttachmentDialog extends React.Component<CreateAttachmentDialogComponentProps, CreateAttachmentDialogComponentState> {

    formRef: RefObject<FormInstance> = React.createRef();

    constructor(props: CreateAttachmentDialogComponentProps) {
        super(props);
        this.state = initialState;
    }

    onCancel() {
        if (this.props.onCancel) {
            this.props.onCancel();
        }
    }

    onSubmit() {
        if (this.formRef.current) {
            this.formRef.current.resetFields();
        }
        if (this.props.onSubmit) {
            this.props.onSubmit(this.state.files.map((value: UploadFile): CreateAttachment => {
                if (value && value.fileName && value.originFileObj) {
                    return {
                        name: value.fileName,
                        content: value.originFileObj
                    };
                }
                throw new Error('Cannot get property of undefined.');
            }));
        }
        this.setState(initialState);
    }

    onChange(info: UploadChangeParam) {
        this.setState({
            files: info.fileList
        });
    }

    render() {
        return (
            <Modal
                centered
                title={this.props.t('Menu.Transaction.Attachment.CreateAttachment')}
                visible={this.props.visible}
                onOk={this.onSubmit.bind(this)}
                onCancel={this.onCancel.bind(this)}
                cancelText={this.props.t('Form.Button.Cancel')}
                keyboard={true}
                okText={this.props.t('Form.Button.Submit')}>
                <Form layout="vertical"
                      onFinish={this.onSubmit}
                      ref={this.formRef}>
                    <Dragger
                        onChange={this.onChange.bind(this)}>
                        <p className="ant-upload-drag-icon">
                            <InboxOutlined/>
                        </p>
                        <p className="ant-upload-text">{this.props.t('Form.Dragger.DraggerText')}</p>
                    </Dragger>
                </Form>
            </Modal>
        );
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(() => {
    return {};
}, mapDispatchToProps)(withTranslation<'default'>()(CreateAttachmentDialog));
