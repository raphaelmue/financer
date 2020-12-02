import {DataNode}                            from 'antd/lib/tree';
import {Category, CategoryCategoryClassEnum} from '../../../.openapi';
import i18next                               from 'i18next';

export default class CategoryUtil {

    static addRootCategories(categories: Category[]): Category[] {
        let rootCategories: Category[] = [];
        Object.keys(CategoryCategoryClassEnum).forEach((value, index) => {
            rootCategories.push({
                id: -index - 1,
                name: i18next.t('Transaction.Category.CategoryClass.' + value as CategoryCategoryClassEnum),
                categoryClass: value as CategoryCategoryClassEnum,
                children: new Set()
            });
        });

        categories.forEach(value => {
            switch (value.categoryClass) {
                case CategoryCategoryClassEnum.FixedRevenue:
                    rootCategories[0].children!.add(value);
                    break;
                case CategoryCategoryClassEnum.VariableRevenue:
                    rootCategories[1].children!.add(value);
                    break;
                case CategoryCategoryClassEnum.FixedExpenses:
                    rootCategories[2].children!.add(value);
                    break;
                case CategoryCategoryClassEnum.VariableExpenses:
                    rootCategories[3].children!.add(value);
                    break;
            }
        });

        return rootCategories;
    }

    static filterFixed(categories: Category[]): Category[] {
        return categories.filter(value =>
            value.categoryClass.valueOf() === 'FixedExpenses' ||
            value.categoryClass.valueOf() === 'FixedRevenue');
    }

    static filterVariable(categories: Category[]): Category[] {
        return categories.filter(value =>
            value.categoryClass.valueOf() === 'VariableExpenses' ||
            value.categoryClass.valueOf() === 'VariableRevenue');
    }

    static convertCategoriesToDataNode(categories: Category[], query?: string, root?: Category): DataNode[] {
        if (categories.length > 0) {
            if (root) {
                let children: DataNode[] = [];
                if (root.children) {
                    root.children.forEach(value => {
                        children.push(...CategoryUtil.convertCategoriesToDataNode(categories, query, value));
                    });
                }

                // root is shown, if any child is shown
                // otherwise, root is hidden
                let isShown: boolean = (children.length > 0 && children.filter(value => value.style?.display !== 'none').length > 0)
                    || ((query && root.name.toLowerCase().includes(query.toLowerCase())) || !query);

                return [{
                    key: root.id,
                    title: root.name,
                    children: children,
                    // root categories cannot be selected
                    selectable: (root.id > 0),
                    icon: false,
                    isLeaf: children.length === 0,
                    // node is shown, if child is shown or root matches query
                    style: {display: isShown ? 'inherit' : 'none'}
                }];
            } else {
                let nodes: DataNode[] = [];
                categories.forEach(value => {
                    nodes.push(...CategoryUtil.convertCategoriesToDataNode(categories, query, value));
                });
                return nodes;
            }
        }
        return [];
    }
}
