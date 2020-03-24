package org.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.financer.client.connection.AsyncConnectionCall;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.format.I18N;
import org.financer.client.javafx.connection.RetrievalServiceImpl;
import org.financer.client.javafx.dialogs.FinancerConfirmDialog;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.local.Application;
import org.financer.client.local.LocalSettingsImpl;
import org.financer.shared.connection.AsyncCall;
import org.financer.shared.connection.ConnectionResult;
import org.financer.util.concurrency.FinancerExecutor;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static org.financer.util.date.Month.getMonthByNumber;

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
    public TableView<VariableTransaction> transactionsTableView;
    @FXML
    public JFXTextField filterTransactionsTextField;
    @FXML
    public JFXButton refreshFixedTransactionsBtn;
    @FXML
    public JFXButton newFixedTransactionBtn;
    @FXML
    public JFXButton editFixedTransactionBtn;
    @FXML
    public JFXButton deleteFixedTransactionBtn;
    @FXML
    public JFXListView<CategoryTree> categoriesListView;
    @FXML
    public JFXListView<FixedTransaction> fixedTransactionsListView;
    @FXML
    public TableView<TransactionOverviewRow> transactionsOverviewTableView;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private BaseCategory categories;
    private JavaFXFormatter formatter = new JavaFXFormatter(localStorage);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            this.user = (User) this.localStorage.readObject("user");

            this.editTransactionBtn.setDisable(true);
            this.deleteTransactionBtn.setDisable(true);

            this.newFixedTransactionBtn.setDisable(true);
            this.editFixedTransactionBtn.setDisable(true);
            this.deleteFixedTransactionBtn.setDisable(true);

            this.categories = (BaseCategory) this.localStorage.readObject("categories");

            this.initializeTransactionsTable();
            this.loadTransactionTableData();
            this.initializeFixedTransactionTable();
            this.loadFixedTransactionTableData();
            this.initializeTransactionsOverviewTable();
            this.loadTransactionOverviewTableData();
            FinancerController.getInstance().hideLoadingBox();

        }));
        FinancerController.getInitializationThread().start();
    }

    private void initializeTransactionsOverviewTable() {
        final int numberOfMaxMonths = ((LocalSettingsImpl) localStorage.readObject("localSettings")).getMaxNumberOfMonthsDisplayed();
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
                        categoryLabel = new Label((item.getValue().getCategoryClass().getIndex() + 1) + ". " + I18N.get(item.getValue().getCategoryClass().getName()));
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
            this.adjustColumnWidth(column, this.transactionsOverviewTableView, numberOfMaxMonths + 2.0);
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
        Platform.runLater(() -> {
            this.transactionsOverviewTableView.getColumns().add(categoryColumn);
            this.transactionsOverviewTableView.getColumns().addAll(monthColumns);
        });
    }

    private void initializeTransactionsTable() {
        TableColumn<VariableTransaction, Category> categoryColumn = new TableColumn<>(I18N.get("category"));
        TableColumn<VariableTransaction, LocalDate> valueDateColumn = new TableColumn<>(I18N.get("valueDate"));
        TableColumn<VariableTransaction, Double> amountColumn = new TableColumn<>(I18N.get("amount"));
        TableColumn<VariableTransaction, String> productColumn = new TableColumn<>(I18N.get("product"));
        TableColumn<VariableTransaction, String> purposeColumn = new TableColumn<>(I18N.get("purpose"));
        TableColumn<VariableTransaction, String> shopColumn = new TableColumn<>(I18N.get("shop"));

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

        Platform.runLater(() -> {
            this.transactionsTableView.getColumns().add(categoryColumn);
            this.transactionsTableView.getColumns().add(valueDateColumn);
            this.transactionsTableView.getColumns().add(amountColumn);
            this.transactionsTableView.getColumns().add(productColumn);
            this.transactionsTableView.getColumns().add(purposeColumn);
            this.transactionsTableView.getColumns().add(shopColumn);
        });

        this.transactionsTableView.setRowFactory(param -> {
            TableRow<VariableTransaction> row = new TableRow<>();
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

        Platform.runLater(() -> {
            transactionsTableView.getColumns().get(1).setSortType(TableColumn.SortType.DESCENDING);
            transactionsTableView.getSortOrder().add(valueDateColumn);
        });
    }

    private void initializeFixedTransactionTable() {
        this.categoriesListView.setCellFactory(param -> new CategoryListViewImpl());

        this.categoriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showFixedTransactions(newValue);
            newFixedTransactionBtn.setDisable(!(newValue != null && !newValue.isRoot()));
            editFixedTransactionBtn.setDisable(true);
            deleteFixedTransactionBtn.setDisable(true);
        });

        this.fixedTransactionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editFixedTransactionBtn.setDisable(false);
            deleteFixedTransactionBtn.setDisable(false);
        });

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
        ObservableList<VariableTransaction> transactions = FXCollections.observableArrayList();
        if (this.categories != null) {
            this.categories.traverse(treeItem -> {
                if (!treeItem.getValue().getCategoryClass().isFixed()) {
                    for (Transaction abstractTransaction : ((CategoryTree) treeItem).getTransactions()) {
                        if (abstractTransaction instanceof VariableTransaction) {
                            transactions.add((VariableTransaction) abstractTransaction);
                        }
                    }
                }
            });
        }

        FilteredList<VariableTransaction> filteredData = new FilteredList<>(transactions, transaction -> true);
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

        SortedList<VariableTransaction> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transactionsTableView.comparatorProperty());

        transactionsTableView.setItems(sortedData);
        transactionsTableView.refresh();
    }

    private void loadFixedTransactionTableData() {
        if (this.categories != null) {
            categoriesListView.getItems().clear();
            this.categories.traverse(treeItem -> {
                if (treeItem.getValue().getCategoryClass().isFixed()) {
                    categoriesListView.getItems().add((CategoryTree) treeItem);
                }
            });
        }

        Platform.runLater(() -> this.categoriesListView.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(formatter.formatCategoryName(o1),
                formatter.formatCategoryName(o2))));

    }

    public void handleRefreshTransactions() {
        RetrievalServiceImpl.getInstance().fetchTransactions(this.user, new AsyncCall<>() {
            @Override
            public void onSuccess(BaseCategory result) {
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
            public void onSuccess(BaseCategory result) {
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
        TransactionDialog dialog = new TransactionDialog(null, this.categories);
        dialog.setOnConfirm(transaction -> {

            if (user.getSettings().isChangeAmountSignAutomatically()) {
                transaction.adjustAmountSign();
            }

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("variableTransaction", transaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "addTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    transaction.setId(((VariableTransaction) result.getResult()).getId());
                    localStorage.writeObject("categories", categories);

                    FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succAddedTransaction"));
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> {
                        loadTransactionTableData();
                        loadTransactionOverviewTableData();
                    });
                }
            }, true));
        });
    }

    public void handleNewFixedTransaction() {
        FixedTransactionDialog dialog = new FixedTransactionDialog(null,
                (this.categoriesListView.getSelectionModel().getSelectedItem()));
        dialog.setOnConfirm(fixedTransaction -> {

            if (user.getSettings().isChangeAmountSignAutomatically()) {
                fixedTransaction.adjustAmountSign();
            }

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("fixedTransaction", fixedTransaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "addFixedTransactions", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    handleRefreshFixedTransactions();
                    FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succAddedFixedTransaction"));
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        });
    }

    public void handleEditTransaction() {
        TransactionDialog dialog = new TransactionDialog(this.transactionsTableView.getSelectionModel().getSelectedItem(),
                this.categories);
        dialog.setOnConfirm(transaction -> {

            if (user.getSettings().isChangeAmountSignAutomatically()) {
                transaction.adjustAmountSign();
            }

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("variableTransaction", transaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateTransaction",
                    parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    localStorage.writeObject("categories", categories);
                    Platform.runLater(() -> transactionsTableView.refresh());

                    FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succUpdatedTransaction"));
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        });
    }

    public void handleEditFixedTransaction() {
        FixedTransactionDialog dialog = new FixedTransactionDialog(
                this.fixedTransactionsListView.getSelectionModel().getSelectedItem(),
                this.categoriesListView.getSelectionModel().getSelectedItem());
        dialog.setOnConfirm(fixedTransaction -> {

            if (user.getSettings().isChangeAmountSignAutomatically()) {
                fixedTransaction.adjustAmountSign();
            }

            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("fixedTransaction", fixedTransaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateFixedTransaction", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    fixedTransaction.getTransactionAmounts().clear();
                    fixedTransaction.getTransactionAmounts().addAll(((FixedTransaction) result.getResult()).getTransactionAmounts());
                    localStorage.writeObject("categories", categories);

                    Platform.runLater(() -> {
                        fixedTransactionsListView.refresh();
                        categoriesListView.refresh();
                    });

                    FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succUpdatedFixedTransaction"));
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        });
    }

    public void handleDeleteTransaction() {
        FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("confirmDeleteTransaction"));
        dialog.setOnConfirm(result -> {
            VariableTransaction transaction = this.transactionsTableView.getSelectionModel().getSelectedItem();
            if (transaction != null) {
                Map<String, Serializable> parameters = new HashMap<>();
                parameters.put("variableTransactionId", transaction.getId());

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteTransaction", parameters, new AsyncConnectionCall() {
                    @Override
                    public void onSuccess(ConnectionResult result) {
                        Platform.runLater(() -> {
                            transaction.getCategoryTree().getTransactions().remove(transaction);
                            loadTransactionTableData();
                            localStorage.writeObject("categories", categories);
                        });

                        FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succDeletedTransaction"));
                    }

                    @Override
                    public void onAfter() {
                        Platform.runLater(() -> loadTransactionOverviewTableData());
                    }
                }, true));
            }
        });
    }

    public void handleDeleteFixedTransaction() {
        FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("confirmDeleteFixedTransaction"));
        dialog.setOnConfirm(result -> {
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("fixedTransactionId", this.fixedTransactionsListView.getSelectionModel().getSelectedItem().getId());

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteFixedTransaction",
                    parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    Platform.runLater(() -> {
                        fixedTransactionsListView.getSelectionModel().getSelectedItem().getCategoryTree().getTransactions().remove(
                                fixedTransactionsListView.getSelectionModel().getSelectedItem());
                        fixedTransactionsListView.getItems().remove(fixedTransactionsListView.getSelectionModel().getSelectedItem());
                    });
                    localStorage.writeObject("categories", categories);

                    fixedTransactionsListView.refresh();
                    categoriesListView.refresh();

                    FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succDeletedFixedTransaction"));
                }

                @Override
                public void onAfter() {
                    Platform.runLater(() -> loadTransactionOverviewTableData());
                }
            }, true));
        });
    }

    private void showFixedTransactions(CategoryTree category) {
        if (category != null) {
            this.fixedTransactionsListView.getItems().clear();
            for (Transaction abstractTransaction : category.getTransactions()) {
                if (abstractTransaction instanceof FixedTransaction) {
                    this.fixedTransactionsListView.getItems().add((FixedTransaction) abstractTransaction);
                }
            }
        }

        this.fixedTransactionsListView.setCellFactory(param -> new FixedTransactionListCellImpl());
        fixedTransactionsListView.getItems().sort(Comparator.comparing(FixedTransaction::getStartDate).reversed());
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
        private Label activeLabel;
        private Label dateLabel;
        private Label amountLabel;
        private Label isVariableLabel;
        private Label dayLabel;
        private Label lastAmountLabel;
        private Label preLastAmountLabel;

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
                    this.dateLabel.setText(I18N.get("since") + " " + formatter.formatDate(item.getStartDate()));
                } else {
                    this.dateLabel.setText(formatter.formatDate(item.getStartDate()) + " - " + formatter.formatDate(item.getEndDate()));
                }

                LocalDate valueDate = (item.isActive() ? LocalDate.now() : item.getEndDate());
                formatter.formatAmountLabel(this.amountLabel, item.getAmount(valueDate));
                if (item.getIsVariable()) {
                    formatter.formatAmountLabel(this.lastAmountLabel, item.getAmount(valueDate.minusMonths(1)));
                    formatter.formatAmountLabel(this.preLastAmountLabel, item.getAmount(valueDate.minusMonths(2)));
                }

                this.isVariableLabel.setText(I18N.get("isVariable") + ": " +
                        (item.getIsVariable() ? I18N.get("yes") : I18N.get("no")));
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

            if (item.getIsVariable()) {
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
        private double[] amounts;

        TransactionOverviewRow(CategoryTree categoryTree) {
            this.categoryTree = categoryTree;
            this.amounts = new double[((LocalSettingsImpl) localStorage.readObject("localSettings")).getMaxNumberOfMonthsDisplayed()];
        }

        CategoryTree getCategory() {
            return categoryTree;
        }

        double[] getAmounts() {
            return amounts;
        }
    }
}
