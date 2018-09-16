package de.raphaelmuesseler.financer.client.ui.main.overview;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.main.FinancerController;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class OverviewController implements Initializable {
    public GridPane lastTransactionsGridPane;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.showLoadingBox();

        this.user = LocalStorage.getLoggedInUser();

        List<Transaction> transactions = (List<Transaction>) LocalStorage.readObject(LocalStorage.TRANSACTIONS_FILE, "transactions");
        this.lastTransactionsGridPane.setVgap(8);
        if (transactions != null && transactions.size() > 0) {
            int counter = 0;
            for (Transaction transaction : transactions) {
                if (counter > 5) {
                    break;
                }
                this.lastTransactionsGridPane.add(new Label(transaction.getPurpose() + " (" + transaction.getProduct() + ")"),
                        0, counter);

                Label amountLabel = new Label(Double.toString(transaction.getAmount()));
                if (transaction.getAmount() < 0) {
                    amountLabel.getStyleClass().add("neg-amount");
                } else {
                    amountLabel.getStyleClass().add("pos-amount");
                }
                this.lastTransactionsGridPane.add(amountLabel, 1, counter);
                GridPane.setHalignment(amountLabel, HPos.RIGHT);
                GridPane.setHgrow(amountLabel, Priority.ALWAYS);
                GridPane.setVgrow(amountLabel, Priority.ALWAYS);
                counter++;
            }
        } else {
            this.lastTransactionsGridPane.add(new Label(I18N.get("noRecentTransactions")), 0, 0);
        }

        FinancerController.hideLoadingBox();
    }
}
