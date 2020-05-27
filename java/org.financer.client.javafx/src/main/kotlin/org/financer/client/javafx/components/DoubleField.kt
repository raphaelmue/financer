package org.financer.client.javafx.components

import com.jfoenix.controls.JFXTextField
import javafx.scene.control.TextFormatter
import javafx.util.StringConverter
import java.util.function.UnaryOperator
import java.util.regex.Pattern

class DoubleField : JFXTextField() {

    var value: Double
        get() = java.lang.Double.valueOf(text)
        set(value) {
            text = java.lang.Double.toString(value)
        }

    init {
        val validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?([[.],][0-9]*)?")
        val filter = label@ UnaryOperator<TextFormatter.Change> { c: TextFormatter.Change ->
            val text = c.controlNewText
            if (validEditingState.matcher(text).matches()) {
                return@label c
            } else {
                return@label null
            }
        }
        val converter: StringConverter<Double> = object : StringConverter<Double>() {
            override fun fromString(s: String): Double {
                return if (s.isEmpty() || "-" == s || "." == s || "-." == s) {
                    0.0
                } else {
                    java.lang.Double.valueOf(s.replace(",", "."))
                }
            }

            override fun toString(d: Double): String {
                return d.toString()
            }
        }
        textFormatter = TextFormatter(converter, 0.0, filter)
    }
}