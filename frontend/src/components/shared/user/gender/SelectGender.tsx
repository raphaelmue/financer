import React                              from 'react';
import {Select}                           from 'antd';
import {withTranslation, WithTranslation} from 'react-i18next';
import {Gender, GenderEnum}               from '../../../../.openapi/models';
import {connect}                          from 'react-redux';

const {Option} = Select;


interface SelectGenderComponentProps extends WithTranslation<'default'> {
    onChange: (gender: Gender | undefined) => void
}

interface SelectGenderComponentState {
    gender: Gender
}

class SelectGender extends React.Component<SelectGenderComponentProps, SelectGenderComponentState> {

    onChange(value: string) {
        this.setState({gender: {gender: value as GenderEnum}}, () => this.props.onChange(this.state.gender));
    }

    render() {
        return (
            <Select key={'selectGender'}
                    placeholder={this.props.t('Gender.gender')}
                    onChange={(value: string) => this.onChange(value)}>
                <Option value={GenderEnum.Male}>{this.props.t('Gender.male')}</Option>
                <Option value={GenderEnum.Female}>{this.props.t('Gender.female')}</Option>
                <Option value={GenderEnum.NotSpecified}>{this.props.t('Gender.notSpecified')}</Option>
            </Select>
        );
    }
}

export default connect()(withTranslation<"default">()(SelectGender));

