package de.raphaelmuesseler.financer.client.javafx.main.overview;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.Balance;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class OverviewController implements Initializable {
    public GridPane lastTransactionsGridPane;
    public GridPane balanceGridPane;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private List<Transaction> transactions;
    private LocalStorageImpl localStorage = LocalStorageImpl.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.showLoadingBox();

        this.user = this.localStorage.getLoggedInUser();

        this.transactions = (List<Transaction>) this.localStorage.readObject(LocalStorageImpl.TRANSACTIONS_FILE, "transactions");
        this.lastTransactionsGridPane.setVgap(8);
        if (this.transactions != null && this.transactions.size() > 0) {
            int counter = 0;
            for (Transaction transaction : this.transactions) {
                // LAST TRANSACTIONS
                if (counter > 5) {
                    break;
                }
                this.lastTransactionsGridPane.add(new Label(transaction.getPurpose() + " (" + transaction.getProduct() + ")"),
                        0, counter);

                Label amountLabel = Formatter.formatAmountLabel(transaction.getAmount());
                this.lastTransactionsGridPane.add(amountLabel, 1, counter);
                GridPane.setHalignment(amountLabel, HPos.RIGHT);
                GridPane.setHgrow(amountLabel, Priority.ALWAYS);
                GridPane.setVgrow(amountLabel, Priority.ALWAYS);
                counter++;
            }
        } else {
            this.lastTransactionsGridPane.add(new Label(I18N.get("noRecentTransactions")), 0, 0);
        }

        Balance balance = new Balance(LocalDate.now(), this.transactions,
                (List<FixedTransaction>) LocalStorageImpl.getInstance().readObject(LocalStorageImpl.TRANSACTIONS_FILE, "fixedTransactions"));

        this.balanceGridPane.add(new Label(I18N.get("fixedRevenue")), 0, 0);
        Label fixedRevenueLabel = Formatter.formatAmountLabel(balance.getFixedRevenueAmount());
        GridPane.setHalignment(fixedRevenueLabel, HPos.RIGHT);
        GridPane.setHgrow(fixedRevenueLabel, Priority.ALWAYS);
        GridPane.setVgrow(fixedRevenueLabel, Priority.ALWAYS);
        this.balanceGridPane.add(fixedRevenueLabel, 1, 0);

        this.balanceGridPane.add(new Label(I18N.get("variableRevenue")), 0, 1);
        Label variableRevenueLabel = Formatter.formatAmountLabel(balance.getVariableRevenueAmount());
        GridPane.setHalignment(variableRevenueLabel, HPos.RIGHT);
        GridPane.setHgrow(variableRevenueLabel, Priority.ALWAYS);
        GridPane.setVgrow(variableRevenueLabel, Priority.ALWAYS);
        this.balanceGridPane.add(variableRevenueLabel, 1, 1);

        this.balanceGridPane.add(new Label(I18N.get("fixedExpenses")), 0, 2);
        Label fixedExpensesLabel = Formatter.formatAmountLabel(balance.getFixedExpensesAmount());
        GridPane.setHalignment(fixedExpensesLabel, HPos.RIGHT);
        GridPane.setHgrow(fixedExpensesLabel, Priority.ALWAYS);
        GridPane.setVgrow(fixedExpensesLabel, Priority.ALWAYS);
        this.balanceGridPane.add(fixedExpensesLabel, 1, 2);

        this.balanceGridPane.add(new Label(I18N.get("variableExpenses")), 0, 3);
        Label variableExpensesLabel = Formatter.formatAmountLabel(balance.getVariableExpensesAmount());
        GridPane.setHalignment(variableExpensesLabel, HPos.RIGHT);
        GridPane.setHgrow(variableExpensesLabel, Priority.ALWAYS);
        GridPane.setVgrow(variableExpensesLabel, Priority.ALWAYS);
        this.balanceGridPane.add(variableExpensesLabel, 1, 3);

        Label balanceTextLabel = new Label(I18N.get("balance"));
        balanceTextLabel.setId("balance-label");
        this.balanceGridPane.add(balanceTextLabel, 0, 4);
        Label balanceLabel = Formatter.formatAmountLabel(balance.getBalance());
        balanceLabel.setId("balance-amount");
        GridPane.setHalignment(balanceLabel, HPos.RIGHT);
        GridPane.setHgrow(balanceLabel, Priority.ALWAYS);
        GridPane.setVgrow(balanceLabel, Priority.ALWAYS);
        this.balanceGridPane.add(balanceLabel, 1, 4);

        FinancerController.hideLoadingBox();
    }
}
