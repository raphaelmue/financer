package de.raphaelmuesseler.financer.client.app.ui.components;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.time.LocalDate;

import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.format.Formatter;

public class DatePicker extends TextView {

    private LocalDate value = LocalDate.now();
    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), this.getContext());

    public DatePicker(Context context) {
        super(context);
        init();
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        new Thread(() -> this.setOnClickListener(v -> new DatePickerDialog(
                this.getContext(),
                (view, year, monthOfYear, dayOfMonth)
                        -> this.setText(formatter.formatDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth))),
                this.getValue().getYear(),
                this.getValue().getMonthValue() - 1,
                this.getValue().getDayOfMonth()).show()));
    }

    public void setValue(LocalDate value) {
        this.value = value;
        this.setText(formatter.formatDate(this.value));
    }

    public LocalDate getValue() {
        return value;
    }
}
