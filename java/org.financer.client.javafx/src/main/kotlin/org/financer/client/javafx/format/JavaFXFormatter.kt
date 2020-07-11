package org.financer.client.javafx.format

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.text.TextAlignment
import org.financer.client.domain.model.user.User
import org.financer.client.format.FormatterImpl
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.SettingPair
import java.util.*

class JavaFXFormatter : FormatterImpl {
    constructor(localStorage: LocalStorage?) : super(localStorage!!) {}
    constructor(user: User?) : super(user) {}

    fun format(amountLabel: Label, amount: Amount): Label {
        amountLabel.text = format(amount)
        amountLabel.textAlignment = TextAlignment.RIGHT
        amountLabel.alignment = Pos.CENTER_RIGHT
        amountLabel.styleClass.add(if (amount.amount < 0) "neg-amount" else "pos-amount")
        return amountLabel
    }

    fun formatChangeLabel(label: Label, amount: Double): Label {
        if (!java.lang.Double.isNaN(amount) && java.lang.Double.isFinite(amount)) {
            label.text = String.format((user!!.getValueOrDefault<Any>(SettingPair.Property.LANGUAGE) as Locale), "%.1f", amount) + "%"
        } else {
            label.text = "---"
        }
        label.textAlignment = TextAlignment.RIGHT
        label.alignment = Pos.CENTER_RIGHT
        label.styleClass.add(if (amount < 0) "neg-amount" else "pos-amount")
        return label
    }
}