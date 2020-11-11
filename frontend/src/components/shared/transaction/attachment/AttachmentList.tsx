import React                              from 'react';
import {withTranslation, WithTranslation} from 'react-i18next';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {Attachment}                       from '../../../../.openapi/models';
import ProTable                           from '@ant-design/pro-table';
import {Button}                           from 'antd';
import {PlusOutlined}                     from '@ant-design/icons';
import {columns}                          from './colums';

interface AttachmentListComponentProps extends WithTranslation{
    attachments?: Attachment[],
    openAttachmentDialog: () => void
}

interface AttachmentListComponentState {
}

class AttachmentList extends React.Component<AttachmentListComponentProps, AttachmentListComponentState> {

    render() {
        return (
            <ProTable<Attachment>
                headerTitle={this.props.t('Transaction.Attachments')}
                rowSelection={{}}
                columns={columns()}
                dataSource={this.props.attachments}
                dateFormatter={'number'}
                rowKey={'id'}
                search={false}
                pagination={false}
                loading={false}
                toolBarRender={() => [
                    <Button
                        key="newVariableTransactionButton"
                        type="primary"
                        icon={<PlusOutlined/>}
                        onClick={this.props.openAttachmentDialog}>
                        {this.props.t('Form.Button.Add')}
                    </Button>
                ]}>
            </ProTable>
        );
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
}, dispatch);

export default connect(() => {return {}}, mapDispatchToProps)(withTranslation()(AttachmentList));
