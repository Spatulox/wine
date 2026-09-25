package com.spatulox.wine.ui.screens.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale

private val PRICE_INPUT_REGEX = Regex("""\d{0,7}([.,]\d{0,2})?""")

// The text is kept as typed (the value is only parsed on save), so "12," or "" stay editable
@Composable
fun PriceField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { text ->
            if (PRICE_INPUT_REGEX.matches(text)) {
                onValueChange(text)
            }
        },
        label = { Text("Prix unitaire") },
        suffix = { Text("€") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier
    )
}

fun parsePrice(text: String): Float? = text.replace(',', '.').toFloatOrNull()

fun formatPriceInput(price: Float?): String =
    price?.let { String.format(Locale.FRANCE, "%.2f", it) } ?: ""

fun formatPrice(price: Float): String =
    NumberFormat.getCurrencyInstance(Locale.FRANCE).format(price)
