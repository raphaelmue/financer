package org.financer.client.javafx.main.overview;

import com.jfoenix.controls.JFXComboBox;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.charts.DonutChart;
import org.financer.client.javafx.components.charts.SmoothedChart;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.javafx.main.transactions.TransactionAmountDialog;
import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.categories.CategoryTree;
import org.financer.shared.model.transactions.FixedTransaction;
import org.financer.shared.model.transactions.Transaction;
import org.financer.shared.model.transactions.TransactionAmount;
import org.financer.shared.model.transactions.VariableTransaction;
import org.financer.shared.model.user.User;
import org.financer.util.collections.TreeUtil;
import org.financer.util.concurrency.FinancerExecutor;
import org.financer.util.date.DateUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OverviewController implements Initializable {
    @FXML
    public GridPane lastTransactionsGridPane;
    @FXML
    public GridPane balanceGridPane;
    @FXML
    public GridPane upcomingFixedTransactionGridPane;
    @FXML
    public Label balanceChangeLabel;
    @FXML
    public Label balanceLabel;
    @FXML
    public Label variableExpensesLabel;
    @FXML
    public Label variableExpensesChangeLabel;
    @FXML
    public Label numberOfTransactionsChangeLabel;
    @FXML
    public Label numberOfTransactionsLabel;
    @FXML
    public JFXComboBox<String> balanceChartMonthComboBox;
    @FXML
    public JFXComboBox<String> variableExpensesDistributionMonthComboBox;
    @FXML
    public DonutChart variableExpensesDistributionPieChart;
    @FXML
    public SmoothedChart<String, Number> balanceChart;

    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private JavaFXFormatter formatter = new JavaFXFormatter(localStorage);
    private BaseCategory categories;
    private User user;

    private double balanceAmount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            categories = (BaseCategory) this.localStorage.readObject("categories");
            user = (User) localStorage.readObject("user");

            loadDetailedBalance();
            loadLatestTransactions();
            loadUpcomingFixedTransactions();

            loadBalanceWidget();
            loadVariableExpensesWidget();
            loadNumberOfTransactionsWidget();

            initializeBalanceChart();
            initializeDistributionChart();

            FinancerController.getInstance().hideLoadingBox();
        }));
        FinancerController.getInitializationThread().start();
    }

    private void loadBalanceWidget() {
        final double balanceRatio = this.balanceAmount / categories.getAmount(LocalDate.now().minusMonths(1)) * 100 - 100;
        Platform.runLater(() -> {
            this.balanceLabel.setText(formatter.formatCurrency(this.balanceAmount));
            formatter.formatChangeLabel(this.balanceChangeLabel, balanceRatio);
            if (!Double.isNaN(balanceRatio) && Double.isFinite(balanceRatio))
                this.balanceChangeLabel.setText((balanceRatio < 0 ? "" : "+") + this.balanceChangeLabel.getText());
        });
    }

    private void loadVariableExpensesWidget() {
        final double variableExpensesAmount = this.categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES).getAmount(LocalDate.now());
        final double variableExpensesRatio = variableExpensesAmount / this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.VARIABLE_EXPENSES).getAmount(LocalDate.now().minusMonths(1)) * 100 - 100;
        Platform.runLater(() -> {
            this.variableExpensesLabel.setText(formatter.formatCurrency(variableExpensesAmount));
            formatter.formatChangeLabel(this.variableExpensesChangeLabel, variableExpensesRatio);
            if (!Double.isNaN(variableExpensesRatio) && Double.isFinite(variableExpensesRatio))
                this.variableExpensesChangeLabel.setText((variableExpensesRatio < 0 ? "" : "+") + this.variableExpensesChangeLabel.getText());
        });
    }

    private void loadNumberOfTransactionsWidget() {
        AtomicInteger numberOfTransactions = new AtomicInteger();
        TreeUtil.traverse(categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES),
                object -> numberOfTransactions.addAndGet((int) ((CategoryTree) object).getTransactions().stream()
                        .filter(transaction -> DateUtil.checkIfMonthsAreEqual(((VariableTransaction) transaction).getValueDate(), LocalDate.now()))
                        .count()));
        TreeUtil.traverse(categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE),
                object -> numberOfTransactions.addAndGet((int) ((CategoryTree) object).getTransactions().stream()
                        .filter(transaction -> DateUtil.checkIfMonthsAreEqual(((VariableTransaction) transaction).getValueDate(), LocalDate.now()))
                        .count()));

        AtomicInteger numberOfTransactionsLastMonth = new AtomicInteger();
        TreeUtil.traverse(categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES),
                object -> numberOfTransactionsLastMonth.addAndGet((int) ((CategoryTree) object).getTransactions().stream()
                        .filter(transaction -> DateUtil.checkIfMonthsAreEqual(((VariableTransaction) transaction).getValueDate(), LocalDate.now().minusMonths(1)))
                        .count()));
        TreeUtil.traverse(categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE),
                object -> numberOfTransactionsLastMonth.addAndGet((int) ((CategoryTree) object).getTransactions().stream()
                        .filter(transaction -> DateUtil.checkIfMonthsAreEqual(((VariableTransaction) transaction).getValueDate(), LocalDate.now().minusMonths(1)))
                        .count()));

        double numberOfTransactionsRatio = (double) numberOfTransactions.get() / (double) numberOfTransactionsLastMonth.get() * 100 - 100;
        Platform.runLater(() -> {
            this.numberOfTransactionsLabel.setText(Integer.toString(numberOfTransactions.get()));
            formatter.formatChangeLabel(this.numberOfTransactionsChangeLabel, numberOfTransactionsRatio);
            if (!Double.isNaN(numberOfTransactionsRatio) && Double.isFinite(numberOfTransactionsRatio))
                this.numberOfTransactionsChangeLabel.setText((numberOfTransactionsRatio < 0 ? "" : "+")
                        + this.numberOfTransactionsChangeLabel.getText());
        });
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
                if (counter >= 7) {
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

    private void loadDetailedBalance() {
        balanceAmount = 0;
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
        TransactionAmountDialog dialog = new TransactionAmountDialog(transactionAmount, new ArrayList<>(transaction.getTransactionAmounts()));


        dialog.setOnConfirm(result -> {
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("transactionAmount", result);
            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "addTransactionAmount", parameters, result1 -> {
                transaction.getTransactionAmounts().add((TransactionAmount) result1.getResult());
                localStorage.writeObject("categories", categories);
                Platform.runLater(() -> upcomingFixedTransactionGridPane.getChildren().clear());
                loadUpcomingFixedTransactions();
            }));
        });
    }

    private void initializeBalanceChart() {
        balanceChart.setChartType(SmoothedChart.ChartType.AREA);
        balanceChart.setAnimated(false);
        this.balanceChartMonthComboBox.getItems().add(I18N.get("lastMonths", 3));
        this.balanceChartMonthComboBox.getItems().add(I18N.get("lastMonths", 6));
        this.balanceChartMonthComboBox.getItems().add(I18N.get("lastMonths", 12));
        this.balanceChartMonthComboBox.valueProperty().addListener(
                (options, oldValue, newValue) -> loadBalanceChartData());
        Platform.runLater(() -> this.balanceChartMonthComboBox.getSelectionModel().select(0));
    }

    private void loadBalanceChartData() {
        balanceChart.getData().clear();
        XYChart.Series<String, Number> data = new XYChart.Series<>();
        data.setName(I18N.get("balance"));
        int numberOfMonths = (int) (1.5 * Math.pow(this.balanceChartMonthComboBox.getSelectionModel().getSelectedIndex(), 2)
                + 1.5 * this.balanceChartMonthComboBox.getSelectionModel().getSelectedIndex() + 3);
        for (int i = numberOfMonths - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            XYChart.Data<String, Number> dataSet = new XYChart.Data<>(formatter.formatMonth(date), categories.getAmount(date));
            Platform.runLater(() -> Tooltip.install(dataSet.getNode(),
                    new Tooltip(dataSet.getXValue() + "\n" +
                            I18N.get("amount") + ": \t" + formatter.formatCurrency((Double) dataSet.getYValue()))));
            data.getData().add(dataSet);
        }

        balanceChart.getData().add(data);
    }

    private void initializeDistributionChart() {
        this.variableExpensesDistributionMonthComboBox.getItems().add(I18N.get("thisMonth"));
        this.variableExpensesDistributionMonthComboBox.getItems().add(I18N.get("lastMonths", 3));
        this.variableExpensesDistributionMonthComboBox.getItems().add(I18N.get("lastMonths", 6));
        this.variableExpensesDistributionMonthComboBox.getItems().add(I18N.get("lastMonths", 12));
        this.variableExpensesDistributionMonthComboBox.valueProperty().addListener(
                (options, oldValue, newValue) -> loadDistributionChartData());
        Platform.runLater(() -> this.variableExpensesDistributionMonthComboBox.getSelectionModel().select(0));
    }

    private void loadDistributionChartData() {
        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (CategoryTree categoryTree : this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.VARIABLE_EXPENSES).getChildren()) {
            double amount;
            if (this.variableExpensesDistributionMonthComboBox.getSelectionModel().getSelectedIndex() == 0) {
                amount = categoryTree.getAmount(LocalDate.now());
            } else {
                int numberOfMonths = (int) (1.5 * Math.pow(this.variableExpensesDistributionMonthComboBox.getSelectionModel().getSelectedIndex(), 2)
                        + 1.5 * this.balanceChartMonthComboBox.getSelectionModel().getSelectedIndex() + 3);
                amount = categoryTree.getAmount(LocalDate.now().minusMonths(numberOfMonths), LocalDate.now());
            }
            if (amount != 0) {
                PieChart.Data data = new PieChart.Data(categoryTree.getValue().getName(), Math.abs(amount));
                Platform.runLater(() -> Tooltip.install(data.getNode(),
                        new Tooltip(formatter.formatCategoryName(categoryTree) + "\n" +
                                I18N.get("amount") + ": \t" + formatter.formatCurrency(data.getPieValue()))));
                variableExpensesData.add(data);
            }
        }
        this.variableExpensesDistributionPieChart.setData(variableExpensesData);
    }
}
