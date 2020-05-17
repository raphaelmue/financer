package org.financer.client.javafx.main.overview;

import com.jfoenix.controls.JFXComboBox;
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
import org.financer.client.domain.api.RestApi;
import org.financer.client.domain.api.RestApiImpl;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.category.CategoryRoot;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;
import org.financer.client.domain.model.transaction.Transaction;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.charts.DonutChart;
import org.financer.client.javafx.components.charts.SmoothedChart;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.javafx.main.transactions.TransactionAmountDialog;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.util.collections.TreeUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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

    private final RestApi restApi = new RestApiImpl();

    private Amount balanceAmount = new Amount();
    private final LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private final JavaFXFormatter formatter = new JavaFXFormatter(localStorage);
    private CategoryRoot categoryRoot;
    private User user;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            categoryRoot = this.localStorage.readObject("categories");
            user = localStorage.readObject("user");

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
        final Amount balanceRatio = this.balanceAmount.calcRatio(categoryRoot.getAmount(new ValueDate(LocalDate.now().minusMonths(1))));
        Platform.runLater(() -> {
            this.balanceLabel.setText(formatter.format(this.balanceAmount));
            formatter.format(this.balanceChangeLabel, balanceRatio);
            if (!Double.isNaN(balanceRatio.getAmount()) && Double.isFinite(balanceRatio.getAmount())) {
                this.balanceChangeLabel.setText((balanceRatio.isNegative() ? "" : "+") + this.balanceChangeLabel.getText());
            }
        });
    }

    private void loadVariableExpensesWidget() {
        final Amount variableExpensesAmount = this.categoryRoot.getAmount(CategoryClass.Values.VARIABLE_EXPENSES, new ValueDate());
        final Amount variableExpensesRatio = variableExpensesAmount.calcRatio(this.categoryRoot.getAmount(CategoryClass.Values.VARIABLE_EXPENSES, new ValueDate(LocalDate.now().minusMonths(1))));
        Platform.runLater(() -> {
            this.variableExpensesLabel.setText(formatter.format(variableExpensesAmount));
            formatter.formatChangeLabel(this.variableExpensesChangeLabel, variableExpensesRatio.getAmount());
            if (!Double.isNaN(variableExpensesRatio.getAmount()) && Double.isFinite(variableExpensesRatio.getAmount()))
                this.variableExpensesChangeLabel.setText((variableExpensesRatio.isNegative() ? "" : "+") + this.variableExpensesChangeLabel.getText());
        });
    }

    private void loadNumberOfTransactionsWidget() {
        AtomicInteger numberOfTransactions = new AtomicInteger();
        TreeUtil.traverse(categoryRoot.getCategoriesByClass(CategoryClass.Values.VARIABLE_EXPENSES),
                object -> numberOfTransactions.addAndGet((int) ((Category) object).getTransactions().stream()
                        .filter(transaction -> ((VariableTransaction) transaction).getValueDate().isInSameMonth(new ValueDate()))
                        .count()));
        TreeUtil.traverse(categoryRoot.getCategoriesByClass(CategoryClass.Values.VARIABLE_REVENUE),
                object -> numberOfTransactions.addAndGet((int) ((Category) object).getTransactions().stream()
                        .filter(transaction -> ((VariableTransaction) transaction).getValueDate().isInSameMonth(new ValueDate()))
                        .count()));

        AtomicInteger numberOfTransactionsLastMonth = new AtomicInteger();
        TreeUtil.traverse(categoryRoot.getCategoriesByClass(CategoryClass.Values.VARIABLE_EXPENSES),
                object -> numberOfTransactionsLastMonth.addAndGet((int) ((Category) object).getTransactions().stream()
                        .filter(transaction -> ((VariableTransaction) transaction).getValueDate().isInSameMonth(new ValueDate(LocalDate.now().minusMonths(1))))
                        .count()));
        TreeUtil.traverse(categoryRoot.getCategoriesByClass(CategoryClass.Values.VARIABLE_REVENUE),
                object -> numberOfTransactionsLastMonth.addAndGet((int) ((Category) object).getTransactions().stream()
                        .filter(transaction -> ((VariableTransaction) transaction).getValueDate().isInSameMonth(new ValueDate(LocalDate.now().minusMonths(1))))
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
        categoryRoot.traverse(categoryTree -> {
            for (Transaction transaction : ((Category) categoryTree).getTransactions()) {
                if (!transaction.isFixed()) {
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
                Platform.runLater(() -> this.lastTransactionsGridPane.add(new Label(transaction.getCategory().getName()),
                        0, _counter));

                Label amountLabel = formatter.format(new Label(), transaction.getAmount());
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
        int counter = 0;
        for (CategoryClass categoryClass : CategoryClass.getAll()) {
            final int _counter = counter;
            Platform.runLater(() -> balanceGridPane.add(new Label(I18N.get(categoryClass.getCategoryClass().getName())), 0, _counter));
            final Amount amount = categoryRoot.getAmount(categoryClass.getCategoryClass(), new ValueDate());
            Label baseCategoryLabel = formatter.format(new Label(), amount);
            balanceAmount = balanceAmount.add(amount);
            GridPane.setHalignment(baseCategoryLabel, HPos.RIGHT);
            GridPane.setHgrow(baseCategoryLabel, Priority.ALWAYS);
            GridPane.setVgrow(baseCategoryLabel, Priority.ALWAYS);
            Platform.runLater(() -> balanceGridPane.add(baseCategoryLabel, 1, _counter));
            counter++;
        }

        Label balanceTextLabel = new Label(I18N.get("balance"));
        balanceTextLabel.setId("balance-label");
        Platform.runLater(() -> balanceGridPane.add(balanceTextLabel, 0, 4));
        Label balanceLabel = formatter.format(new Label(), balanceAmount);
        balanceLabel.setId("balance-amount");
        GridPane.setHalignment(balanceLabel, HPos.RIGHT);
        GridPane.setHgrow(balanceLabel, Priority.ALWAYS);
        GridPane.setVgrow(balanceLabel, Priority.ALWAYS);
        Platform.runLater(() -> balanceGridPane.add(balanceLabel, 1, 4));
    }

    private void loadUpcomingFixedTransactions() {
        List<FixedTransaction> transactions = new ArrayList<>();
        categoryRoot.traverse(categoryTree -> {
            for (Transaction transaction : ((Category) categoryTree).getTransactions()) {
                if (transaction.isFixed() && ((FixedTransaction) transaction).isActive() &&
                        (transaction.getAmount(new ValueDate()).getAmount() == 0)) {
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
                    Hyperlink link = new Hyperlink(transaction.getCategory().getName());
                    link.setOnAction(event -> addTransactionAmount(transaction));
                    Platform.runLater(() -> this.upcomingFixedTransactionGridPane.add(link, 0, _counter));
                } else {
                    Platform.runLater(() -> this.upcomingFixedTransactionGridPane.add(new Label(transaction.getCategory().getName()),
                            0, _counter));
                }

                Label dayLabel = new Label(formatter.format(LocalDate.now().withDayOfMonth(transaction.getDay())));
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
        FixedTransactionAmount transactionAmount = new FixedTransactionAmount()
                .setId(0)
                .setAmount(new Amount())
                .setValueDate(new ValueDate());
        transactionAmount.setFixedTransaction(transaction);
        TransactionAmountDialog dialog = new TransactionAmountDialog(transactionAmount, new ArrayList<>(transaction.getTransactionAmounts()));

        dialog.setOnConfirm(result -> {
            restApi.createTransactionAmount(transaction.getId(), transactionAmount, fixedTransactionAmount -> {
                transaction.getTransactionAmounts().add(transactionAmount);
                localStorage.writeObject("categories", categoryRoot);
                Platform.runLater(() -> upcomingFixedTransactionGridPane.getChildren().clear());
                loadUpcomingFixedTransactions();
            }).execute();
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
            XYChart.Data<String, Number> dataSet = new XYChart.Data<>(formatter.format(date), categoryRoot.getAmount(new ValueDate(date)).getAmount());
            Platform.runLater(() -> Tooltip.install(dataSet.getNode(),
                    new Tooltip(dataSet.getXValue() + "\n" +
                            I18N.get("amount") + ": \t" + formatter.format((Double) dataSet.getYValue()))));
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
        for (Category category : this.categoryRoot.getCategoriesByClass(CategoryClass.Values.VARIABLE_EXPENSES)) {
            Amount amount;
            if (this.variableExpensesDistributionMonthComboBox.getSelectionModel().getSelectedIndex() == 0) {
                amount = category.getAmount(new ValueDate());
            } else {
                int numberOfMonths = (int) (1.5 * Math.pow(this.variableExpensesDistributionMonthComboBox.getSelectionModel().getSelectedIndex(), 2)
                        + 1.5 * this.balanceChartMonthComboBox.getSelectionModel().getSelectedIndex() + 3);
                amount = category.getAmount(new TimeRange(LocalDate.now().minusMonths(numberOfMonths), LocalDate.now()));
            }
            if (amount.isNotNull()) {
                PieChart.Data data = new PieChart.Data(category.getName(), Math.abs(amount.getAmount()));
                Platform.runLater(() -> Tooltip.install(data.getNode(),
                        new Tooltip(formatter.format(category) + "\n" +
                                I18N.get("amount") + ": \t" + formatter.format(data.getPieValue()))));
                variableExpensesData.add(data);
            }
        }
        this.variableExpensesDistributionPieChart.setData(variableExpensesData);
    }
}
