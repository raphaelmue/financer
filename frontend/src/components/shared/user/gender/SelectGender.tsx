import React                              from 'react';
import {Select}                           from 'antd';
import {withTranslation, WithTranslation} from 'react-i18next';
import {Gender, GenderEnum}               from '../../../../.openapi/models';
import {connect}                          from 'react-redux';

const {Option} = Select;


interface SelectGenderComponentProps extends WithTranslation<'default'> {
    gender?: Gender
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
                    placeholder={this.props.t('Profile.User.Gender.Gender')}
                    onChange={(value: string) => this.onChange(value)}
                    defaultValue={this.props.gender?.gender.valueOf()}>
                <Option value={GenderEnum.MALE}>{this.props.t('Profile.User.Gender.MALE')}</Option>
                <Option value={GenderEnum.FEMALE}>{this.props.t('Profile.User.Gender.FEMALE')}</Option>
                <Option value={GenderEnum.NOTSPECIFIED}>{this.props.t('Profile.User.Gender.NOT_SPECIFIED')}</Option>
            </Select>
        );
    }
}

export default connect()(withTranslation<'default'>()(SelectGender));

