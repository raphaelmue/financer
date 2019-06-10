package de.raphaelmuesseler.financer.client.javafx.main.overview;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.client.javafx.main.transactions.TransactionAmountDialog;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class OverviewController implements Initializable {
    @FXML
    public GridPane lastTransactionsGridPane;
    @FXML
    public GridPane balanceGridPane;
    @FXML
    public GridPane upcomingFixedTransactionGridPane;

    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private JavaFXFormatter formatter = new JavaFXFormatter(localStorage);
    private BaseCategory categories;
    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            categories = (BaseCategory) this.localStorage.readObject("categories");
            user = (User) localStorage.readObject("user");

            loadLatestTransactions();
            loadBalance();
            loadUpcomingFixedTransactions();

            FinancerController.getInstance().hideLoadingBox();
        }).start();
    }

    private void loadLatestTransactions() {
        List<VariableTransaction> transactions = new ArrayList<>();
        categories.traverse(categoryTree -> {
            for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                if (transaction instanceof VariableTransaction) {
                    transactions.add((VariableTransaction) transaction);
                }
            }
        });
        transactions.sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
        this.lastTransactionsGridPane.setVgap(8);
        if (!transactions.isEmpty()) {
            int counter = 0;
            for (VariableTransaction transaction : transactions) {
                // LAST TRANSACTIONS
                if (counter >= 5) {
                    break;
                }
                final int _counter = counter;
                Platform.runLater(() -> this.lastTransactionsGridPane.add(new Label(transaction.getCategoryTree().getValue().getName()),
                        0, _counter));

                Label amountLabel = formatter.formatAmountLabel(transaction.getAmount());
                Platform.runLater(() -> this.lastTransactionsGridPane.add(amountLabel, 1, _counter));
                GridPane.setHalignment(amountLabel, HPos.RIGHT);
                GridPane.setHgrow(amountLabel, Priority.ALWAYS);
                GridPane.setVgrow(amountLabel, Priority.ALWAYS);
                counter++;
            }
        } else {
            Platform.runLater(() -> this.lastTransactionsGridPane.add(new Label(I18N.get("noRecentTransactions")), 0, 0));
        }
    }

    private void loadBalance() {
        double balanceAmount = 0;
        int counter = 0;
        for (CategoryTree root : categories.getChildren()) {
            final int _counter = counter;
            Platform.runLater(() -> balanceGridPane.add(new Label(I18N.get(root.getValue().getCategoryClass().getName())), 0, _counter));
            Label baseCategoryLabel = formatter.formatAmountLabel(root.getAmount(LocalDate.now()));
            balanceAmount += root.getAmount(LocalDate.now());
            GridPane.setHalignment(baseCategoryLabel, HPos.RIGHT);
            GridPane.setHgrow(baseCategoryLabel, Priority.ALWAYS);
            GridPane.setVgrow(baseCategoryLabel, Priority.ALWAYS);
            Platform.runLater(() -> balanceGridPane.add(baseCategoryLabel, 1, _counter));
            counter++;
        }

        Label balanceTextLabel = new Label(I18N.get("balance"));
        balanceTextLabel.setId("balance-label");
        Platform.runLater(() -> balanceGridPane.add(balanceTextLabel, 0, 4));
        Label balanceLabel = formatter.formatAmountLabel(balanceAmount);
        balanceLabel.setId("balance-amount");
        GridPane.setHalignment(balanceLabel, HPos.RIGHT);
        GridPane.setHgrow(balanceLabel, Priority.ALWAYS);
        GridPane.setVgrow(balanceLabel, Priority.ALWAYS);
        Platform.runLater(() -> balanceGridPane.add(balanceLabel, 1, 4));
    }

    private void loadUpcomingFixedTransactions() {
        List<FixedTransaction> transactions = new ArrayList<>();
        categories.traverse(categoryTree -> {
            for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                if (transaction instanceof FixedTransaction && ((FixedTransaction) transaction).isActive() &&
                        (transaction.getAmount(LocalDate.now()) == 0)) {
                    transactions.add((FixedTransaction) transaction);
                }
            }
        });

        if (!transactions.isEmpty()) {
            int counter = 0;
            for (FixedTransaction transaction : transactions) {
                // LAST TRANSACTIONS
                if (counter >= 5) {
                    break;
                }
                final int _counter = counter;
                if (transaction.getIsVariable()) {
                    Hyperlink link = new Hyperlink(transaction.getCategoryTree().getValue().getName());
                    link.setOnAction(event -> addTransactionAmount(transaction));
                    Platform.runLater(() -> this.upcomingFixedTransactionGridPane.add(link, 0, _counter));
                } else {
                    Platform.runLater(() -> this.upcomingFixedTransactionGridPane.add(new Label(transaction.getCategoryTree().getValue().getName()),
                            0, _counter));
                }

                Label dayLabel = new Label(formatter.formatDate(LocalDate.now().withDayOfMonth(transaction.getDay())));
                Platform.runLater(() -> this.upcomingFixedTransactionGridPane.add(dayLabel, 1, _counter));
                GridPane.setHalignment(dayLabel, HPos.RIGHT);
                GridPane.setHgrow(dayLabel, Priority.ALWAYS);
                GridPane.setVgrow(dayLabel, Priority.ALWAYS);
                counter++;
            }
        } else {
            Platform.runLater(() -> this.upcomingFixedTransactionGridPane.add(new Label(I18N.get("noUpcomingTransactions")), 0, 0));
        }
    }

    private void addTransactionAmount(FixedTransaction transaction) {
        TransactionAmount transactionAmount = new TransactionAmount(0, 0.0, LocalDate.now());
        transactionAmount.setFixedTransaction(transaction);
        transactionAmount = new TransactionAmountDialog(transactionAmount, new ArrayList<>(transaction.getTransactionAmounts()))
                .showAndGetResult();

        if (transactionAmount != null) {
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("transactionAmount", transactionAmount);
            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "addTransactionAmount", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    transaction.getTransactionAmounts().add((TransactionAmount) result.getResult());
                    localStorage.writeObject("categories", categories);
                    Platform.runLater(() -> upcomingFixedTransactionGridPane.getChildren().clear());
                    loadUpcomingFixedTransactions();
                }

                @Override
                public void onFailure(Exception exception) {
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }
}
