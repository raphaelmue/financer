package org.financer.client.javafx.components

import com.jfoenix.controls.JFXTextField
import javafx.scene.control.TextFormatter
import javafx.util.StringConverter
import java.util.function.UnaryOperator
import java.util.regex.Pattern

class IntegerField : JFXTextField() {

    var value: Int
        get() = text.toInt()
        set(value) {
            text = Integer.toString(value)
        }

    init {
        val validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?")
        val filter = label@ UnaryOperator<TextFormatter.Change> { c: TextFormatter.Change ->
            val text = c.controlNewText
            if (validEditingState.matcher(text).matches()) {
                return@label c
            } else {
                return@label null
            }
        }
        val converter: StringConverter<Int> = object : StringConverter<Int>() {
            override fun fromString(s: String): Int {
                return if (s.isEmpty() || "-" == s || "." == s || "-." == s) {
                    0
                } else {
                    Integer.valueOf(s)
                }
            }

            override fun toString(d: Int): String {
                return d.toString()
            }
        }
        textFormatter = TextFormatter(converter, 0, filter)
    }
}