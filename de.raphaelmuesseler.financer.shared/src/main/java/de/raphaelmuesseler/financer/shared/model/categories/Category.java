package de.raphaelmuesseler.financer.shared.model.categories;

import de.raphaelmuesseler.financer.shared.model.db.DatabaseCategory;

import java.io.Serializable;

public class Category extends DatabaseCategory implements Serializable {
    private static final long serialVersionUID = -5776418454648469541L;
    private String prefix = null;
    private BaseCategory.CategoryClass categoryClass;

    public Category() {
        super();
        this.categoryClass = BaseCategory.CategoryClass.getCategoryClassByIndex(this.getCategoryRoot());
    }

    public Category(String name) {
        this(-1, name, -1, null);
    }

    public Category(String name, BaseCategory.CategoryClass categoryClass) {
        this(-1, name, -1, categoryClass);
    }

    public Category(int id, String name, int parentId, BaseCategory.CategoryClass categoryClass) {
        this.setId(id);
        this.setName(name);
        this.setParentId(parentId);
        this.categoryClass = categoryClass;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public BaseCategory.CategoryClass getCategoryClass() {
        return categoryClass;
    }

    public void setCategoryClass(BaseCategory.CategoryClass categoryClass) {
        this.categoryClass = categoryClass;
    }

    @Override
    public String toString() {
        return this.getPrefix() + " " + this.getName();
    }
}
