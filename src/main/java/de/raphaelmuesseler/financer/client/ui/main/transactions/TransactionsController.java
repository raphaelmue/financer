package de.raphaelmuesseler.financer.client.ui.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerConfirmDialog;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.ConnectException;
import java.net.URL;
import java.time.LocalDate;
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

        this.refreshFixedTransactionsBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.REFRESH));
        this.refreshFixedTransactionsBtn.setGraphicTextGap(8);
        this.newFixedTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.PLUS));
        this.newFixedTransactionBtn.setGraphicTextGap(8);
        this.newFixedTransactionBtn.setDisable(true);
        this.editFixedTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
        this.editFixedTransactionBtn.setGraphicTextGap(8);
        this.editFixedTransactionBtn.setDisable(true);
        this.deleteFixedTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
        this.deleteFixedTransactionBtn.setGraphicTextGap(8);
        this.deleteFixedTransactionBtn.setDisable(true);

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
        if (LocalStorage.readObject(LocalStorage.PROFILE_FILE) != null) {
            SerialTreeItem<Category> tree = SerialTreeItem.fromJson((String) LocalStorage.readObject(LocalStorage.PROFILE_FILE).get(0),
                    Category.class);

            for (TreeItem<Category> subTree : tree.getChildren()) {
                SerialTreeItem<Category> serialSubTree = (SerialTreeItem) subTree;
                if ((serialSubTree.getValue().getRootId() != -1 && (serialSubTree.getValue().getRootId() % 2) == 0) ||
                        (serialSubTree.getValue().getRootId() == -1 && (serialSubTree.getValue().getParentId() % 2) == 0)) {
                    serialSubTree.numberItemsByValue((result, prefix) -> {
                        if (!result.getValue().isKey()) {
                            result.getValue().setName(prefix + " " + result.getValue().getName());
                        }
                    });
                    serialSubTree.traverse(treeItem -> categoriesListView.getItems().add(treeItem.getValue()));
                }
            }

            this.categoriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                showFixedTransactions((Category) newValue);
                if (!((Category) newValue).isKey()) {
                    newFixedTransactionBtn.setDisable(false);
                } else {
                    newFixedTransactionBtn.setDisable(true);
                }
                editFixedTransactionBtn.setDisable(true);
                deleteFixedTransactionBtn.setDisable(true);
            });

            this.fixedTransactionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                editFixedTransactionBtn.setDisable(false);
                deleteFixedTransactionBtn.setDisable(false);
            });

            this.handleRefreshFixedTransactions();
        }
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

            @Override
            public void onAfter() {
                Platform.runLater(() -> {
                    showFixedTransactions((Category) categoriesListView.getSelectionModel().getSelectedItem());
                    categoriesListView.setCellFactory(param -> new CategoryListViewImpl());
                });
            }
        }));
    }

    public void handleNewFixedTransaction() {
        FixedTransaction fixedTransaction = new FixedTransactionDialog(null,
                ((Category) this.categoriesListView.getSelectionModel().getSelectedItem()))
                .showAndGetResult();
        if (fixedTransaction != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("fixedTransaction", fixedTransaction);

            this.executor.execute(new ServerRequestHandler("addFixedTransactions", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    handleRefreshFixedTransactions();
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }

    public void handleEditFixedTransaction() {
        FixedTransaction fixedTransaction = new FixedTransactionDialog(
                (FixedTransaction) this.fixedTransactionsListView.getSelectionModel().getSelectedItem(),
                ((Category) this.categoriesListView.getSelectionModel().getSelectedItem()))
                .showAndGetResult();
        if (fixedTransaction != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fixedTransaction", fixedTransaction);

            this.executor.execute(new ServerRequestHandler("updateFixedTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    handleRefreshFixedTransactions();
                }
            }));
        }
    }

    public void handleDeleteFixedTransaction() {
        boolean result = new FinancerConfirmDialog(I18N.get("confirmDeleteFixedTransaction")).showAndGetResult();
        if (result) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fixedTransaction", this.fixedTransactionsListView.getSelectionModel().getSelectedItem());

            this.executor.execute(new ServerRequestHandler("deleteFixedTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    handleRefreshFixedTransactions();
                }
            }));
        }
    }

    private void showFixedTransactions(Category category) {
        if (category != null) {
            this.fixedTransactionsListView.getItems().clear();
            for (FixedTransaction transaction : this.fixedTransactions) {
                if (transaction.getCategory().getId() == category.getId()) {
                    this.fixedTransactionsListView.getItems().add(transaction);
                }
            }
        }
        this.fixedTransactionsListView.setCellFactory(param -> new FixedTransactionListCellImpl());
    }

    private final class CategoryListViewImpl extends ListCell<Category> {
        private BorderPane borderPane;
        private Label categoryLabel, amountLabel;

        @Override
        protected void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                this.initListCell();
                this.categoryLabel.setText(item.getName());
                if (fixedTransactions != null && fixedTransactions.size() > 0) {
                    for (FixedTransaction fixedTransaction : fixedTransactions) {
                        if (fixedTransaction.getCategory().getId() == item.getId() && !item.isKey()) {
                            if (fixedTransaction.getAmount() == 0) {
                                this.amountLabel.setText(String.valueOf(fixedTransaction.getTransactionAmounts().get(
                                        fixedTransaction.getTransactionAmounts().size() - 1
                                ).getAmount()));
                            } else {
                                this.amountLabel.setText(String.valueOf(fixedTransaction.getAmount()));
                            }
                            if (fixedTransaction.getAmount() < 0) {
                                this.amountLabel.getStyleClass().add("neg-amount");
                            } else {
                                this.amountLabel.getStyleClass().add("pos-amount");
                            }
                            break;
                        }
                        if (item.isKey()) {
                            this.categoryLabel.getStyleClass().add("list-cell-title");
                        }
                    }
                }
                setGraphic(this.borderPane);
            }
        }

        private void initListCell() {
            this.borderPane = new BorderPane();
            this.borderPane.getStyleClass().add("categories-list-item");
            this.categoryLabel = new Label();

            this.amountLabel = new Label();
            this.amountLabel.getStyleClass().add("list-cell-title");

            this.borderPane.setLeft(this.categoryLabel);
            this.borderPane.setRight(this.amountLabel);
        }
    }

    private final class FixedTransactionListCellImpl extends ListCell<FixedTransaction> {
        private BorderPane borderPane;
        private Label activeLabel, dateLabel, amountLabel, isVariableLabel, dayLabel, lastAmountLabel, preLastAmountLabel;

        @Override
        protected void updateItem(FixedTransaction item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                this.initListCell(item);
                if (item.getEndDate() == null || (item.getEndDate() != null && item.getEndDate().compareTo(LocalDate.now()) >= 0)) {
                    this.activeLabel.setText(I18N.get("active"));
                    this.activeLabel.getStyleClass().add("pos-amount");
                } else {
                    this.activeLabel.setText(I18N.get("inactive"));
                }

                if (item.getEndDate() == null) {
                    this.dateLabel.setText(I18N.get("since") + " " + item.getStartDate());
                } else {
                    this.dateLabel.setText(item.getStartDate() + " - " + item.getEndDate());
                }

                if (item.isVariable()) {
                    this.amountLabel.setText(String.valueOf(item.getTransactionAmounts().get(0).getAmount()));
                    if (item.getTransactionAmounts().size() > 1) {
                        this.lastAmountLabel.setText(String.valueOf(item.getTransactionAmounts().get(1).getAmount()));
                        if (item.getTransactionAmounts().size() > 2) {
                            this.preLastAmountLabel.setText(String.valueOf(item.getTransactionAmounts().get(2).getAmount()));
                        }
                    }
                } else {
                    this.amountLabel.setText(String.valueOf(item.getAmount()));
                }
                if (item.getAmount() < 0) {
                    this.amountLabel.getStyleClass().add("neg-amount");
                } else {
                    this.amountLabel.getStyleClass().add("pos-amount");
                }

                this.isVariableLabel.setText(I18N.get("isVariable") + ": " +
                        (item.isVariable() ? I18N.get("yes") : I18N.get("no")));
                this.dayLabel.setText(I18N.get("valueDate") + ": " + Integer.toString(item.getDay()));


                setGraphic(this.borderPane);
            }
        }

        private void initListCell(FixedTransaction item) {
            this.borderPane = new BorderPane();
            this.borderPane.getStyleClass().add("transactions-list-item");

            VBox vBoxLeft = new VBox();
            this.activeLabel = new Label();
            this.activeLabel.getStyleClass().add("list-cell-title");
            vBoxLeft.getChildren().add(this.activeLabel);

            this.isVariableLabel = new Label();
            vBoxLeft.getChildren().add(this.isVariableLabel);

            this.dayLabel = new Label();
            vBoxLeft.getChildren().add(this.dayLabel);

            this.dateLabel = new Label();
            this.dateLabel.setTextAlignment(TextAlignment.CENTER);

            VBox vBoxRight = new VBox();
            this.amountLabel = new Label();
            this.amountLabel.setAlignment(Pos.CENTER_RIGHT);
            this.amountLabel.getStyleClass().add("list-cell-title");
            vBoxRight.getChildren().add(this.amountLabel);

            if (item.isVariable()) {
                this.lastAmountLabel = new Label();
                this.lastAmountLabel.setAlignment(Pos.CENTER_RIGHT);
                vBoxRight.getChildren().add(this.lastAmountLabel);

                this.preLastAmountLabel = new Label();
                this.preLastAmountLabel.setAlignment(Pos.CENTER_RIGHT);
                vBoxRight.getChildren().add(this.preLastAmountLabel);
            }
            vBoxRight.setAlignment(Pos.CENTER_RIGHT);

            this.borderPane.setLeft(vBoxLeft);
            this.borderPane.setCenter(this.dateLabel);
            this.borderPane.setRight(vBoxRight);
            BorderPane.setAlignment(this.borderPane.getCenter(), Pos.TOP_CENTER);
            BorderPane.setAlignment(this.borderPane.getRight(), Pos.CENTER_RIGHT);
        }
    }
}
