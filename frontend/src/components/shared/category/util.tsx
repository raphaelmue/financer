import {DataNode}                            from 'antd/lib/tree';
import {Category, CategoryCategoryClassEnum} from '../../../.openapi';
import i18next                               from 'i18next';

export default class CategoryUtil {

    static addRootCategories(categories: Category[]): Category[] {
        const rootCategories: Category[] = [];
        Object.values(CategoryCategoryClassEnum).forEach((value, index) => {
            rootCategories.push({
                id: -index - 1,
                name: i18next.t('Transaction.Category.CategoryClass.' + value.valueOf() as CategoryCategoryClassEnum)?.toString(),
                categoryClass: value,
                children: []
            });
        });

        categories.forEach(value => {
            switch (value.categoryClass) {
                case CategoryCategoryClassEnum.FIXEDREVENUE:
                    rootCategories[0].children!.push(value);
                    break;
                case CategoryCategoryClassEnum.VARIABLEREVENUE:
                    rootCategories[1].children!.push(value);
                    break;
                case CategoryCategoryClassEnum.FIXEDEXPENSES:
                    rootCategories[2].children!.push(value);
                    break;
                case CategoryCategoryClassEnum.VARIABLEEXPENSES:
                    rootCategories[3].children!.push(value);
                    break;
            }
        });

        return rootCategories;
    }

    static filterFixed(categories: Category[]): Category[] {
        return categories.filter(value =>
            value.categoryClass === CategoryCategoryClassEnum.FIXEDEXPENSES ||
            value.categoryClass === CategoryCategoryClassEnum.FIXEDREVENUE);
    }

    static filterVariable(categories: Category[]): Category[] {
        return categories.filter(value =>
            value.categoryClass === CategoryCategoryClassEnum.VARIABLEEXPENSES ||
            value.categoryClass === CategoryCategoryClassEnum.VARIABLEREVENUE);
    }

    static convertCategoriesToDataNode(categories: Category[], query?: string, root?: Category): DataNode[] {
        if (categories.length > 0) {
            if (root) {
                const children: DataNode[] = [];
                if (root.children) {
                    root.children.forEach(value => {
                        children.push(...CategoryUtil.convertCategoriesToDataNode(categories, query, value));
                    });
                }

                // root is shown, if any child is shown
                // otherwise, root is hidden
                const isShown: boolean = (children.length > 0 && children.filter(value => value.style?.display !== 'none').length > 0)
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
                const nodes: DataNode[] = [];
                categories.forEach(value => {
                    nodes.push(...CategoryUtil.convertCategoriesToDataNode(categories, query, value));
                });
                return nodes;
            }
        }
        return [];
    }
}
