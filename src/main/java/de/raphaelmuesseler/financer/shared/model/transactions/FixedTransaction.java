package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.util.date.Month;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class FixedTransaction extends AbstractTransaction {

    private LocalDate startDate, endDate;
    private boolean isVariable;
    private int day;
    private final List<TransactionAmount> transactionAmounts;

    public FixedTransaction(int id, double amount, Category category, String product, String purpose, LocalDate startDate,
                     LocalDate endDate, boolean isVariable, int day, List<TransactionAmount> transactionAmounts) {
        super(id, amount, category, product, purpose);
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVariable = isVariable;
        this.day = day;
        this.transactionAmounts = transactionAmounts;
    }

    public void sortTransactionAmounts() {
        this.getTransactionAmounts().sort(Comparator.comparing(TransactionAmount::getValueDate).reversed());
    }

    public TransactionAmount getAmountByMonth(Month month) {
        return null;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isVariable() {
        return isVariable;
    }

    public int getDay() {
        return day;
    }

    public List<TransactionAmount> getTransactionAmounts() {
        return transactionAmounts;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setVariable(boolean variable) {
        isVariable = variable;
    }

    public void setDay(int day) {
        this.day = day;
    }
}