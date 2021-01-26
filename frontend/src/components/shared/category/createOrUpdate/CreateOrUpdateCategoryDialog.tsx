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
    parentCategoryId?: number
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
            categoryName: props.data?.name,
            parentCategoryId: props.data?.parentId || props.parentCategoryId
        };
    }


    onSubmit() {
        const parentId: number | undefined = this.state.parentCategoryId || this.props.data?.parentId || this.props.parentCategoryId;
        if (this.state.categoryName && parentId) {
            const categoryClass = CategoryUtil.getCategoryClassFromCategory(
                CategoryUtil.addRootCategories(this.props.categoryState.categories), parentId);
            if (categoryClass) {
                this.setState({confirmLoading: true});

                let category: Category;
                if (this.props.data === undefined) {
                    category = {
                        id: -1,
                        children: [],
                        name: this.state.categoryName,
                        parentId: parentId,
                        categoryClass: categoryClass
                    };
                } else {
                    category = {
                        ...this.props.data,
                        name: this.state.categoryName,
                        parentId: parentId,
                        categoryClass: categoryClass
                    };
                }

                if (this.props.onSubmit) {
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
    }

    onCancel() {
        if (this.props.onCancel) {
            this.props.onCancel();
        }
    }

    render() {
        return (
            <Modal
                title={this.props.t('Menu.Categories')}
                visible={this.props.visible}
                okText={this.props.t('Form.Button.Submit')}
                cancelText={this.props.t('Form.Button.Cancel')}
                confirmLoading={this.state.confirmLoading}
                onOk={this.onSubmit.bind(this)}
                onCancel={this.onCancel.bind(this)}>
                <Form
                    labelCol={{span: 8}}
                    wrapperCol={{span: 16}}
                    labelAlign={'left'}
                    name={'categoryDialog'}>
                    <Form.Item
                        name={'parentCategory'}
                        label={this.props.t('Transaction.Category.ParentCategory')}
                        rules={[fieldIsRequiredRule()]}>
                        <CategoryTreeSelect
                            rootSelectable
                            categoryId={this.props.data?.parentId || this.props.parentCategoryId}
                            onChange={categoryId => this.setState({parentCategoryId: categoryId})}/>
                    </Form.Item>
                    <Form.Item
                        name={'categoryName'}
                        label={this.props.t('Transaction.Category.CategoryName')}
                        rules={[fieldIsRequiredRule()]}
                        initialValue={this.props.data?.name}>
                        <Input
                            name={'categoryName'}
                            onChange={event => this.setState({categoryName: event.target.value})}
                            maxLength={255}/>
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
