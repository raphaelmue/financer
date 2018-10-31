package de.raphaelmuesseler.financer.client.javafx.main.transactions;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Comparator;

class TransactionDialog extends FinancerDialog<Transaction> {

    private DoubleField amountField;
    private ComboBox<CategoryTree> categoryComboBox;
    private TextField productField;
    private TextField purposeField;
    private TextField shopField;
    private JFXDatePicker valueDateField;
    private BaseCategory categories;

    TransactionDialog(Transaction transaction, BaseCategory categories) {
        super(transaction);

        this.categories = categories;

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
        categories.traverse(treeItem -> {
            if (!treeItem.isRoot() && (((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_EXPENSES ||
                    ((CategoryTree) treeItem).getCategoryClass() == BaseCategory.CategoryClass.VARIABLE_REVENUE)) {
                categoryComboBox.getItems().add((CategoryTree) treeItem);
            }
        });
        this.categoryComboBox.setCellFactory(param -> new ListCell<CategoryTree>() {
            @Override
            protected void updateItem(CategoryTree item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(Formatter.formatCategoryName(item.getValue()));
                } else {
                    setText(null);
                }
            }
        });

        if (this.getValue() != null) {
            this.amountField.setText(String.valueOf(this.getValue().getAmount()));
            this.categoryComboBox.getSelectionModel().select((CategoryTree) TreeUtil.getByValue(this.categories,
                    this.getValue().getCategoryTree(), Comparator.comparingInt(Category::getId)));
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
            this.getValue().setCategoryTree(this.categoryComboBox.getSelectionModel().getSelectedItem());
            this.getValue().setProduct(this.productField.getText());
            this.getValue().setPurpose(this.purposeField.getText());
            this.getValue().setValueDate(this.valueDateField.getValue());
            this.getValue().setShop(this.shopField.getText());
        }

        return super.onConfirm();
    }
}