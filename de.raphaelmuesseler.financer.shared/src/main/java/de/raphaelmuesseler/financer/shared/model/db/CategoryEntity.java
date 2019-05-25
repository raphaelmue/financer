package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.util.Comparator;

@Entity
@Table(name = "users_categories")
public class CategoryEntity implements Comparable<CategoryEntity>, DataEntity {
    private final static long serialVersionUID = 5491420625985358596L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(targetEntity = UserEntity.class)
    private UserEntity user;

    @Column(name = "cat_id")
    private int categoryRoot;

    @Column(name = "parent_id")
    private int parentId;

    @Column(name = "name")
    private String name;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getCategoryRoot() {
        return categoryRoot;
    }

    public void setCategoryRoot(int categoryClass) {
        this.categoryRoot = categoryClass;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(CategoryEntity o) {
        return Comparator.comparing(CategoryEntity::getCategoryRoot)
                .thenComparing(CategoryEntity::getParentId)
                .thenComparing(CategoryEntity::getId)
                .compare(this, o);
    }
}
