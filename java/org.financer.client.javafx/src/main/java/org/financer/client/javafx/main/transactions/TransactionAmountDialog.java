package org.financer.client.javafx.main.transactions;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DatePicker;
import org.financer.client.javafx.components.DoubleField;
import org.financer.client.javafx.dialogs.FinancerDialog;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import java.time.LocalDate;
import java.util.List;

public class TransactionAmountDialog extends FinancerDialog<FixedTransactionAmount> {
    private DatePicker valueDateField;
    private DoubleField amountField;
    private final List<FixedTransactionAmount> transactionAmounts;

    public TransactionAmountDialog(FixedTransactionAmount value, List<FixedTransactionAmount> transactionAmounts) {
        super(value);
        this.transactionAmounts = transactionAmounts;

        this.prepareDialogContent();
        this.setDialogTitle(I18N.get("transactionAmounts"));
    }

    @Override
    protected Region getDialogContent() {
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
            this.valueDateField.setValue(this.getValue().getValueDate().getDate());
            this.amountField.setText(Double.toString(this.getValue().getAmount().getAmount()));
        }
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.getValue() == null) {
            for (FixedTransactionAmount transactionAmount : this.transactionAmounts) {
                if (transactionAmount.getValueDate().isInSameMonth(new ValueDate(valueDateField.getValue()))) {
                    this.setErrorMessage(I18N.get("errTransactionAmountExists"));
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    protected void onConfirm() {
        if (this.getValue() == null) {
            this.setValue(new FixedTransactionAmount()
                    .setId(0)
                    .setAmount(new Amount(Double.parseDouble(this.amountField.getText())))
                    .setValueDate(new ValueDate(this.valueDateField.getValue())));
        } else {
            getValue().setValueDate(new ValueDate(this.valueDateField.getValue()));
            getValue().setAmount(new Amount(Double.parseDouble(this.amountField.getText())));
        }
        super.onConfirm();
    }
}
