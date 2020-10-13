import {AppState}                         from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {CategoryReducerProps}             from '../../../store/reducers/category.reducer';
import * as api                           from '../../../store/api/category.api';
import {TreeSelect}                       from 'antd';
import {UserReducerProps}                 from '../../../store/reducers/user.reducers';
import {Category}                         from '../../../.openapi/models';
import {DataNode, LegacyDataNode}         from 'rc-tree-select/lib/interface';

interface CategoryTreeSelectComponentProps extends WithTranslation, UserReducerProps, CategoryReducerProps {
    onChange: (categoryId: number | undefined) => void
}

interface CategoryTreeSelectComponentState {
    categoryId: number | undefined
}

class CategoryTreeSelect extends React.Component<CategoryTreeSelectComponentProps, CategoryTreeSelectComponentState> {

    constructor(props: CategoryTreeSelectComponentProps) {
        super(props);

        this.loadCategories();
    }

    private loadCategories() {
        if (this.props.userState.user) {
            this.props.dispatchLoadCategories({userId: this.props.userState.user.id});
        }
    }

    private convertTreeData(root: Category | undefined = undefined): DataNode[] {
        if (this.props.categoryState.categories.length > 0) {
            if (root) {
                let children: DataNode[] = [];
                if (root.children) {
                    root.children.forEach(value => {
                        children.push(...this.convertTreeData(value));
                    });
                }

                return [{
                    key: root.id,
                    title: root.name,
                    label: root.name,
                    children: children
                }];
            } else {
                let nodes: DataNode[] = [];
                this.props.categoryState.categories.forEach(value => {
                    nodes.push(...this.convertTreeData(value));
                });
                return nodes;
            }
        }
        return [];
    }

    onChange(value: any) {
        this.setState({categoryId: value}, () => this.props.onChange(this.state.categoryId));
    }

    filter = (inputValue: string, option: LegacyDataNode | undefined): boolean => {
        return option?.title?.toString().toLocaleLowerCase().includes(inputValue) || false;
    };

    render() {
        return (
            <TreeSelect<Category>
                showSearch
                allowClear
                placeholder={this.props.t('Transaction.Category.SelectCategoryPlaceholder')}
                treeData={this.convertTreeData()}
                onChange={value => this.onChange(value)}
                filterTreeNode={this.filter}
                treeDefaultExpandAll
                loading={this.props.categoryState.isLoading}>
            </TreeSelect>
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
    dispatchLoadCategories: api.loadCategories
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(CategoryTreeSelect));
