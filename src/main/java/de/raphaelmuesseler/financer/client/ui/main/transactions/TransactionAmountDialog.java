package de.raphaelmuesseler.financer.client.ui.main.transactions;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.components.DoubleField;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TransactionAmountDialog extends FinancerDialog<TransactionAmount> {
    private JFXDatePicker valueDateField;
    private DoubleField amountField;

    public TransactionAmountDialog(TransactionAmount value) {
        super(value);
        this.setHeaderText(I18N.get("transactionAmounts"));

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        this.prepareDialogContent();
    }

    @Override
    protected Node setDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(120);
        gridPane.setVgap(10);

        gridPane.add(new Label(I18N.get("valueDate")), 0, 0);
        this.valueDateField = new JFXDatePicker();
        gridPane.add(this.valueDateField, 1, 0);

        gridPane.add(new Label(I18N.get("amount")), 0, 1);
        this.amountField = new DoubleField();
        gridPane.add(this.amountField, 1, 1);

        return gridPane;
    }

    @Override
    protected void prepareDialogContent() {
        if (this.getValue() != null) {
            this.valueDateField.setValue(this.getValue().getValueDate());
            this.amountField.setText(Double.toString(this.getValue().getAmount()));
        }
    }

    @Override
    protected TransactionAmount onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new TransactionAmount(-1, Double.valueOf(this.amountField.getText()),
                    this.valueDateField.getValue()));
        } else {
            this.getValue().setValueDate(this.valueDateField.getValue());
            this.getValue().setAmount(Double.valueOf(this.amountField.getText()));
        }
        return super.onConfirm();
    }
}
