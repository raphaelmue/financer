package org.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.financer.client.format.Formatter;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DatePicker;
import org.financer.client.javafx.components.DoubleField;
import org.financer.client.javafx.components.IntegerField;
import org.financer.client.javafx.dialogs.FinancerConfirmDialog;
import org.financer.client.javafx.dialogs.FinancerDialog;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.model.categories.CategoryTree;
import org.financer.shared.model.transactions.FixedTransaction;
import org.financer.shared.model.transactions.TransactionAmount;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class FixedTransactionDialog extends FinancerDialog<FixedTransaction> {

    private CategoryTree categoryTree;
    private Label categoryLabel;
    private IntegerField dayField;
    private DatePicker startDateField;
    private DatePicker endDateField;
    private JFXTextField productField;
    private JFXTextField purposeField;
    private JFXCheckBox isVariableCheckbox;
    private DoubleField amountField;
    private VBox transactionAmountContainer;
    private JFXListView<TransactionAmount> transactionAmountListView;

    private static final Formatter formatter = new JavaFXFormatter(LocalStorageImpl.getInstance());

    FixedTransactionDialog(FixedTransaction value, CategoryTree category) {
        super(value);

        this.categoryTree = category;

        this.prepareDialogContent();
        this.setDialogTitle(I18N.get("fixedTransactions"));
    }

    @Override
    protected Region getDialogContent() {
        HBox hBox = new HBox();
        hBox.setSpacing(30);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(120);
        gridPane.setVgap(10);

        gridPane.add(new Label(I18N.get("category")), 0, 0);
        this.categoryLabel = new Label();
        gridPane.add(this.categoryLabel, 1, 0);

        gridPane.add(new Label(I18N.get("valueDate")), 0, 1);
        this.dayField = new IntegerField();
        this.dayField.setId("dayTextField");
        this.dayField.setValue(1);
        gridPane.add(this.dayField, 1, 1);

        gridPane.add(new Label(I18N.get("startDate")), 0, 2);
        this.startDateField = new DatePicker(this.formatter);
        this.startDateField.setId("startDateDatePicker");
        this.startDateField.setValue(LocalDate.now());
        gridPane.add(this.startDateField, 1, 2);

        gridPane.add(new Label(I18N.get("endDate")), 0, 3);
        this.endDateField = new DatePicker(formatter);
        this.endDateField.setId("endDateDatePicker");
        gridPane.add(this.endDateField, 1, 3);

        gridPane.add(new Label(I18N.get("isVariable")), 0, 4);
        this.isVariableCheckbox = new JFXCheckBox();
        this.isVariableCheckbox.setId("isVariableCheckbox");
        this.isVariableCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (this.amountField != null) {
                this.amountField.setDisable(newValue);
            }
            if (this.getContent() != null) {
                toggleTransactionAmountContainer();
            }
        });
        gridPane.add(this.isVariableCheckbox, 1, 4);

        gridPane.add(new Label(I18N.get("amount")), 0, 5);
        this.amountField = new DoubleField();
        this.amountField.setId("amountTextField");
        gridPane.add(this.amountField, 1, 5);

        gridPane.add(new Label(I18N.get("product")), 0, 6);
        this.productField = new JFXTextField();
        this.productField.setId("productTextField");
        gridPane.add(this.productField, 1, 6);

        gridPane.add(new Label(I18N.get("purpose")), 0, 7);
        this.purposeField = new JFXTextField();
        this.purposeField.setId("purposeTextField");
        gridPane.add(this.purposeField, 1, 7);

        hBox.getChildren().add(gridPane);

        this.transactionAmountContainer = new VBox();
        this.transactionAmountContainer.setSpacing(10);
        this.transactionAmountContainer.setPrefHeight(200);
        this.transactionAmountContainer.getChildren().add(new Label(I18N.get("transactionAmounts")));

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        JFXButton newTransactionAmountBtn = new JFXButton(I18N.get("new"), fontAwesome.create(FontAwesome.Glyph.PLUS));
        newTransactionAmountBtn.setId("newTransactionAmountBtn");
        JFXButton editTransactionAmountBtn = new JFXButton(I18N.get("edit"), fontAwesome.create(FontAwesome.Glyph.EDIT));
        editTransactionAmountBtn.setId("editTransactionAmountBtn");
        editTransactionAmountBtn.setDisable(true);
        JFXButton deleteTransactionAmountBtn = new JFXButton(I18N.get("delete"), fontAwesome.create(FontAwesome.Glyph.TRASH));
        deleteTransactionAmountBtn.setId("deleteTransactionAmountBtn");
        deleteTransactionAmountBtn.setDisable(true);

        newTransactionAmountBtn.setOnAction(event -> {
            TransactionAmountDialog dialog = new TransactionAmountDialog(null, transactionAmountListView.getItems());
            dialog.setOnConfirm(transactionAmount -> {
                transactionAmount.setFixedTransaction(this.getValue());
                transactionAmountListView.getItems().add(transactionAmount);
                transactionAmountListView.getItems().sort(Comparator.comparing(TransactionAmount::getValueDate).reversed());
            });
        });
        editTransactionAmountBtn.setOnAction(event -> {
            if (transactionAmountListView.getSelectionModel().getSelectedItem() != null) {
                TransactionAmountDialog dialog = new TransactionAmountDialog(transactionAmountListView.getSelectionModel().getSelectedItem(),
                        transactionAmountListView.getItems());
                dialog.setOnConfirm(result -> transactionAmountListView.refresh());
            }
        });
        deleteTransactionAmountBtn.setOnAction(event -> {
            FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("confirmDeleteTransactionAmount"));
            dialog.setOnConfirm(result -> {
                if (transactionAmountListView.getSelectionModel().getSelectedItem() != null) {
                    transactionAmountListView.getItems().remove(transactionAmountListView.getSelectionModel().getSelectedItem());
                }
            });
        });

        HBox toolBox = new HBox();
        toolBox.setSpacing(8);
        toolBox.getChildren().add(newTransactionAmountBtn);
        toolBox.getChildren().add(editTransactionAmountBtn);
        toolBox.getChildren().add(deleteTransactionAmountBtn);

        this.transactionAmountContainer.getChildren().add(toolBox);

        this.transactionAmountListView = new JFXListView<>();
        this.transactionAmountListView.setId("transactionAmountListView");
        this.transactionAmountListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                editTransactionAmountBtn.setDisable(false);
                deleteTransactionAmountBtn.setDisable(false);
            } else {
                editTransactionAmountBtn.setDisable(true);
                deleteTransactionAmountBtn.setDisable(true);
            }
        });
        this.transactionAmountListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TransactionAmount item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    JavaFXFormatter formatter = new JavaFXFormatter(LocalStorageImpl.getInstance());
                    BorderPane borderPane = new BorderPane();
                    borderPane.getStyleClass().add("transactions-list-item");
                    borderPane.setLeft(new Label(formatter.formatDate(item.getValueDate())));
                    Label amountLabel = formatter.formatAmountLabel(item.getAmount());
                    borderPane.setRight(amountLabel);
                    setGraphic(borderPane);
                }
            }
        });
        this.transactionAmountContainer.getChildren().add(this.transactionAmountListView);
        hBox.getChildren().add(this.transactionAmountContainer);

        return hBox;
    }

    @Override
    protected double getDialogWidth() {
        return 750;
    }

    @Override
    protected void prepareDialogContent() {
        this.categoryLabel.setText(new JavaFXFormatter(LocalStorageImpl.getInstance()).formatCategoryName(this.categoryTree.getValue()));
        if (this.getValue() != null) {
            this.dayField.setValue(this.getValue().getDay());
            this.startDateField.setValue(this.getValue().getStartDate());
            this.endDateField.setValue(this.getValue().getEndDate());
            this.productField.setText(this.getValue().getProduct());
            this.purposeField.setText(this.getValue().getPurpose());
            if (this.getValue().getIsVariable()) {
                this.isVariableCheckbox.setSelected(this.getValue().getIsVariable());
                this.toggleTransactionAmountContainer(true);

                if (this.getValue().getTransactionAmounts() != null && !this.getValue().getTransactionAmounts().isEmpty()) {
                    this.transactionAmountListView.getItems().addAll(this.getValue().getTransactionAmounts());
                    this.transactionAmountListView.getItems().sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
                }
                this.amountField.setDisable(true);
            } else {
                this.amountField.setText(Double.toString(this.getValue().getAmountValue()));
                this.toggleTransactionAmountContainer(false);
            }
        } else {
            this.toggleTransactionAmountContainer(false);
        }
    }

    private void toggleTransactionAmountContainer() {
        this.transactionAmountContainer.setManaged(!this.transactionAmountContainer.isManaged());
        this.transactionAmountContainer.setVisible(!this.transactionAmountContainer.isVisible());
    }

    private void toggleTransactionAmountContainer(boolean visible) {
        this.transactionAmountContainer.setManaged(visible);
        this.transactionAmountContainer.setVisible(visible);
    }

    @Override
    protected boolean checkConsistency() {
        return true;
    }

    @Override
    protected void onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new FixedTransaction(0,
                    Double.parseDouble(this.amountField.getText()),
                    this.categoryTree,
                    this.startDateField.getValue(),
                    this.endDateField.getValue(),
                    this.productField.getText(),
                    this.purposeField.getText(),
                    this.isVariableCheckbox.isSelected(),
                    this.dayField.getValue(),
                    (this.isVariableCheckbox.isSelected() ? new HashSet<>(transactionAmountListView.getItems()) : null)));
        } else {
            this.getValue().getTransactionAmounts().clear();
            this.getValue().getTransactionAmounts().addAll(new ArrayList<>(this.transactionAmountListView.getItems()));
            this.getValue().setStartDate(this.startDateField.getValue());
            this.getValue().setEndDate(this.endDateField.getValue());
            this.getValue().setProduct(this.productField.getText());
            this.getValue().setPurpose(this.purposeField.getText());
            this.getValue().setIsVariable(this.isVariableCheckbox.isSelected());
            this.getValue().setDay(this.dayField.getValue());
            this.getValue().setAmount(Double.parseDouble(this.amountField.getText()));
        }

        super.onConfirm();
    }
}
