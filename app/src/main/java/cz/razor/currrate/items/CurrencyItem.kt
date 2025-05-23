package cz.razor.currrate.items

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.razor.currrate.R
import cz.razor.currrate.consts.Routes
import cz.razor.currrate.data.CurrencyInfo
import cz.razor.currrate.data.CurrencyRate
import cz.razor.currrate.viewmodels.CurrencyListViewModel
import cz.razor.currrate.dialogs.DeleteCurrencyConfirmationDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun CurrencyItem(navController: NavController,
                 currencyRate: CurrencyRate,
                 currencyInfo: CurrencyInfo,
                 viewModel: CurrencyListViewModel = koinViewModel()) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(
                    Routes.currencyDetail(
                        currencyRate.toCurrency,
                        currencyRate.date
                    )
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = currencyInfo.name, fontWeight = FontWeight.Bold)
            Text(text = "From: 1 ${currencyRate.baseCurrency}")
            Text(text = "Rate: ${currencyRate.rate} ${currencyRate.toCurrency}")
        }
        IconButton(onClick = {  navController.navigate(Routes.currencyDetail(currencyRate.toCurrency, currencyRate.date)) }) {
            Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.detail))
        }
        IconButton(onClick = {
            if(!currencyInfo.isToCurrencyFavourite){
                viewModel.addFavoriteCurrency(currencyInfo)
            }else{
                showDeleteConfirmDialog = true
            }
        }) {
            if (currencyInfo.isToCurrencyFavourite) {
                Icon(Icons.Filled.Favorite, contentDescription = stringResource(R.string.remove_from_favorites))
            } else {
                Icon(Icons.Filled.FavoriteBorder, contentDescription = stringResource(R.string.add_to_favorites))
            }
        }
    }

    if(showDeleteConfirmDialog){
        DeleteCurrencyConfirmationDialog(currencyInfo.code, onConfirmDelete = {
            viewModel.removeFavoriteCurrency(currencyInfo)
            showDeleteConfirmDialog = false
        }, onDismiss = {
            showDeleteConfirmDialog = false
            Toast.makeText(context, context.getString(R.string.user_canceled_the_deletion), Toast.LENGTH_SHORT).show();
        })
    }
}