package de.raphaelmuesseler.financer.client.ui.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.format.Formatter;
import de.raphaelmuesseler.financer.client.ui.format.I18N;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.ui.main.FinancerController;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.util.collections.CollectionUtil;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import de.raphaelmuesseler.financer.shared.util.date.DateUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.ConnectException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.raphaelmuesseler.financer.shared.util.date.Month.*;

public class TransactionsController implements Initializable {

    @FXML
    public JFXButton refreshTransactionsBtn;
    @FXML
    public JFXButton newTransactionBtn;
    @FXML
    public JFXButton editTransactionBtn;
    @FXML
    public JFXButton deleteTransactionBtn;
    @FXML
    public TableView<Transaction> transactionsTableView;
    @FXML
    public JFXButton refreshFixedTransactionsBtn;
    @FXML
    public JFXButton newFixedTransactionBtn;
    @FXML
    public JFXButton editFixedTransactionBtn;
    @FXML
    public JFXButton deleteFixedTransactionBtn;
    @FXML
    public JFXListView categoriesListView;
    @FXML
    public JFXListView fixedTransactionsListView;
    public TableView<TransactionOverviewRow> transactionsOverviewTableView;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ObservableList<Transaction> transactions;
    private ObservableList<FixedTransaction> fixedTransactions;
    private SerialTreeItem<Category> tree;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.showLoadingBox();

        this.user = LocalStorage.getLoggedInUser();

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.refreshTransactionsBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.REFRESH));
        this.refreshTransactionsBtn.setGraphicTextGap(8);
        this.newTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.PLUS));
        this.newTransactionBtn.setGraphicTextGap(8);
        this.editTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
        this.editTransactionBtn.setGraphicTextGap(8);
        this.editTransactionBtn.setDisable(true);
        this.deleteTransactionBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
        this.deleteTransactionBtn.setGraphicTextGap(8);
        this.deleteTransactionBtn.setDisable(true);

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

        this.tree = SerialTreeItem.fromJson((String) LocalStorage.readObject(LocalStorage.PROFILE_FILE, "categories"),
                Category.class);

        this.tree.numberItemsByValue((result, prefix) -> {
            result.getValue().setPrefix(prefix);
        });

        Platform.runLater(() -> {
            this.loadTransactionsTable();
            this.loadFixedTransactionsTable();
        });
    }

    private void loadTransactionsOverviewTable() {

        final int numberOfMaxMonths = 6;
        final List<TableColumn<TransactionOverviewRow, String>> monthColumns = new ArrayList<>(numberOfMaxMonths);
        final Map<Category, TransactionOverviewRow> rows = new HashMap<>();

        this.tree.traverse(treeItem -> rows.put(treeItem.getValue(), new TransactionOverviewRow(treeItem.getValue())), true);

        TableColumn<TransactionOverviewRow, String> categoryColumn = new TableColumn<>(I18N.get("category"));
        categoryColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().category.getName()));
        categoryColumn.prefWidthProperty().bind(this.transactionsOverviewTableView.widthProperty().divide(8/2).add(-3));
        categoryColumn.setSortable(false);

        for (int i = 0; i < numberOfMaxMonths; i++) {
            TableColumn<TransactionOverviewRow, String> column = new TableColumn<>(getMonthByNumber(LocalDate.now().minusMonths(i).getMonthValue()).getName());
            column.prefWidthProperty().bind(this.transactionsOverviewTableView.widthProperty().divide(8).add(-3));
            column.setStyle("-fx-alignment: CENTER-RIGHT;");
            column.setSortable(false);
            int index = i;
            column.setCellValueFactory(param -> new SimpleStringProperty(Double.toString(param.getValue().amounts[index])));
            column.setCellFactory(param -> new TableCell<TransactionOverviewRow, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(!empty ? Formatter.formatAmountLabel(Double.valueOf(item)) : null);
                }
            });
            monthColumns.add(column);
        }
        this.transactionsOverviewTableView.getColumns().add(categoryColumn);
        this.transactionsOverviewTableView.getColumns().addAll(monthColumns);

        for (Transaction transaction : this.transactions) {
            if (transaction.getValueDate().plusMonths(numberOfMaxMonths).compareTo(LocalDate.now()) >= 0) {
                TransactionOverviewRow row = rows.get(transaction.getCategory());
                if (row == null) {
                    row = new TransactionOverviewRow(transaction.getCategory());
                    rows.put(row.getCategory(), row);
                }
                row.getAmounts()[DateUtil.getMonthDifference(transaction.getValueDate(), LocalDate.now())] += transaction.getAmount();
            }
        }

        for (FixedTransaction fixedTransaction : this.fixedTransactions) {
            if (fixedTransaction.getEndDate() == null || fixedTransaction.getEndDate().plusMonths(numberOfMaxMonths).compareTo(LocalDate.now()) >= 0) {
                TransactionOverviewRow row = rows.get(fixedTransaction.getCategory());
                if (row == null) {
                    row = new TransactionOverviewRow(fixedTransaction.getCategory());
                    rows.put(row.getCategory(), row);
                }
                if (fixedTransaction.isVariable() && fixedTransaction.getTransactionAmounts().size() > 1) {
                    for (int i = 0; i < Math.min(numberOfMaxMonths, fixedTransaction.getTransactionAmounts().size()); i++) {
                        if (DateUtil.getMonthDifference(fixedTransaction.getTransactionAmounts().get(i).getValueDate(), LocalDate.now()) < 6) {
                            row.getAmounts()[DateUtil.getMonthDifference(fixedTransaction.getTransactionAmounts().get(i).getValueDate(),
                                    LocalDate.now())] += fixedTransaction.getTransactionAmounts().get(i).getAmount();
                        }
                    }
                } else {
                    int start = 0;
                    if (fixedTransaction.getEndDate() != null) {
                        start = Period.between(fixedTransaction.getEndDate().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1)).getMonths();
                        if (start == DateUtil.getMonthDifference(fixedTransaction.getStartDate(), LocalDate.now())) {
                            row.getAmounts()[start] += fixedTransaction.getAmount();
                        }
                    } else {
                        for (int i = start; i < Math.min(numberOfMaxMonths, DateUtil.getMonthDifference(fixedTransaction.getStartDate(),
                                LocalDate.now())); i++) {
                            row.getAmounts()[i] += fixedTransaction.getAmount();
                        }
                    }
                }
            }
        }

        List<TransactionOverviewRow> items = new ArrayList<>(rows.values());
        Collections.sort(items, (o1, o2) ->
                String.CASE_INSENSITIVE_ORDER.compare(o1.getCategory().getName(), o2.getCategory().getName()));
        this.transactionsOverviewTableView.getItems().addAll(items);
    }

    private void loadTransactionsTable() {
        TableColumn<Transaction, Category> categoryColumn = new TableColumn<>(I18N.get("category"));
        TableColumn<Transaction, Date> valueDateColumn = new TableColumn<>(I18N.get("valueDate"));
        TableColumn<Transaction, Double> amountColumn = new TableColumn<>(I18N.get("amount"));
        TableColumn<Transaction, String> productColumn = new TableColumn<>(I18N.get("product"));
        TableColumn<Transaction, String> purposeColumn = new TableColumn<>(I18N.get("purpose"));
        TableColumn<Transaction, String> shopColumn = new TableColumn<>(I18N.get("shop"));

        valueDateColumn.setCellValueFactory(new PropertyValueFactory<>("valueDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        amountColumn.setCellFactory(param -> new TableCell<Transaction, Double>(){
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(!empty ? Formatter.formatAmountLabel(item) : null);
            }
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        shopColumn.setCellValueFactory(new PropertyValueFactory<>("shop"));


        valueDateColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6).add(-3));
        amountColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6).add(-3));
        categoryColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6).add(-3));
        productColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6).add(-3));
        purposeColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6).add(-3));
        shopColumn.prefWidthProperty().bind(this.transactionsTableView.widthProperty().divide(6).add(-3));

        this.transactionsTableView.getColumns().addAll(categoryColumn, valueDateColumn, amountColumn, productColumn, purposeColumn, shopColumn);

        this.transactionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editTransactionBtn.setDisable(false);
            deleteTransactionBtn.setDisable(false);
        });

        this.handleRefreshTransactions();
    }

    private void loadFixedTransactionsTable() {
        if (LocalStorage.readObject(LocalStorage.PROFILE_FILE, "categories") != null) {
            this.tree.traverse(treeItem -> {
                if ((treeItem.getValue().getRootId() != -1 && (treeItem.getValue().getRootId() % 2) == 0) ||
                        (treeItem.getValue().getRootId() == -1 && (treeItem.getValue().getParentId() % 2) == 0)) {
                    categoriesListView.getItems().add(treeItem.getValue());
                }
            });
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

    public void handleRefreshTransactions() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", this.user);

        this.executor.execute(new ServerRequestHandler("getTransactions", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                transactions = FXCollections.observableArrayList((List<Transaction>) result.getResult());
                LocalStorage.writeObject(LocalStorage.TRANSACTIONS_FILE, "transactions", result.getResult());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    // TODO set offline
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
                List<Object> result = ((List<Object>) LocalStorage.readObject(
                        LocalStorage.TRANSACTIONS_FILE, "transactions"));
                if (result != null && result.size() > 0) {
                    transactions = CollectionUtil.castObjectListToObservable(result);
                }
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> {
                    transactionsTableView.setItems(transactions);
                    transactionsTableView.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
                    transactionsTableView.getSortOrder().add(transactionsTableView.getColumns().get(1));
                    FinancerController.hideLoadingBox();
                });
            }
        }));
    }

    public void handleNewTransaction() {
        Transaction transaction = new TransactionDialog(null).showAndGetResult();
        if (transaction != null) {

            if ((transaction.getCategory().getRootId() == 1 && transaction.getAmount() < 0) ||
                    (transaction.getCategory().getRootId() == 3 && transaction.getAmount() >= 0)) {
                transaction.setAmount(transaction.getAmount() * (-1));
            }

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

            if ((transaction.getCategory().getRootId() == 1 && transaction.getAmount() < 0) ||
                    (transaction.getCategory().getRootId() == 3 && transaction.getAmount() >= 0)) {
                transaction.setAmount(transaction.getAmount() * (-1));
            }

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
                LocalStorage.writeObject(LocalStorage.TRANSACTIONS_FILE, "fixedTransactions", result.getResult());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    // TODO set offline
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    AsyncConnectionCall.super.onFailure(exception);
                }
                List<Object> result = (List<Object>) LocalStorage.readObject(
                        LocalStorage.TRANSACTIONS_FILE, "fixedTransactions");
                if (result != null && result.size() > 0) {
                    fixedTransactions = CollectionUtil.castObjectListToObservable(result);
                }
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> {
                    showFixedTransactions((Category) categoriesListView.getSelectionModel().getSelectedItem());
                    categoriesListView.setCellFactory(param -> new CategoryListViewImpl());
                    FinancerController.hideLoadingBox();

                    loadTransactionsOverviewTable();
                });
            }
        }));
    }

    public void handleNewFixedTransaction() {
        FixedTransaction fixedTransaction = new FixedTransactionDialog(null,
                ((Category) this.categoriesListView.getSelectionModel().getSelectedItem()))
                .showAndGetResult();
        if (fixedTransaction != null) {

            if ((fixedTransaction.getCategory().getRootId() == 0 && fixedTransaction.getAmount() < 0) ||
                    (fixedTransaction.getCategory().getRootId() == 2 && fixedTransaction.getAmount() >= 0)) {
                fixedTransaction.setAmount(fixedTransaction.getAmount() * (-1));
            }

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

            if ((fixedTransaction.getCategory().getRootId() == 0 && fixedTransaction.getAmount() < 0) ||
                    (fixedTransaction.getCategory().getRootId() == 2 && fixedTransaction.getAmount() >= 0)) {
                fixedTransaction.setAmount(fixedTransaction.getAmount() * (-1));
            }

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
                            if (fixedTransaction.isVariable() && fixedTransaction.getTransactionAmounts() != null &&
                                    fixedTransaction.getTransactionAmounts().size() > 0) {
                                Formatter.formatAmountLabel(this.amountLabel, fixedTransaction.getTransactionAmounts().get(
                                        fixedTransaction.getTransactionAmounts().size() - 1
                                ).getAmount());
                            } else {
                                Formatter.formatAmountLabel(this.amountLabel, fixedTransaction.getAmount());
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

                if (item.isVariable() && item.getTransactionAmounts() != null &&
                        item.getTransactionAmounts().size() > 0) {
                    Formatter.formatAmountLabel(this.amountLabel, item.getTransactionAmounts().get(0).getAmount());
                    if (item.getTransactionAmounts().size() > 1) {
                        Formatter.formatAmountLabel(this.lastAmountLabel, item.getTransactionAmounts().get(1).getAmount());
                        if (item.getTransactionAmounts().size() > 2) {
                            Formatter.formatAmountLabel(this.preLastAmountLabel, item.getTransactionAmounts().get(2).getAmount());
                        }
                    }
                } else {
                    Formatter.formatAmountLabel(this.amountLabel, item.getAmount());
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

    private class TransactionOverviewRow {
        private Category category;
        private double[] amounts = new double[6];

        TransactionOverviewRow(Category category) {
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }

        public double[] getAmounts() {
            return amounts;
        }
    }
}
