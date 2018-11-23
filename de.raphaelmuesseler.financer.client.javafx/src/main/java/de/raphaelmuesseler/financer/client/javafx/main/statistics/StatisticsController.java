package de.raphaelmuesseler.financer.client.javafx.main.statistics;

import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.util.collections.Tree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {
    private LocalStorage localStorage = LocalStorageImpl.getInstance();
    private BaseCategory categories;

    public Label variableExpensesNoDataLabel;
    public PieChart variableExpensesDistributionChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.categories = (BaseCategory) localStorage.readObject("categories");

        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (Tree<Category> categoryTree : this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.VARIABLE_EXPENSES).getChildren()) {
            double amount = ((CategoryTree) categoryTree).getAmount(LocalDate.now());
            if (amount != 0) {
                variableExpensesData.add(new PieChart.Data(categoryTree.getValue().getName(), Math.abs(amount)));
            }
        }

        if (variableExpensesData.size() > 0) {
            this.variableExpensesDistributionChart.setManaged(true);
            this.variableExpensesNoDataLabel.setManaged(false);
            this.variableExpensesDistributionChart.setData(variableExpensesData);
        } else {
            this.variableExpensesDistributionChart.setManaged(false);
            this.variableExpensesNoDataLabel.setManaged(true);
        }
    }
}
