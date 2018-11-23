package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.AmountProvider;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class FixedTransaction extends AbstractTransaction {

    private LocalDate startDate, endDate;
    private boolean isVariable;
    private int day;
    private final List<TransactionAmount> transactionAmounts;

    public FixedTransaction(int id, double amount, CategoryTree category, String product, String purpose, LocalDate startDate,
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

    @Override
    public double getAmount(LocalDate localDate) {
        double amount = 0;

        if (this.endDate == null || this.endDate.compareTo(LocalDate.now()) >= 0) {
            if (this.isVariable && this.transactionAmounts != null) {
                for (AmountProvider amountProvider : this.transactionAmounts) {
                    amount += amountProvider.getAmount(localDate);
                }
            }
            if (!this.isVariable) {
                amount = super.getAmount();
            }
        }

        return amount;
    }

    @Override
    public double getAmount() {
        double amount = 0;

        if (this.endDate == null || this.endDate.compareTo(LocalDate.now()) >= 0) {
            if (this.isVariable && this.transactionAmounts != null) {
                for (AmountProvider amountProvider : this.transactionAmounts) {
                    amount += amountProvider.getAmount();
                }
            }
            if (!this.isVariable) {
                amount = super.getAmount();
            }
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        double amount = 0;

        if (this.endDate == null || this.endDate.compareTo(startDate) >= 0) {
            if (this.isVariable && this.transactionAmounts != null) {
                for (AmountProvider amountProvider : this.transactionAmounts) {
                    amount += amountProvider.getAmount(startDate, endDate);
                }
            } else {
                LocalDate maxStartDate, minEndDate;
                if (this.endDate == null) {
                    minEndDate = endDate;
                } else {
                    if (endDate.compareTo(this.endDate) <= 0) {
                        minEndDate = endDate;
                    } else {
                        minEndDate = this.endDate;
                    }
                }

                if (startDate.compareTo(this.startDate) >= 0) {
                    maxStartDate = startDate;
                } else {
                    maxStartDate = this.startDate;
                }
                amount = super.getAmount() * DateUtil.getMonthDifference(maxStartDate, minEndDate);
            }
        }

        return amount;
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