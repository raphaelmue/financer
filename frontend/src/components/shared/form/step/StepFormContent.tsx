import React   from 'react';
import ProCard from '@ant-design/pro-card';

interface StepFormContentComponentProps {
    key: string,
    visible: boolean
}

interface StepFormContentComponentState {
}

export class StepFormContent extends React.Component<StepFormContentComponentProps, StepFormContentComponentState> {
    render() {
        return (
            <ProCard collapsed={!this.props.visible} bordered>
                {this.props.children}
            </ProCard>
        );
    }
}
