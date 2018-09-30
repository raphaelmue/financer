package de.raphaelmuesseler.financer.shared.model.transactions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Balance {
    private List<Transaction> transactions;
    private List<FixedTransaction> fixedTransactions;
    private double fixedRevenueAmount, variableRevenueAmount, fixedExpensesAmount, variableExpensesAmount;

    public Balance(LocalDate localDate, List<Transaction> transactions, List<FixedTransaction> fixedTransactions) {
        this.transactions = transactions;
        this.fixedTransactions = fixedTransactions;

        for (Transaction transaction : this.getTransactionsByMonth(localDate)) {
            if (transaction.getAmount() < 0) {
                this.variableExpensesAmount += transaction.getAmount();
            } else {
                this.variableRevenueAmount += transaction.getAmount();
            }
        }

        for (FixedTransaction fixedTransaction : this.getFixedTransactionsByMonth(localDate)) {
            if (fixedTransaction.getAmount() < 0) {
                this.fixedExpensesAmount += fixedTransaction.getAmount();
            } else {
                this.fixedRevenueAmount += fixedTransaction.getAmount();
            }
        }
    }

    public List<FixedTransaction> getFixedTransactionsByMonth(LocalDate localDate) {
        List<FixedTransaction> result = new ArrayList<>();
        if (this.fixedTransactions != null) {
            for (FixedTransaction fixedTransaction : this.fixedTransactions) {
                if (fixedTransaction.getEndDate() == null && (fixedTransaction.getDay() == 0 ||
                        fixedTransaction.getDay() > 0 && fixedTransaction.getDay() <= localDate.getDayOfMonth())) {
                    result.add(fixedTransaction);
                }
            }
        }
        return result;
    }

    public List<Transaction> getTransactionsByMonth(LocalDate localDate) {
        List<Transaction> result = new ArrayList<>();
        if (this.transactions != null) {
            for (Transaction transaction : this.transactions) {
                if (transaction.getValueDate().compareTo(localDate.withDayOfMonth(1)) >= 0 &&
                        transaction.getValueDate().compareTo(localDate.withDayOfMonth(localDate.lengthOfMonth())) <= 0) {
                    result.add(transaction);
                }
            }
        }
        return result;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<FixedTransaction> getFixedTransactions() {
        return fixedTransactions;
    }

    public double getFixedExpensesAmount() {
        return fixedExpensesAmount;
    }

    public double getFixedRevenueAmount() {
        return fixedRevenueAmount;
    }

    public double getVariableRevenueAmount() {
        return variableRevenueAmount;
    }

    public double getVariableExpensesAmount() {
        return variableExpensesAmount;
    }

    public double getBalance() {
        return this.fixedRevenueAmount + this.variableRevenueAmount + this.fixedExpensesAmount + this.variableExpensesAmount;
    }
}
