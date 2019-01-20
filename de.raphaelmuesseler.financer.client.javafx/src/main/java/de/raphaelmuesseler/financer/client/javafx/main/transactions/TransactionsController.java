package de.raphaelmuesseler.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.AbstractTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.CollectionUtil;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private ExecutorService executor = Executors.newCachedThreadPool();
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private ObservableList<Transaction> transactions;
    private ObservableList<FixedTransaction> fixedTransactions;
    private BaseCategory categories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.showLoadingBox();

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

        if (this.categories != null) {
            // TODO set prefixes
            TreeUtil.numberItemsByValue(this.categories, (result, prefix) -> result.getValue().setPrefix(prefix));
        }

        this.loadTransactionsTable();
        this.loadFixedTransactionsTable();
    }

    private void loadTransactionsOverviewTable() {
        final int numberOfMaxMonths = 6;
        final List<TableColumn<TransactionOverviewRow, String>> monthColumns = new ArrayList<>(numberOfMaxMonths);
        final Map<Category, TransactionOverviewRow> rows = new HashMap<>();

        if (this.categories != null) {
            this.categories.traverse(categoryTree -> {
                TransactionOverviewRow transactionOverviewRow = new TransactionOverviewRow((CategoryTree) categoryTree);
                for (int i = 0; i < 6; i++) {
                    transactionOverviewRow.getAmounts()[i] = ((CategoryTree) categoryTree).getAmount(LocalDate.now().minusMonths(i));
                }
                rows.put(categoryTree.getValue(), transactionOverviewRow);
            });
        }

        TableColumn<TransactionOverviewRow, String> categoryColumn = new TableColumn<>(I18N.get("category"));
        categoryColumn.setCellValueFactory(param -> new SimpleStringProperty(JavaFXFormatter.formatCategoryName(param.getValue().getCategory())));
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
                    setGraphic(!empty ? Formatter.formatAmountLabel(Double.valueOf(item)) : null);
                }
            });
            monthColumns.add(column);
        }
        this.transactionsOverviewTableView.getColumns().add(categoryColumn);
        this.transactionsOverviewTableView.getColumns().addAll(monthColumns);

        List<TransactionOverviewRow> items = new ArrayList<>(rows.values());
        items.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(Formatter.formatCategoryName(o1.getCategory().getValue()),
                Formatter.formatCategoryName(o2.getCategory().getValue())));
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
        amountColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(!empty ? Formatter.formatAmountLabel(item) : null);
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

        this.transactionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editTransactionBtn.setDisable(false);
            deleteTransactionBtn.setDisable(false);
        });

        this.handleRefreshTransactions();
    }

    private void loadFixedTransactionsTable() {
        if (this.localStorage.readObject("categories") != null) {
            this.categories.traverse(treeItem -> {
                if ((((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.FIXED_EXPENSES ||
                        ((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.FIXED_REVENUE)) {
                    categoriesListView.getItems().add((CategoryTree) treeItem);
                }
            });
        }

        this.categoriesListView.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(JavaFXFormatter.formatCategoryName(o1),
                JavaFXFormatter.formatCategoryName(o2)));

        this.categoriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showFixedTransactions(newValue);
            if (!newValue.isRoot()) {
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
        RetrievalServiceImpl.getInstance().fetchTransactions(this.user, new AsyncCall<>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                transactions = CollectionUtil.castListToObservableList(result);
                Platform.runLater(() -> {
                    loadTransactionTableItems();
                    FinancerController.hideLoadingBox();
                });
            }

            @Override
            public void onFailure(Exception exception) {
                List<Transaction> result = localStorage.readList("transactions");
                if (result != null && result.size() > 0) {
                    transactions = CollectionUtil.castListToObservableList(result);
                }
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
                    Platform.runLater(() -> {
                        // removing numbers in category's name
                        transactions.add(transaction);
                        loadTransactionTableItems();
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }

    private void loadTransactionTableItems() {
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
        transactionsTableView.setItems(filteredData);
        transactionsTableView.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
        transactionsTableView.getSortOrder().add(transactionsTableView.getColumns().get(1));
    }

    public void handleEditTransaction() {
        Transaction transaction = new TransactionDialog(this.transactionsTableView.getSelectionModel().getSelectedItem(),
                this.categories).showAndGetResult();
        if (transaction != null) {

            this.correctTransactionAmount(transaction);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("transaction", transaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateTransaction", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    handleRefreshTransactions();
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }));
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
                        Platform.runLater(() -> transactionsTableView.getItems().remove(transaction));
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                        JavaFXAsyncConnectionCall.super.onFailure(exception);
                    }
                }));
            }
        }
    }

    public void handleRefreshFixedTransactions() {
        RetrievalServiceImpl.getInstance().fetchFixedTransactions(this.user, new AsyncCall<>() {
            @Override
            public void onSuccess(List<FixedTransaction> result) {
                fixedTransactions = CollectionUtil.castListToObservableList(result);

                Platform.runLater(() -> {
                    showFixedTransactions(categoriesListView.getSelectionModel().getSelectedItem());
                    categoriesListView.setCellFactory(param -> new TransactionsController.CategoryListViewImpl());
                    FinancerController.hideLoadingBox();

                    loadTransactionsOverviewTable();
                });
            }

            @Override
            public void onFailure(Exception exception) {
                List<FixedTransaction> result = localStorage.readList("fixedTransactions");
                if (result != null && result.size() > 0) {
                    fixedTransactions = CollectionUtil.castListToObservableList(result);
                }
            }
        });
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
            }));
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

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateFixedTransaction", parameters,
                    this.getFixedTransactionCallback()));
        }
    }

    public void handleDeleteFixedTransaction() {
        boolean result = new FinancerConfirmDialog(I18N.get("confirmDeleteFixedTransaction")).showAndGetResult();
        if (result) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("fixedTransaction", this.fixedTransactionsListView.getSelectionModel().getSelectedItem());

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteFixedTransaction", parameters,
                    this.getFixedTransactionCallback()));
        }
    }

    private JavaFXAsyncConnectionCall getFixedTransactionCallback() {
        return new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
            }

            @Override
            public void onFailure(Exception exception) {
                logger.log(Level.SEVERE, exception.getMessage(), exception);
                JavaFXAsyncConnectionCall.super.onFailure(exception);
            }

            @Override
            public void onAfter() {
                handleRefreshFixedTransactions();
            }
        };
    }

    private void showFixedTransactions(CategoryTree category) {
        if (category != null) {
            this.fixedTransactionsListView.getItems().clear();
            if (this.fixedTransactions != null) {
                for (FixedTransaction transaction : this.fixedTransactions) {
                    if (transaction.getCategoryTree().getValue().getId() == category.getValue().getId()) {
                        this.fixedTransactionsListView.getItems().add(transaction);
                    }
                }
            }
        }
        this.fixedTransactionsListView.setCellFactory(param -> new FixedTransactionListCellImpl());
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
                this.categoryLabel.setText(JavaFXFormatter.formatCategoryName(item));
                Formatter.formatAmountLabel(this.amountLabel, item.getAmount(LocalDate.now()));
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
