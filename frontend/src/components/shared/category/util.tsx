import {DataNode}                    from 'antd/lib/tree';
import {Category, CategoryClassEnum} from '../../../.openapi';
import i18next                       from 'i18next';

export default class CategoryUtil {

    static addRootCategories(categories: Category[]): Category[] {
        const rootCategories: Category[] = [];
        Object.values(CategoryClassEnum).forEach((value, index) => {
            rootCategories.push({
                id: -index - 1,
                name: i18next.t('Transaction.Category.CategoryClass.' + value.valueOf() as CategoryClassEnum)?.toString(),
                categoryClass: value,
                children: []
            });
        });

        categories.forEach(value => {
            switch (value.categoryClass) {
                case CategoryClassEnum.FIXEDREVENUE:
                    rootCategories[0].children!.push(value);
                    break;
                case CategoryClassEnum.VARIABLEREVENUE:
                    rootCategories[1].children!.push(value);
                    break;
                case CategoryClassEnum.FIXEDEXPENSES:
                    rootCategories[2].children!.push(value);
                    break;
                case CategoryClassEnum.VARIABLEEXPENSES:
                    rootCategories[3].children!.push(value);
                    break;
            }
        });

        return rootCategories;
    }

    static getCategoryClassFromCategory(categories: Category[], categoryId: number): CategoryClassEnum | undefined {
        return CategoryUtil.getCategoryById(categories, categoryId)?.categoryClass;
    }

    static getCategoryById(categories: Category[], categoryId: number): Category | undefined {
        for (const category of categories) {
            if (category.id === categoryId) {
                return category;
            }

            const foundCategory = this.getCategoryById(category.children || [], categoryId);
            if (foundCategory) {
                return foundCategory;
            }
        }
        return undefined;
    }

    static filterFixed(categories: Category[]): Category[] {
        return categories.filter(value =>
            value.categoryClass === CategoryClassEnum.FIXEDEXPENSES ||
            value.categoryClass === CategoryClassEnum.FIXEDREVENUE);
    }

    static filterVariable(categories: Category[]): Category[] {
        return categories.filter(value =>
            value.categoryClass === CategoryClassEnum.VARIABLEEXPENSES ||
            value.categoryClass === CategoryClassEnum.VARIABLEREVENUE);
    }

    static getTreeData(categories: Category[], filterFixed?: boolean, filterVariable?: boolean, rootSelectable?: boolean): DataNode[] {
        let rootCategories = CategoryUtil.addRootCategories(categories);
        if (filterFixed) {
            rootCategories = CategoryUtil.filterFixed(rootCategories);
        }
        if (filterVariable) {
            rootCategories = CategoryUtil.filterVariable(rootCategories);
        }
        return CategoryUtil.convertCategoriesToDataNode(rootCategories, rootSelectable || false);
    }

    static convertCategoriesToDataNode(categories: Category[], rootSelectable: boolean, query?: string, root?: Category): DataNode[] {
        if (categories.length > 0) {
            if (root) {
                const children: DataNode[] = [];
                if (root.children) {
                    root.children.forEach(value => {
                        children.push(...CategoryUtil.convertCategoriesToDataNode(categories, rootSelectable, query, value));
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
                    selectable: (root.id > 0 || rootSelectable),
                    icon: false,
                    isLeaf: children.length === 0,
                    // node is shown, if child is shown or root matches query
                    style: {display: isShown ? 'inherit' : 'none'}
                }];
            } else {
                const nodes: DataNode[] = [];
                categories.forEach(value => {
                    nodes.push(...CategoryUtil.convertCategoriesToDataNode(categories, rootSelectable, query, value));
                });
                return nodes;
            }
        }
        return [];
    }

    static insertCategoryIntoTree(categories: Category[], categoryToInsert: Category): void {
        for (const category of categories) {
            if (category.id === categoryToInsert.parentId) {
                if (category.children === undefined) {
                    category.children = [];
                }
                const index = category.children.findIndex(value => value.id === categoryToInsert.id);
                if (index > 0) {
                    category.children.splice(index, 1);
                }
                category.children.push(categoryToInsert);
                return;
            }

            this.insertCategoryIntoTree(category.children || [], categoryToInsert);
        }
    }

    static deleteCategoryFromTree(categories: Category[], categoryIdToDelete: number, root?: Category): void {
        if (root) {
            if (root.children) {
                const index = root.children.findIndex(value => value.id === categoryIdToDelete);
                if (index > 0) {
                    root.children.splice(index, 1);
                }
            }
        } else {
            const index = categories.findIndex(value => value.id === categoryIdToDelete);
            if (index > 0) {
                categories.splice(index, 1);
            } else {
                for (const category of categories) {
                    this.deleteCategoryFromTree(categories, categoryIdToDelete, category);
                }
            }
        }
    }
}
