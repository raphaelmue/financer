package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "fixed_transactions")
public class FixedTransactionEntity implements DataEntity {
    private static final long serialVersionUID = 8295185142317654835L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cat_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "amount")
    private double amount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "product")
    private String product;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "is_variable")
    private boolean isVariable;

    @Column(name = "day")
    private int day;

    @OneToMany(mappedBy = "fixedTransaction")
    private Set<FixedTransactionAmountEntity> transactionAmounts;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public boolean getIsVariable() {
        return isVariable;
    }

    public void setIsVariable(boolean variable) {
        isVariable = variable;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Set<? extends FixedTransactionAmountEntity> getTransactionAmounts() {
        return transactionAmounts;
    }

    public void setTransactionAmounts(Set<FixedTransactionAmountEntity> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
    }
}
