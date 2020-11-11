import {UserReducerProps}             from '../../../../../store/reducers/user.reducers';
import {AppState}                     from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import {connect}                      from 'react-redux';
import {Amount}                       from '../../../../../.openapi/models';
import React                          from 'react';
import {InputNumber}                  from 'antd';
import {getCurrencySign}              from '../../../user/settings/settingsUtil';

interface AmountInputComponentProps extends UserReducerProps {
    amount?: Amount,
    onChange?: (amount: Amount) => void
}

interface AmountInputComponentState {
    amount: number
}

class AmountInput extends React.Component<AmountInputComponentProps, AmountInputComponentState> {

    constructor(props: AmountInputComponentProps) {
        super(props);
        this.state = {amount: 0};
    }


    formatter = (value: (string | number | undefined)): string => {
        let currency: string = getCurrencySign();
        return `${currency} ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    };

    onChange(value: number | string | undefined) {
        this.setState({amount: Number(value) || 0}, () => {
            if (this.props.onChange) {
                this.props.onChange({amount: this.state.amount});
            }
        });
    }

    render() {
        return (
            <InputNumber
                name={'amount'}
                value={this.state.amount}
                inputMode={'decimal'}
                style={{width: '100%'}}
                precision={2}
                formatter={this.formatter}
                onChange={this.onChange.bind(this)}/>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(AmountInput);
