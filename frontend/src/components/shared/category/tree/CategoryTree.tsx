import React                              from 'react';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {CategoryReducerProps}             from '../../../../store/reducers/category.reducer';
import {Input, Tree}                      from 'antd';
import CategoryUtil                       from '../util';
import * as api                           from '../../../../store/api/category.api';
import {UserReducerProps}                 from '../../../../store/reducers/user.reducers';
import {DataNode, EventDataNode}          from 'antd/lib/tree';

const {Search} = Input;

interface CategoryTreeComponentProps extends WithTranslation, UserReducerProps, CategoryReducerProps {
    onSelect?: (categoryId?: number) => void
}

interface CategoryTreeComponentState {
    searchQuery?: string,
}

class CategoryTree extends React.Component<CategoryTreeComponentProps, CategoryTreeComponentState> {

    private $loadCategories = new Promise<void>(resolve => {
        if (this.props.userState.user?.id) {
            this.props.dispatchLoadCategories({userId: this.props.userState.user.id}, () => resolve());
        }
    });

    constructor(props: CategoryTreeComponentProps) {
        super(props);

        this.$loadCategories.then();

        this.state = {
            searchQuery: undefined,
        };
    }

    getTreeData(): DataNode[] {
        return CategoryUtil.convertCategoriesToDataNode(
            CategoryUtil.filterFixed(
                CategoryUtil.addRootCategories(
                    this.props.categoryState.categories)), this.state.searchQuery);
    }

    filter = (treeNode: EventDataNode): boolean => {
        return (this.state.searchQuery === ''
            && treeNode.title?.toString().toLocaleLowerCase().includes(this.state.searchQuery.toLowerCase())) || false;
    };

    onSelect(selectedKeys: React.Key[]) {
        if (selectedKeys.length > 0 && this.props.onSelect !== undefined) {
            this.props.onSelect(parseInt(selectedKeys[0].toString()));
        }
    }

    render() {
        return (
            <div>
                <Search style={{marginBottom: 8}}
                        placeholder={this.props.t('Form.Input.Search')?.toString()}
                        onChange={event => this.setState({searchQuery: event.target.value})}/>
                <Tree
                    showLine
                    onSelect={this.onSelect.bind(this)}
                    defaultExpandAll
                    filterTreeNode={this.filter}
                    treeData={this.getTreeData()}/>
            </div>
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

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(CategoryTree));
