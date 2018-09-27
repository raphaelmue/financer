package de.raphaelmuesseler.financer.shared.server.main.transactions;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.format.I18N;
import de.raphaelmuesseler.financer.client.ui.components.DoubleField;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Comparator;

class TransactionDialog extends FinancerDialog<Transaction> {

    private DoubleField amountField;
    private ComboBox<Category> categoryComboBox;
    private TextField productField;
    private TextField purposeField;
    private TextField shopField;
    private JFXDatePicker valueDateField;
    private SerialTreeItem<Category> tree;

    TransactionDialog(Transaction transaction) {
        super(transaction);

        this.setHeaderText(I18N.get("transaction"));

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        this.prepareDialogContent();
    }

    @Override
    protected Node setDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(80);
        gridPane.setVgap(8);

        gridPane.add(new Label(I18N.get("amount")), 0, 0);
        this.amountField = new DoubleField();
        gridPane.add(this.amountField, 1, 0);

        gridPane.add(new Label(I18N.get("category")), 0, 1);
        this.categoryComboBox = new ComboBox<>();
        this.categoryComboBox.setPlaceholder(new Label(I18N.get("selectCategory")));
        this.tree = SerialTreeItem.fromJson((String) LocalStorage.readObject(LocalStorage.PROFILE_FILE, "categories"),
                Category.class);

        this.tree.numberItemsByValue((result, prefix) -> {
            result.getValue().setName(prefix + " " + result.getValue().getName());
            result.getValue().setKey(false);
        });

        tree.traverse(treeItem -> {
            // selecting only variable revenue (id: 1) and variable expenses (id: 3) => id % 2 == 1
            if ((treeItem.getValue().getRootId() != -1 && (treeItem.getValue().getRootId() % 2) == 1) ||
                    (treeItem.getValue().getRootId() == -1 && (treeItem.getValue().getParentId() % 2) == 1)) {
                categoryComboBox.getItems().add(treeItem.getValue());
            }
        });
        gridPane.add(this.categoryComboBox, 1, 1);

        gridPane.add(new Label(I18N.get("product")), 0, 2);
        this.productField = new TextField();
        gridPane.add(this.productField, 1, 2);

        gridPane.add(new Label(I18N.get("purpose")), 0, 3);
        this.purposeField = new TextField();
        gridPane.add(purposeField, 1, 3);

        gridPane.add(new Label(I18N.get("shop")), 0, 4);
        this.shopField = new TextField();
        gridPane.add(this.shopField, 1, 4);

        gridPane.add(new Label(I18N.get("product")), 0, 5);
        this.valueDateField = new JFXDatePicker();
        gridPane.add(this.valueDateField, 1, 5);

        return gridPane;
    }

    @Override
    protected void prepareDialogContent() {
        if (this.getValue() != null) {
            this.amountField.setText(String.valueOf(this.getValue().getAmount()));
            tree.getItemByValue(this.getValue().getCategory(), serialTreeItem -> {
                this.categoryComboBox.getSelectionModel().select(serialTreeItem.getValue());
            }, Comparator.comparingInt(Category::getId));
            this.productField.setText(this.getValue().getProduct());
            this.purposeField.setText(this.getValue().getPurpose());
            this.shopField.setText(this.getValue().getShop());
            this.valueDateField.setValue(this.getValue().getValueDate());
        }
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            setErrorMessage(I18N.get("selectCategory"));
            result = false;
        }

        if (Double.valueOf(this.amountField.getText()) == 0) {
            setErrorMessage(I18N.get("selectValidAmount"));
            result = false;
        }

        return result;
    }

    @Override
    protected Transaction onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new Transaction(-1, Double.valueOf(this.amountField.getText()),
                    this.categoryComboBox.getSelectionModel().getSelectedItem(), this.productField.getText(),
                    this.purposeField.getText(), this.valueDateField.getValue(), this.shopField.getText()));
        } else {
            this.getValue().setAmount(Double.valueOf(this.amountField.getText()));
            this.getValue().setCategory(this.categoryComboBox.getSelectionModel().getSelectedItem());
            this.getValue().setProduct(this.productField.getText());
            this.getValue().setPurpose(this.purposeField.getText());
            this.getValue().setValueDate(this.valueDateField.getValue());
            this.getValue().setShop(this.shopField.getText());
        }

        return super.onConfirm();
    }
}