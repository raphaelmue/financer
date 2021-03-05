import {AppState}                         from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {CategoryReducerProps}             from '../../../../store/reducers/category.reducer';
import * as categoryApi                   from '../../../../store/api/category.api';
import {TreeSelect}                       from 'antd';
import {UserReducerProps}                 from '../../../../store/reducers/user.reducers';
import {LegacyDataNode}                   from 'rc-tree-select/lib/interface';
import CategoryUtil                       from '../util';
import * as userApi                       from '../../../../store/api/user.api';
import {Category}                         from '../../../../.openapi';

interface CategoryTreeSelectComponentProps extends WithTranslation<'default'>, UserReducerProps, CategoryReducerProps {
    onChange: (categoryId: number | undefined) => void,
    categoryId?: number,
    filterFixed?: boolean,
    filterVariable?: boolean,
    rootSelectable?: boolean,
    filter?: (category: Category) => boolean
}

interface CategoryTreeSelectComponentState {
    categoryId: number | undefined,
}

class CategoryTreeSelect extends React.Component<CategoryTreeSelectComponentProps, CategoryTreeSelectComponentState> {

    constructor(props: CategoryTreeSelectComponentProps) {
        super(props);

        this.loadCategories();
        this.state = {
            categoryId: props.categoryId
        };
    }

    private loadCategories() {
        if (this.props.userState.user) {
            this.props.dispatchLoadCategories({userId: this.props.userState.user.id});
        }
    }

    onChange(value: any) {
        this.setState({categoryId: value}, () => this.props.onChange(this.state.categoryId));
    }

    filter = (inputValue: string, option: LegacyDataNode | undefined): boolean => {
        return option?.title?.toString().toLocaleLowerCase().includes(inputValue) || false;
    };

    render() {
        return (
            <TreeSelect
                showSearch
                defaultValue={this.props.categoryId}
                key={'id'}
                allowClear
                placeholder={this.props.t('Transaction.Category.SelectCategoryPlaceholder')?.toString()}
                treeData={CategoryUtil.getTreeData(this.props.categoryState.categories, this.props.filterFixed, this.props.filterVariable, this.props.rootSelectable)}
                onChange={value => this.onChange(value)}
                filterTreeNode={this.filter}
                treeDefaultExpandAll
                loading={this.props.categoryState.isLoading}/>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user,
        categoryState: state.category
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadCategories: categoryApi.loadCategories,
    dispatchCreateCategory: categoryApi.createCategory,
    dispatchUpdateCategory: categoryApi.updateCategory,
    dispatchDeleteCategory: categoryApi.deleteCategory,
    dispatchLoginUser: userApi.loginUser,
    dispatchRegisterUser: userApi.registerUser,
    dispatchDeleteToken: userApi.deleteToken,
    dispatchGetUser: userApi.getUser,
    dispatchUpdateUsersPassword: userApi.updateUsersPassword,
    dispatchUpdateUsersSettings: userApi.updateUsersSettings,
    dispatchUpdateUsersData: userApi.updateUsersData
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(CategoryTreeSelect));
