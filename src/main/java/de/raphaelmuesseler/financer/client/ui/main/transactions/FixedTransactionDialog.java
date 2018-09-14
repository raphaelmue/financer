package de.raphaelmuesseler.financer.client.ui.main.transactions;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.sun.javafx.scene.control.IntegerField;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.components.DoubleField;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;

public class FixedTransactionDialog extends FinancerDialog<FixedTransaction> {

    private Category category;
    private SerialTreeItem<Category> tree;
    private Label categoryLabel;
    private IntegerField dayField;
    private JFXDatePicker startDateField, endDateField;
    private CheckBox isVariableCheckbox;
    private DoubleField amountField;
    private VBox transactionAmountContainer;
    private JFXListView<TransactionAmount> transactionAmountListView;

    public FixedTransactionDialog(FixedTransaction value, Category category) {
        super(value);

        this.category = category;

        this.setHeaderText(I18N.get("fixedTransactions"));

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        this.prepareDialogContent();
    }

    @Override
    protected Node setDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(120);
        gridPane.setVgap(10);

        gridPane.add(new Label(I18N.get("category")), 0, 0);
        this.categoryLabel = new Label();
        gridPane.add(this.categoryLabel, 1, 0);

        gridPane.add(new Label(I18N.get("valueDate")), 0, 1);
        this.dayField = new IntegerField();
        this.dayField.setMaxValue(30);
        gridPane.add(this.dayField, 1, 1);

        gridPane.add(new Label(I18N.get("startDate")), 0, 2);
        this.startDateField = new JFXDatePicker();
        this.startDateField.setValue(LocalDate.now());
        gridPane.add(this.startDateField, 1, 2);

        gridPane.add(new Label(I18N.get("endDate")), 0, 3);
        this.endDateField = new JFXDatePicker();
        gridPane.add(this.endDateField, 1, 3);

        gridPane.add(new Label(I18N.get("isVariable")), 0, 4);
        this.isVariableCheckbox = new CheckBox();
        this.isVariableCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (this.amountField != null) {
                this.amountField.setDisable(newValue);
            }
        });
        gridPane.add(this.isVariableCheckbox, 1, 4);

        gridPane.add(new Label(I18N.get("amount")), 0, 5);
        this.amountField = new DoubleField();
        gridPane.add(this.amountField, 1, 5);

        this.transactionAmountContainer = new VBox();
        this.transactionAmountContainer.setPrefHeight(200);
        this.transactionAmountContainer.getChildren().add(new Label());
        this.transactionAmountListView = new JFXListView<>();
        this.transactionAmountListView.setCellFactory(param -> new ListCell<TransactionAmount>() {
            @Override
            protected void updateItem(TransactionAmount item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    BorderPane borderPane = new BorderPane();
                    borderPane.getStyleClass().add("transactions-list-item");
                    borderPane.setLeft(new Label(item.getValueDate().toString()));
                    Label amountLabel = new Label(Double.toString(item.getAmount()));
                    if (item.getAmount() < 0) {
                        amountLabel.getStyleClass().add("neg-amount");
                    } else {
                        amountLabel.getStyleClass().add("pos-amount");
                    }
                    borderPane.setRight(amountLabel);
                    setGraphic(borderPane);
                }
            }
        });
        this.transactionAmountContainer.getChildren().add(this.transactionAmountListView);
        gridPane.add(this.transactionAmountContainer, 0, 6, 2, 1);

        return gridPane;
    }

    @Override
    protected void prepareDialogContent() {
        this.categoryLabel.setText(this.category.getName());
        if (this.getValue() != null) {
            this.dayField.setValue(this.getValue().getDay());
            this.startDateField.setValue(this.getValue().getStartDate());
            this.endDateField.setValue(this.getValue().getEndDate());
            this.isVariableCheckbox.setSelected(this.getValue().isVariable());
            if (this.getValue().isVariable()) {
                this.transactionAmountListView.getItems().addAll(this.getValue().getTransactionAmounts());
                this.amountField.setDisable(true);
            } else {
                this.amountField.setText(Double.toString(this.getValue().getAmount()));
                this.transactionAmountContainer.setPrefHeight(0);
                this.transactionAmountContainer.setVisible(false);
                this.transactionAmountContainer.setDisable(true);
            }
        } else {
            this.transactionAmountContainer.setPrefHeight(0);
            this.transactionAmountContainer.setVisible(false);
            this.transactionAmountContainer.setDisable(true);
        }
    }

    @Override
    protected FixedTransaction onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new FixedTransaction(-1,
                    Double.valueOf(this.amountField.getText()),
                    this.category,
                    "", "",
                    this.startDateField.getValue(),
                    this.endDateField.getValue(),
                    this.isVariableCheckbox.isSelected(),
                    this.dayField.getValue(),
                    (this.isVariableCheckbox.isSelected() ? this.getValue().getTransactionAmounts() : null)));
        } else {
            this.getValue().setStartDate(this.startDateField.getValue());
            this.getValue().setEndDate(this.endDateField.getValue());
            this.getValue().setVariable(this.isVariableCheckbox.isSelected());
            this.getValue().setDay(this.dayField.getValue());
            this.getValue().setAmount(Double.valueOf(this.amountField.getText()));
        }

        return super.onConfirm();
    }
}
