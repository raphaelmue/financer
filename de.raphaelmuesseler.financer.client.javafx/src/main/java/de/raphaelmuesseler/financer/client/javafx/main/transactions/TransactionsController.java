package de.raphaelmuesseler.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.AbstractTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.raphaelmuesseler.financer.util.date.Month.getMonthByNumber;

public class TransactionsController implements Initializable {

    public JFXButton refreshTransactionsBtn;
    public JFXButton newTransactionBtn;
    public JFXButton editTransactionBtn;
    public JFXButton deleteTransactionBtn;
    public TableView<Transaction> transactionsTableView;
    public JFXTextField filterTransactionsTextField;
    public JFXButton refreshFixedTransactionsBtn;
    public JFXButton newFixedTransactionBtn;
    public JFXButton editFixedTransactionBtn;
    public JFXButton deleteFixedTransactionBtn;
    public JFXListView<CategoryTree> categoriesListView;
    public JFXListView<FixedTransaction> fixedTransactionsListView;
    public TableView<TransactionOverviewRow> transactionsOverviewTableView;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private BaseCategory categories;
    private JavaFXFormatter formatter = new JavaFXFormatter(localStorage);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            this.user = (User) this.localStorage.readObject("user");

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

            this.categories = (BaseCategory) this.localStorage.readObject("categories");

            this.initializeTransactionsTable();
            this.initializeFixedTransactionTable();
            this.initializeTransactionsOverviewTable();

        });
    }

    private void initializeTransactionsOverviewTable() {
        final int numberOfMaxMonths = 6;
        final List<TableColumn<TransactionOverviewRow, String>> monthColumns = new ArrayList<>(numberOfMaxMonths);

        TableColumn<TransactionOverviewRow, CategoryTree> categoryColumn = new TableColumn<>(I18N.get("category"));
        categoryColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCategory()));
        categoryColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(CategoryTree item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label categoryLabel;
                    if (item.isRoot()) {
                        categoryLabel = new Label((item.getCategoryClass().getIndex() + 1) + ". " + I18N.get(item.getCategoryClass().getName()));
                        categoryLabel.setStyle("-fx-font-weight: 700");
                    } else {
                        categoryLabel = new Label(formatter.formatCategoryName(item.getValue()));
                    }
                    setGraphic(categoryLabel);
                }
            }
        });
        this.adjustColumnWidth(categoryColumn, this.transactionsOverviewTableView, 4);
        categoryColumn.setSortable(false);

        for (int i = 0; i < numberOfMaxMonths; i++) {
            TableColumn<TransactionOverviewRow, String> column = new TableColumn<>(I18N.get(Objects.requireNonNull(getMonthByNumber(LocalDate.now().minusMonths(i).getMonthValue())).getName()));
            this.adjustColumnWidth(column, this.transactionsOverviewTableView, 8);
            column.setStyle("-fx-alignment: CENTER-RIGHT;");
            column.setSortable(false);
            int index = i;
            column.setCellValueFactory(param -> new SimpleStringProperty(Double.toString(param.getValue().amounts[index])));
            column.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(!empty ? formatter.formatAmountLabel(Double.valueOf(item)) : null);
                }
            });
            monthColumns.add(column);
        }
        this.transactionsOverviewTableView.getColumns().add(categoryColumn);
        this.transactionsOverviewTableView.getColumns().addAll(monthColumns);
    }

    private void initializeTransactionsTable() {

        TableColumn<Transaction, Category> categoryColumn = new TableColumn<>(I18N.get("category"));
        TableColumn<Transaction, LocalDate> valueDateColumn = new TableColumn<>(I18N.get("valueDate"));
        TableColumn<Transaction, Double> amountColumn = new TableColumn<>(I18N.get("amount"));
        TableColumn<Transaction, String> productColumn = new TableColumn<>(I18N.get("product"));
        TableColumn<Transaction, String> purposeColumn = new TableColumn<>(I18N.get("purpose"));
        TableColumn<Transaction, String> shopColumn = new TableColumn<>(I18N.get("shop"));

        valueDateColumn.setCellValueFactory(new PropertyValueFactory<>("valueDate"));
        valueDateColumn.setSortable(true);
        valueDateColumn.setComparator(LocalDate::compareTo);
        valueDateColumn.setStyle("-fx-alignment: CENTER;");
        valueDateColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ? formatter.formatDate(item) : null);
            }
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        amountColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(!empty ? formatter.formatAmountLabel(item) : null);
            }
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryTree"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        shopColumn.setCellValueFactory(new PropertyValueFactory<>("shop"));


        this.adjustColumnWidth(valueDateColumn, this.transactionsTableView, 6);
        this.adjustColumnWidth(amountColumn, this.transactionsTableView, 6);
        this.adjustColumnWidth(categoryColumn, this.transactionsTableView, 6);
        this.adjustColumnWidth(productColumn, this.transactionsTableView, 6);
        this.adjustColumnWidth(purposeColumn, this.transactionsTableView, 6);
        this.adjustColumnWidth(shopColumn, this.transactionsTableView, 6);

        this.transactionsTableView.getColumns().add(categoryColumn);
        this.transactionsTableView.getColumns().add(valueDateColumn);
        this.transactionsTableView.getColumns().add(amountColumn);
        this.transactionsTableView.getColumns().add(productColumn);
        this.transactionsTableView.getColumns().add(purposeColumn);
        this.transactionsTableView.getColumns().add(shopColumn);

        this.transactionsTableView.setRowFactory(param -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleEditTransaction();
                }
            });
            return row;
        });

        this.transactionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editTransactionBtn.setDisable(false);
            deleteTransactionBtn.setDisable(false);
        });

        transactionsTableView.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
        transactionsTableView.getSortOrder().add(valueDateColumn);

        this.handleRefreshTransactions();
    }

    private void initializeFixedTransactionTable() {
        this.categoriesListView.setCellFactory(param -> new CategoryListViewImpl());

        this.categoriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showFixedTransactions(newValue);
            if (newValue != null && !newValue.isRoot()) {
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
        this.fixedTransactionsListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                handleEditFixedTransaction();
            }
        });

        categoriesListView.setCellFactory(param -> new CategoryListViewImpl());
    }

    private void loadTransactionOverviewTableData() {
        this.transactionsOverviewTableView.getItems().clear();
        final Map<CategoryTree, TransactionOverviewRow> rows = new HashMap<>();

        if (this.categories != null) {
            this.categories.traverse(categoryTree -> {
                TransactionOverviewRow transactionOverviewRow = new TransactionOverviewRow((CategoryTree) categoryTree);
                for (int i = 0; i < 6; i++) {
                    transactionOverviewRow.getAmounts()[i] = ((CategoryTree) categoryTree).getAmount(LocalDate.now().minusMonths(i));
                }
                rows.put((CategoryTree) categoryTree, transactionOverviewRow);
            });
        }

        List<TransactionOverviewRow> items = new ArrayList<>(rows.values());
        items.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(formatter.formatCategoryName(o1.getCategory().getValue()),
                formatter.formatCategoryName(o2.getCategory().getValue())));
        this.transactionsOverviewTableView.getItems().addAll(items);

        this.transactionsOverviewTableView.refresh();
    }

    private void loadTransactionTableData() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        if (this.categories != null) {
//            transactionsTableView.getItems().clear();
            this.categories.traverse(treeItem -> {
                if ((((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_EXPENSES ||
                        ((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_REVENUE)) {
                    for (AbstractTransaction abstractTransaction : ((CategoryTree) treeItem).getTransactions()) {
                        if (abstractTransaction instanceof Transaction) {
                            transactions.add((Transaction) abstractTransaction);
                        }
                    }
                }
            });
        }

        FilteredList<Transaction> filteredData = new FilteredList<>(transactions, transaction -> true);
        filterTransactionsTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(transaction -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    return transaction.getShop().toLowerCase().contains(newValue.toLowerCase()) ||
                            transaction.getCategoryTree().getValue().getName().toLowerCase().contains(newValue.toLowerCase()) ||
                            transaction.getProduct().toLowerCase().contains(newValue.toLowerCase()) ||
                            transaction.getPurpose().toLowerCase().contains(newValue.toLowerCase());
                }));

        SortedList<Transaction> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transactionsTableView.comparatorProperty());

        transactionsTableView.setItems(sortedData);
        transactionsTableView.refresh();
    }

    private void loadFixedTransactionTableData() {
        if (this.categories != null) {
            categoriesListView.getItems().clear();
            this.categories.traverse(treeItem -> {
                if ((((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.FIXED_EXPENSES ||
                        ((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.FIXED_REVENUE)) {
                    categoriesListView.getItems().add((CategoryTree) treeItem);
                }
            });
        }

        this.categoriesListView.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(formatter.formatCategoryName(o1),
                formatter.formatCategoryName(o2)));

    }

    public void handleRefreshTransactions() {
        RetrievalServiceImpl.getInstance().fetchTransactions(this.user, new AsyncCall<>() {
            @Override
            public void onSuccess(List<Transaction> result) {
            }

            @Override
            public void onAfter() {
                categories = (BaseCategory) localStorage.readObject("categories");
                Platform.runLater(() -> {
                    loadTransactionTableData();
                    transactionsTableView.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
                    transactionsTableView.getSortOrder().add(transactionsTableView.getColumns().get(1));
                    transactionsTableView.refresh();

                    loadTransactionOverviewTableData();
                });
            }
        });
    }

    public void handleRefreshFixedTransactions() {
        RetrievalServiceImpl.getInstance().fetchFixedTransactions(this.user, new AsyncCall<>() {
            @Override
            public void onSuccess(List<FixedTransaction> result) {
            }

            @Override
            public void onFailure(Exception exception) {
                logger.log(Level.SEVERE, exception.getMessage(), exception);
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> {
                    categories = (BaseCategory) localStorage.readObject("categories");

                    loadFixedTransactionTableData();
                    fixedTransactionsListView.getItems().clear();

                    loadTransactionOverviewTableData();
                });
            }
        });
    }

    public void handleNewTransaction() {
        Transaction transaction = new TransactionDialog(null, this.categories).showAndGetResult();
        if (transaction != null) {

            this.correctTransactionAmount(transaction);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("transaction", transaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "addTransaction", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    transaction.setId(((Transaction) result.getResult()).getId());
                    localStorage.writeObject("categories", categories);
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> {
                        loadTransactionTableData();
                        loadTransactionOverviewTableData();
                    });
                }
            }, true));
        }
    }

    public void handleNewFixedTransaction() {
        FixedTransaction fixedTransaction = new FixedTransactionDialog(null,
                (this.categoriesListView.getSelectionModel().getSelectedItem()))
                .showAndGetResult();
        if (fixedTransaction != null) {

            if ((fixedTransaction.getCategoryTree().getCategoryClass().isRevenue() && fixedTransaction.getAmount() < 0) ||
                    (!fixedTransaction.getCategoryTree().getCategoryClass().isRevenue() && fixedTransaction.getAmount() >= 0)) {
                fixedTransaction.setAmount(fixedTransaction.getAmount() * (-1));
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("fixedTransaction", fixedTransaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "addFixedTransactions", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    handleRefreshFixedTransactions();
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        }
    }

    public void handleEditTransaction() {
        Transaction transaction = new TransactionDialog(this.transactionsTableView.getSelectionModel().getSelectedItem(),
                this.categories).showAndGetResult();
        if (transaction != null) {

            this.correctTransactionAmount(transaction);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("transaction", transaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateTransaction",
                    parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    localStorage.writeObject("categories", categories);
                    Platform.runLater(() -> transactionsTableView.refresh());
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        }
    }

    public void handleEditFixedTransaction() {
        FixedTransaction fixedTransaction = new FixedTransactionDialog(
                this.fixedTransactionsListView.getSelectionModel().getSelectedItem(),
                this.categoriesListView.getSelectionModel().getSelectedItem())
                .showAndGetResult();
        if (fixedTransaction != null) {

            if ((fixedTransaction.getCategoryTree().getValue().getRootId() == 0 && fixedTransaction.getAmount() < 0) ||
                    (fixedTransaction.getCategoryTree().getValue().getRootId() == 2 && fixedTransaction.getAmount() >= 0)) {
                fixedTransaction.setAmount(fixedTransaction.getAmount() * (-1));
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fixedTransaction", fixedTransaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateFixedTransaction", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    localStorage.writeObject("categories", categories);

                    Platform.runLater(() -> {
                        fixedTransactionsListView.refresh();
                        categoriesListView.refresh();
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> {
                        loadTransactionOverviewTableData();
                    });
                }
            }, true));
        }
    }

    public void handleDeleteTransaction() {
        if (new FinancerConfirmDialog(I18N.get("confirmDeleteTransaction")).showAndGetResult()) {
            Transaction transaction = this.transactionsTableView.getSelectionModel().getSelectedItem();
            if (transaction != null) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("transaction", transaction);

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteTransaction", parameters, new JavaFXAsyncConnectionCall() {
                    @Override
                    public void onSuccess(ConnectionResult result) {
                        Platform.runLater(() -> {
                            transaction.getCategoryTree().getTransactions().remove(transaction);
                            loadTransactionTableData();
                            localStorage.writeObject("categories", categories);
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                        JavaFXAsyncConnectionCall.super.onFailure(exception);
                    }

                    @Override
                    public void onAfter() {
                        Platform.runLater(() -> loadTransactionOverviewTableData());
                    }
                }, true));
            }
        }
    }

    public void handleDeleteFixedTransaction() {
        boolean result = new FinancerConfirmDialog(I18N.get("confirmDeleteFixedTransaction")).showAndGetResult();
        if (result) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fixedTransaction", this.fixedTransactionsListView.getSelectionModel().getSelectedItem());

            this.fixedTransactionsListView.getItems().remove(this.fixedTransactionsListView.getSelectionModel().getSelectedItem());

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteFixedTransaction",
                    parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    fixedTransactionsListView.getSelectionModel().getSelectedItem().getCategoryTree().getTransactions().remove(
                            fixedTransactionsListView.getSelectionModel().getSelectedItem());
                    localStorage.writeObject("categories", categories);

                    fixedTransactionsListView.refresh();
                    categoriesListView.refresh();
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        }
    }

    private void showFixedTransactions(CategoryTree category) {
        if (category != null) {
            this.fixedTransactionsListView.getItems().clear();
            for (AbstractTransaction abstractTransaction : category.getTransactions()) {
                if (abstractTransaction instanceof FixedTransaction) {
                    this.fixedTransactionsListView.getItems().add((FixedTransaction) abstractTransaction);
                }
            }
        }

        this.fixedTransactionsListView.setCellFactory(param -> new FixedTransactionListCellImpl());
        fixedTransactionsListView.getItems().sort(Comparator.comparing(FixedTransaction::getStartDate).reversed());
    }


    private void correctTransactionAmount(AbstractTransaction transaction) {
        if ((transaction.getCategoryTree().getValue().getRootId() == 1 && transaction.getAmount() < 0) ||
                (transaction.getCategoryTree().getValue().getRootId() == 3 && transaction.getAmount() >= 0)) {
            transaction.setAmount(transaction.getAmount() * (-1));
        }
    }

    private <S, T> void adjustColumnWidth(TableColumn<S, T> column, TableView<S> tableView, double ratio) {
        column.prefWidthProperty().bind(tableView.widthProperty().divide(ratio).add(-3));
    }

    private final class CategoryListViewImpl extends ListCell<CategoryTree> {
        private BorderPane borderPane;
        private Label categoryLabel, amountLabel;

        @Override
        protected void updateItem(CategoryTree item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                this.initListCell();
                this.categoryLabel.setText(formatter.formatCategoryName(item));
                formatter.formatAmountLabel(this.amountLabel, item.getAmount(LocalDate.now()));
                if (item.isRoot()) {
                    this.categoryLabel.getStyleClass().add("list-cell-title");
                }
            }

            setGraphic(this.borderPane);

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
                if (item.getEndDate() == null || (item.getEndDate() != null && item.getEndDate().compareTo(LocalDate.now()) > 0)) {
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
                    formatter.formatAmountLabel(this.amountLabel, item.getTransactionAmounts().get(0).getAmount());
                    if (item.getTransactionAmounts().size() > 1) {
                        formatter.formatAmountLabel(this.lastAmountLabel, item.getTransactionAmounts().get(1).getAmount());
                        if (item.getTransactionAmounts().size() > 2) {
                            formatter.formatAmountLabel(this.preLastAmountLabel, item.getTransactionAmounts().get(2).getAmount());
                        }
                    }
                } else {
                    formatter.formatAmountLabel(this.amountLabel, item.getAmount());
                }

                this.isVariableLabel.setText(I18N.get("isVariable") + ": " +
                        (item.isVariable() ? I18N.get("yes") : I18N.get("no")));
                this.dayLabel.setText(I18N.get("valueDate") + ": " + item.getDay());


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
        private CategoryTree categoryTree;
        private double[] amounts = new double[6];

        TransactionOverviewRow(CategoryTree categoryTree) {
            this.categoryTree = categoryTree;
        }

        CategoryTree getCategory() {
            return categoryTree;
        }

        double[] getAmounts() {
            return amounts;
        }
    }
}
