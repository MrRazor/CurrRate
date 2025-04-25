package cz.razor.currrate.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.razor.currrate.api.ApiResult
import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.viewmodels.CurrencyGraphViewModel
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun CurrencyGraphScreen(
    to: String,
    date: LocalDate,
    viewModel: CurrencyGraphViewModel = koinViewModel()
) {
    val currencyListResult by viewModel.currencyList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCurrencyList(to, date)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (currencyListResult) {
            is ApiResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is ApiResult.Success -> {
                val currencyRates = (currencyListResult as ApiResult.Success<List<CurrencyRate>>).data
                    .sortedBy { it.date }

                val rateValues = currencyRates.map { it.rate }
                val labels = currencyRates.map {"${it.date} - ${it.rate} ${it.toCurrency}"}

                val dataSet = rateValues.toChartDataSet(title = "Currency Rate", labels = labels)

                Text(
                    text = "Rate: ${currencyRates.first().baseCurrency} → ${currencyRates.first().toCurrency}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LineChart(dataSet)
            }

            is ApiResult.Error -> {
                val message = (currencyListResult as ApiResult.Error).message
                Text(text = "Error: $message", color = Color.Red)
            }
        }
    }
}