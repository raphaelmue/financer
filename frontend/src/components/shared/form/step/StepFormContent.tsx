import React   from 'react';
import ProCard from '@ant-design/pro-card';

interface StepFormContentComponentProps {
    key: string,
    visible: boolean,
    loading?: boolean
}

interface StepFormContentComponentState {
}

export class StepFormContent extends React.Component<StepFormContentComponentProps, StepFormContentComponentState> {
    render() {
        return (
            <ProCard bordered
                     collapsed={!this.props.visible}
                     loading={this.props.loading}>
                {this.props.children}
            </ProCard>
        );
    }
}
