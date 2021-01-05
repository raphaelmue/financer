import React                              from 'react';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {CategoryReducerProps}             from '../../../../store/reducers/category.reducer';
import {Input, Tree}                      from 'antd';
import CategoryUtil                       from '../util';
import * as categoryApi                   from '../../../../store/api/category.api';
import * as userApi                       from '../../../../store/api/user.api';
import {UserReducerProps}                 from '../../../../store/reducers/user.reducers';
import {EventDataNode}                    from 'antd/lib/tree';
import {bindActionCreators, Dispatch}     from 'redux';

const {Search} = Input;

interface CategoryTreeComponentProps extends WithTranslation<'default'>, UserReducerProps, CategoryReducerProps {
    onSelect?: (categoryId?: number) => void,
    filterFixed?: boolean,
    filterVariable?: boolean
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
                    defaultExpandAll
                    onSelect={this.onSelect.bind(this)}
                    filterTreeNode={this.filter}
                    treeData={CategoryUtil.getTreeData(this.props.categoryState.categories, this.props.filterFixed, this.props.filterVariable)}/>
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
    dispatchLoadCategories: categoryApi.loadCategories,
    dispatchCreateCategory: categoryApi.createCategory,
    dispatchUpdateCategory: categoryApi.updateCategory,
    dispatchLoginUser: userApi.loginUser,
    dispatchRegisterUser: userApi.registerUser,
    dispatchDeleteToken: userApi.deleteToken,
    dispatchGetUser: userApi.getUser,
    dispatchUpdateUsersPassword: userApi.updateUsersPassword,
    dispatchUpdateUsersSettings: userApi.updateUsersSettings,
    dispatchUpdateUsersData: userApi.updateUsersData
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(CategoryTree));
