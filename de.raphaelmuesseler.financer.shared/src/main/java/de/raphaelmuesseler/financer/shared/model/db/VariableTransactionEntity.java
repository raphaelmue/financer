package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "transactions")
public class VariableTransactionEntity implements DataEntity {
    private final static long serialVersionUID = -118658876074097774L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cat_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "amount")
    private double amount;

    @Column(name = "product")
    private String product;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "shop")
    private String shop;

    @OneToMany(mappedBy = "transaction")
    private Set<AttachmentEntity> attachments;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Set<? extends AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }
}
