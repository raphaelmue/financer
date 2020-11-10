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
    onChange: (categoryId: number | undefined) => void,
    categoryId?: number
}

interface CategoryTreeSelectComponentState {
    categoryId: number | undefined,
    initialValue?: Category
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
            this.props.dispatchLoadCategories({userId: this.props.userState.user.id}, () => {
                this.setState({initialValue: this.getDefaultData()});
            });
        }
    }

    private convertTreeData(root?: Category | undefined): DataNode[] {
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
                    value: root.id,
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

    getDefaultData(root?: Category): Category | undefined {
        if (this.props.categoryId) {
            let result: Category | undefined;
            if (root !== undefined) {
                if (root.children) {
                    for (let category of root.children) {
                        result = this.getDefaultData(category);
                        if (result) {
                            return result;
                        }
                    }
                }
                if (root.id === this.props.categoryId) {
                    return root;
                }
            } else {
                for (let category of this.props.categoryState.categories) {
                    result = this.getDefaultData(category);
                    if (result) {
                        return result;
                    }
                }
            }
        }
        return undefined;
    };

    render() {
        return (
            <TreeSelect
                showSearch
                defaultValue={this.props.categoryId}
                key={'id'}
                allowClear
                placeholder={this.props.t('Transaction.Category.SelectCategoryPlaceholder')}
                treeData={this.convertTreeData()}
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
    dispatchLoadCategories: api.loadCategories
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(CategoryTreeSelect));
