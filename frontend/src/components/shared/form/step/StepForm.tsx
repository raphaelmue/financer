import React                              from 'react';
import {StepProps}                        from 'antd/lib/steps';
import {Button, Steps}                    from 'antd';
import {FooterToolbar}                    from '@ant-design/pro-layout';
import {WithTranslation, withTranslation} from 'react-i18next';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {StepFormContent}                  from './StepFormContent';

const {Step} = Steps;

export interface FormStep extends StepProps {
    key: string,
    content: () => React.ReactNode,
    condition?: () => boolean
}

interface StepFormComponentProps extends WithTranslation<'default'> {
    steps: FormStep[],
    loading?: boolean,
    onSubmit?: () => void,
}

interface StepFormComponentState {
    currentStepIndex: number,
    currentStepKey: string | number
}

class StepForm extends React.Component<StepFormComponentProps, StepFormComponentState> {

    constructor(props: StepFormComponentProps) {
        super(props);

        this.state = {
            currentStepIndex: 0,
            currentStepKey: this.props.steps[0].key
        };
    }

    onPrevious() {
        this.setState({
            currentStepIndex: this.state.currentStepIndex - 1,
            currentStepKey: this.props.steps[this.state.currentStepIndex - 1].key
        });

    }

    onNext() {
        this.setState({
            currentStepIndex: this.state.currentStepIndex + 1,
            currentStepKey: this.props.steps[this.state.currentStepIndex + 1].key
        });
    }

    onSubmit() {
        if (this.props.onSubmit) {
            this.props.onSubmit();
        }
    }

    validateSteps(): boolean {
        let isValid = true;
        this.props.steps.forEach(step => {
            if (step.condition) {
                isValid = step.condition();
            }
        });
        return isValid;
    }

    renderSteps(): React.ReactNode[] {
        // eslint-disable-next-line react/jsx-key
        return this.props.steps.map((step) => <Step {...step}/>);
    }

    renderStepContent(): React.ReactNode[] {
        return this.props.steps.map(step =>
            <StepFormContent
                loading={this.props.loading}
                key={step.key}
                visible={step.key === this.state.currentStepKey}>
                {step.content()}
            </StepFormContent>);
    }

    render() {
        return (
            <div>
                <Steps current={this.state.currentStepIndex}
                       style={{marginBottom: '24px'}}>
                    {this.renderSteps()}
                </Steps>
                <div>
                    {this.renderStepContent()}
                </div>
                <FooterToolbar>
                    <Button
                        id={'previousStepButton'}
                        disabled={this.state.currentStepIndex === 0}
                        onClick={this.onPrevious.bind(this)}>
                        {this.props.t('Form.Button.Previous')}
                    </Button>
                    <Button
                        id={'nextStepButton'}
                        disabled={this.state.currentStepIndex === this.props.steps.length - 1}
                        onClick={this.onNext.bind(this)}>
                        {this.props.t('Form.Button.Next')}
                    </Button>
                    <Button
                        id={'submitStepsButton'}
                        type={'primary'}
                        loading={this.props.loading}
                        disabled={!this.validateSteps()}
                        onClick={this.onSubmit.bind(this)}>
                        {this.props.t('Form.Button.Submit')}
                    </Button>
                </FooterToolbar>
            </div>
        );
    }
}

const mapStateToProps = () => {
    return {};
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(StepForm));
