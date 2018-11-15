package de.raphaelmuesseler.financer.client.javafx.main.overview;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.Tree;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {
    public GridPane lastTransactionsGridPane;
    public GridPane balanceGridPane;

    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BaseCategory categories = (BaseCategory) this.localStorage.readObject("categories");

        if (categories == null) {
            categories = new BaseCategory();
        }

        List<Transaction> transactions = this.localStorage.readList("transactions");
        this.lastTransactionsGridPane.setVgap(8);
        if (transactions != null && transactions.size() > 0) {
            int counter = 0;
            for (Transaction transaction : transactions) {
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

        double balanceAmount = 0;
        int counter = 0;
        for (Tree<Category> root : categories.getChildren()) {
            this.balanceGridPane.add(new Label(I18N.get(((CategoryTree) root).getCategoryClass().getName())), 0, counter);
            Label baseCategoryLabel = Formatter.formatAmountLabel(((CategoryTree) root).getAmount(LocalDate.now()));
            balanceAmount += ((CategoryTree) root).getAmount(LocalDate.now());
            GridPane.setHalignment(baseCategoryLabel, HPos.RIGHT);
            GridPane.setHgrow(baseCategoryLabel, Priority.ALWAYS);
            GridPane.setVgrow(baseCategoryLabel, Priority.ALWAYS);
            this.balanceGridPane.add(baseCategoryLabel, 1, counter);
            counter++;
        }

        Label balanceTextLabel = new Label(I18N.get("balance"));
        balanceTextLabel.setId("balance-label");
        this.balanceGridPane.add(balanceTextLabel, 0, 4);
        Label balanceLabel = Formatter.formatAmountLabel(balanceAmount);
        balanceLabel.setId("balance-amount");
        GridPane.setHalignment(balanceLabel, HPos.RIGHT);
        GridPane.setHgrow(balanceLabel, Priority.ALWAYS);
        GridPane.setVgrow(balanceLabel, Priority.ALWAYS);
        this.balanceGridPane.add(balanceLabel, 1, 4);
    }
}
