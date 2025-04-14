package cz.uhk.fim.cryptoapp.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cz.razor.currrate.R

@Composable
fun DeleteCurrencyConfirmationDialog(
    currencyCode: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var isSwitchChecked by remember { mutableStateOf(false) }
    var enteredCryptoName by remember { mutableStateOf(TextFieldValue("")) }
    var isNameCorrect by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = stringResource(R.string.delete_confirmation_title))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.delete_confirmation_message, currencyCode))

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = enteredCryptoName,
                    onValueChange = {
                        enteredCryptoName = it
                        isNameCorrect = it.text.equals(currencyCode, ignoreCase = true)
                    },
                    label = { Text(stringResource(R.string.entry_currency_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Switch(
                    checked = isSwitchChecked,
                    onCheckedChange = { isSwitchChecked = it },
                )
                Text(
                    text = stringResource(R.string.i_know_what_i_am_doing_label),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete()
                },
                enabled = isSwitchChecked && isNameCorrect
            ) {
                Text(stringResource(R.string.dialog_remove))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}