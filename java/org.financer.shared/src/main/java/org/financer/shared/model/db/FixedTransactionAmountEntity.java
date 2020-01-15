package org.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fixed_transactions_amounts")
public class FixedTransactionAmountEntity implements DataEntity {
    private static final long serialVersionUID = -3901962625430867317L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fixed_transaction_id", nullable = false)
    private FixedTransactionEntity fixedTransaction;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "amount")
    private double amount;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected FixedTransactionEntity getFixedTransaction() {
        return fixedTransaction;
    }

    public void setFixedTransaction(FixedTransactionEntity fixedTransaction) {
        this.fixedTransaction = fixedTransaction;
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
}
