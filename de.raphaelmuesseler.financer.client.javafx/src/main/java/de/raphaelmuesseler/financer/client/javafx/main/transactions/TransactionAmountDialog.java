package de.raphaelmuesseler.financer.client.javafx.main.transactions;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DatePicker;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;

public class TransactionAmountDialog extends FinancerDialog<TransactionAmount> {
    private DatePicker valueDateField;
    private DoubleField amountField;
    private List<TransactionAmount> transactionAmounts;

    public TransactionAmountDialog(TransactionAmount value, List<TransactionAmount> transactionAmounts) {
        super(value);
        this.transactionAmounts = transactionAmounts;

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
        this.valueDateField = new DatePicker(new JavaFXFormatter(LocalStorageImpl.getInstance()));
        this.valueDateField.setValue(LocalDate.now());
        this.valueDateField.setId("transactionAmountValueDatePicker");
        gridPane.add(this.valueDateField, 1, 0);

        gridPane.add(new Label(I18N.get("amount")), 0, 1);
        this.amountField = new DoubleField();
        this.amountField.setId("transactionAmountTextField");
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
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.getValue() == null) {
            for (TransactionAmount transactionAmount : this.transactionAmounts) {
                if (transactionAmount.getValueDate().getMonth() == this.valueDateField.getValue().getMonth() &&
                        transactionAmount.getValueDate().getYear() == this.valueDateField.getValue().getYear()) {
                    this.setErrorMessage(I18N.get("errTransactionAmountExists"));
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    protected TransactionAmount onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new TransactionAmount(0, Double.valueOf(this.amountField.getText()),
                    this.valueDateField.getValue()));
        } else {
            this.getValue().setValueDate(this.valueDateField.getValue());
            this.getValue().setAmount(Double.valueOf(this.amountField.getText()));
        }
        return super.onConfirm();
    }
}
