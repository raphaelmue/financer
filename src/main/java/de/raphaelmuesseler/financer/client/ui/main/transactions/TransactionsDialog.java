package de.raphaelmuesseler.financer.client.ui.main.transactions;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.components.DoubleField;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Comparator;

class TransactionsDialog extends FinancerDialog<Transaction> {

    private final DoubleField amountField;
    private final ComboBox<Category> categoryComboBox;
    private final TextField productField;
    private final TextField purposeField;
    private final TextField shopField;
    private final JFXDatePicker valueDateField;

    TransactionsDialog(Transaction transaction) {
        super(transaction);

        this.setHeaderText(I18N.get("transaction"));

        final DialogPane dialogPane = getDialogPane();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(80);
        gridPane.setVgap(8);

        gridPane.add(new Label(I18N.get("amount")), 0, 0);
        this.amountField = new DoubleField();
        gridPane.add(this.amountField, 1, 0);

        gridPane.add(new Label(I18N.get("category")), 0, 1);
        this.categoryComboBox = new ComboBox<>();
        this.categoryComboBox.setPlaceholder(new Label(I18N.get("selectCategory")));
        SerialTreeItem<Category> tree = SerialTreeItem.fromJson((String) LocalStorage.readObject(LocalStorage.PROFILE_FILE).get(0),
                Category.class);

        this.renameCategoriesInOrder(tree);

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

        if (transaction != null) {
            this.amountField.setText(String.valueOf(transaction.getAmount()));
            tree.getItemByValue(this.getValue().getCategory(), serialTreeItem -> {
                this.categoryComboBox.getSelectionModel().select(serialTreeItem.getValue());
            }, Comparator.comparingInt(Category::getId));
            this.productField.setText(transaction.getProduct());
            this.purposeField.setText(transaction.getPurpose());
            this.shopField.setText(transaction.getShop());
            this.valueDateField.setValue(this.getValue().getValueDate());
        }

        dialogPane.setContent(gridPane);

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
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

    private void renameCategoriesInOrder(SerialTreeItem<Category> tree) {
        this.renameCategoriesInOrder(tree, "");
    }

    private void renameCategoriesInOrder(SerialTreeItem<Category> tree, String prefix) {
        int counter = 1;
        if (!tree.isLeaf()) {
            for (TreeItem<Category> item : tree.getChildren()) {
                SerialTreeItem<Category> serialTreeItem = (SerialTreeItem<Category>) item;

                String prefixCopy = prefix + counter + ".";

                serialTreeItem.getValue().setName(prefixCopy + " " + serialTreeItem.getValue().getName());
                serialTreeItem.getValue().setKey(false);

                this.renameCategoriesInOrder(serialTreeItem, prefixCopy);

                counter++;
            }
        }
    }
}