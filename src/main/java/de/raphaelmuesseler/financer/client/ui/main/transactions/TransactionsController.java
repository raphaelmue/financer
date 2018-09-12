package de.raphaelmuesseler.financer.client.ui.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.util.collections.CollectionUtil;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.ConnectException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionsController implements Initializable {


    public JFXButton refreshTransactionsBtn;
    public JFXButton newTransactionBtn;
    public JFXButton editTransactionBtn;
    public JFXButton deleteTransactionBtn;
    public TableView<Transaction> transactionsTableView;
    public JFXButton refreshFixedTransactionsBtn;
    public JFXButton newFixedTransactionBtn;
    public JFXButton editFixedTransactionBtn;
    public JFXButton deleteFixedTransactionBtn;
    public JFXListView categoriesListView;
    public JFXListView fixedTransactionsListView;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ObservableList<Transaction> transactions;
    private ObservableList<FixedTransaction> fixedTransactions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.user = LocalStorage.getLoggedInUser();

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.refreshTransactionsBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.REFRESH));
        this.refreshTransactionsBtn.setGraphicTextGap(8);
        this.newTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.PLUS));
        this.newTransactionBtn.setGraphicTextGap(8);
        this.editTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
        this.editTransactionBtn.setGraphicTextGap(8);
        this.deleteTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
        this.deleteTransactionBtn.setGraphicTextGap(8);

        this.loadTransactionsTable();
        this.loadFixedTransactionsTable();
    }

    private void loadTransactionsTable() {
        TableColumn<Transaction, Category> categoryColumn = new TableColumn<>(I18N.get("category"));
        TableColumn<Transaction, Integer> idColumn = new TableColumn<>(I18N.get("id"));
        TableColumn<Transaction, Date> valueDateColumn = new TableColumn<>(I18N.get("valueDate"));
        TableColumn<Transaction, Double> amountColumn = new TableColumn<>(I18N.get("amount"));
        TableColumn<Transaction, String> productColumn = new TableColumn<>(I18N.get("product"));
        TableColumn<Transaction, String> purposeColumn = new TableColumn<>(I18N.get("purpose"));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        valueDateColumn.setCellValueFactory(new PropertyValueFactory<>("valueDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        idColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(18));
        valueDateColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6));
        amountColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(12));
        categoryColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6));
        productColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(4));
        purposeColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(4));

        valueDateColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.transactionsTableView.getColumns().addAll(idColumn, valueDateColumn, amountColumn, categoryColumn, productColumn, purposeColumn);

        this.handleRefreshTransactions();
    }

    private void loadFixedTransactionsTable() {
        SerialTreeItem<Category> tree = SerialTreeItem.fromJson((String) LocalStorage.readObject(LocalStorage.PROFILE_FILE).get(0),
                Category.class);
        tree.numberItemsByValue((result, prefix) -> {
            if (!result.getValue().isKey()) {
                result.getValue().setName(prefix + " " + result.getValue().getName());
            }
        });
        tree.traverse(treeItem -> {
            if ((treeItem.getValue().getRootId() != -1 && (treeItem.getValue().getRootId() % 2) == 0) ||
                    (treeItem.getValue().getRootId() == -1 && (treeItem.getValue().getParentId() % 2) == 0)) {
                categoriesListView.getItems().add(treeItem.getValue());
            }
        });

        this.categoriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                showFixedTransactions((Category) newValue));

        this.categoriesListView.setCellFactory(param -> new CategoryListViewImpl());

        this.handleRefreshFixedTransactions();
    }

    public void handleRefreshTransactions() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", this.user);

        this.executor.execute(new ServerRequestHandler("getTransactions", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                transactions = FXCollections.observableArrayList((List<Transaction>) result.getResult());
                LocalStorage.writeObject(LocalStorage.TRANSACTIONS_FILE, result.getResult());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    // TODO set offline
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
                List<Object> result = LocalStorage.readObject(LocalStorage.TRANSACTIONS_FILE);
                if (result != null && result.size() > 0) {
                    transactions = CollectionUtil.castObjectListToObservable((List<Object>) result.get(0));
                }
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> transactionsTableView.setItems(transactions));
            }
        }));
    }

    public void handleNewTransaction() {
        Transaction transaction = new TransactionDialog(null).showAndGetResult();
        if (transaction != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("transaction", transaction);

            this.executor.execute(new ServerRequestHandler("addTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    Platform.runLater(() -> {
                        // removing numbers in category's name
                        transaction.getCategory().setName(transaction.getCategory().getName().substring(
                                transaction.getCategory().getName().indexOf(" ") + 1));
                        transactionsTableView.getItems().add(transaction);
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }

    public void handleEditTransaction() {
        Transaction transaction = new TransactionDialog(this.transactionsTableView.getSelectionModel().getSelectedItem())
                .showAndGetResult();
        if (transaction != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("transaction", transaction);

            this.executor.execute(new ServerRequestHandler("updateTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    handleRefreshTransactions();
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }

    public void handleDeleteTransaction() {
        Transaction transaction = this.transactionsTableView.getSelectionModel().getSelectedItem();
        if (transaction != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("transaction", transaction);

            this.executor.execute(new ServerRequestHandler("deleteTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    Platform.runLater(() -> transactionsTableView.getItems().remove(transaction));
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }

    public void handleRefreshFixedTransactions() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", this.user);

        this.executor.execute(new ServerRequestHandler("getFixedTransactions", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                fixedTransactions = FXCollections.observableArrayList((List<FixedTransaction>) result.getResult());
                LocalStorage.writeObject(LocalStorage.TRANSACTIONS_FILE, result.getResult());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    // TODO set offline
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
                List<Object> result = LocalStorage.readObject(LocalStorage.TRANSACTIONS_FILE);
                if (result != null && result.size() > 1) {
                    fixedTransactions = CollectionUtil.castObjectListToObservable((List<Object>) result.get(1));
                }
            }
        }));
    }

    public void handleNewFixedTransaction() {
    }

    public void handleEditFixedTransaction() {
    }

    public void handleDeleteFixedTransaction() {
    }

    private void showFixedTransactions(Category category) {
        // TODO show fixed transactions for specific category
        this.fixedTransactionsListView.getItems().clear();
        for (FixedTransaction transaction : this.fixedTransactions) {
            if (transaction.getCategory().getId() == category.getId()) {
                this.fixedTransactionsListView.getItems().add(transaction);
            }
        }
    }

    private final class CategoryListViewImpl extends ListCell<Category> {
        private BorderPane borderPane;
        private Label categoryLabel, amountLabel;

        @Override
        protected void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);

            if(empty || item == null) {
                setGraphic(null);
            } else {
                this.initListCell();
                this.categoryLabel.setText(item.getName());
                if (fixedTransactions != null && fixedTransactions.size() > 0) {
                    for (FixedTransaction fixedTransaction : fixedTransactions) {
                        if (fixedTransaction.getCategory().getId() == item.getId() && !item.isKey()) {
                            this.amountLabel.setText(String.valueOf(fixedTransaction.getAmount()));
                            if (fixedTransaction.getAmount() < 0) {
                                this.amountLabel.getStyleClass().add("neg-amount");
                            } else {
                                this.amountLabel.getStyleClass().add("pos-amount");
                            }
                            break;
                        }
                    }
                }
tus

                setGraphic(this.borderPane);
            }
        }

        private void initListCell() {
            this.borderPane = new BorderPane();
            this.borderPane.getStyleClass().add("categories-list-item");
            this.categoryLabel = new Label();
            this.categoryLabel.getStyleClass().add("category-label");
            this.amountLabel = new Label();
            this.amountLabel.getStyleClass().add("amount-label");

            this.borderPane.setLeft(this.categoryLabel);
            this.borderPane.setRight(this.amountLabel);
        }
    }
}
