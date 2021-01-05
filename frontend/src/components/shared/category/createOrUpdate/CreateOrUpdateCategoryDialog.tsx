import {AppState}                         from '../../../../store/reducers/root.reducers';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {DataDialog}                       from '../../form/modal/data/types';
import {Category}                         from '../../../../.openapi';
import {Form, Input, Modal}               from 'antd';
import {CategoryReducerProps}             from '../../../../store/reducers/category.reducer';
import CategoryTreeSelect                 from '../select/CategoyTreeSelect';
import CategoryUtil                       from '../util';
import {fieldIsRequiredRule}              from '../../user/form/rules';
import {categoryDispatchMap}              from '../../../../store/api/category.api';

interface CreateOrUpdateCategoryDialogComponentProps extends DataDialog<Category>, WithTranslation<'default'>, CategoryReducerProps {
}

interface CreateOrUpdateCategoryDialogComponentState {
    parentCategoryId?: number,
    categoryName?: string,
    confirmLoading: boolean
}

class CreateOrUpdateCategoryDialog extends React.Component<CreateOrUpdateCategoryDialogComponentProps, CreateOrUpdateCategoryDialogComponentState> {

    constructor(props: CreateOrUpdateCategoryDialogComponentProps) {
        super(props);

        this.state = {
            confirmLoading: false,
        };
    }


    onSubmit() {
        if (this.props.onSubmit && this.state.categoryName && this.state.parentCategoryId) {
            const categoryClass = CategoryUtil.getCategoryClassFromCategory(this.props.categoryState.categories, this.state.parentCategoryId);
            if (categoryClass) {
                this.setState({confirmLoading: true});

                let category: Category;
                if (this.props.data === undefined) {
                    category = {
                        id: -1,
                        children: [],
                        name: this.state.categoryName,
                        categoryClass: categoryClass
                    };
                } else {
                    category = this.props.data;
                }

                const promise = this.props.onSubmit(category);
                if (promise) {
                    promise.then(() => {
                        this.setState({confirmLoading: false});
                    });
                } else {
                    this.setState({confirmLoading: false});
                }
            }
        }
    }

    onCancel() {
        if (this.props.onCancel) {
            this.props.onCancel();
        }
    }

    render() {
        return (
            <Modal
                visible={this.props.visible}
                okText={this.props.t('Form.Button.Submit')}
                cancelText={this.props.t('Form.Button.Cancel')}
                confirmLoading={this.state.confirmLoading}
                onOk={this.onSubmit.bind(this)}
                onCancel={this.onCancel.bind(this)}>
                <Form
                    name={'categoryDialog'}>
                    <Form.Item
                        name={'parentCategory'}
                        label={''}
                        rules={[fieldIsRequiredRule()]}>
                        <CategoryTreeSelect
                            rootSelectable
                            categoryId={this.props.data?.id}
                            onChange={categoryId => this.setState({parentCategoryId: categoryId})}/>
                    </Form.Item>
                    <Form.Item
                        name={'categoryName'}
                        label={''}
                        rules={[fieldIsRequiredRule()]}
                        initialValue={this.props.data?.name}>
                        <Input
                            name={'categoryName'}
                            maxLength={32}/>
                    </Form.Item>
                </Form>
            </Modal>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        categoryState: state.category
    };
};

export default connect(mapStateToProps, categoryDispatchMap)(withTranslation<'default'>()(CreateOrUpdateCategoryDialog));
