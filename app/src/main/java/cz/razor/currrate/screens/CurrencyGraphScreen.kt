package cz.razor.currrate.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.razor.currrate.viewmodels.CurrencyDetailViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate


@Composable
fun CurrencyGraphScreen(
    to: String,
    date: LocalDate,
    viewModel: CurrencyDetailViewModel = koinViewModel(),
) {

    LaunchedEffect(Unit) {

    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

    }
}
