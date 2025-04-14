package cz.razor.currrate.items


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.razor.currrate.consts.Routes
import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyRate

@Composable
fun CurrencyItem(navController: NavController, currencyRate: CurrencyRate, currencyInfo: CurrencyInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(Routes.currencyDetail(currencyRate.baseCurrency, currencyRate.toCurrency, currencyRate.date))
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = currencyInfo.name, fontWeight = FontWeight.Bold)
            Text(text = "From: 1 ${currencyRate.baseCurrency}")
            Text(text = "Rate: ${currencyRate.rate} ${currencyRate.toCurrency}")
        }
    }
}