package cz.razor.currrate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import cz.razor.currrate.consts.BottomNavItem
import cz.razor.currrate.consts.Routes
import cz.razor.currrate.helpers.NotificationSchedulerHelper
import cz.razor.currrate.helpers.PermissionHelper
import cz.razor.currrate.screens.CurrencyDetailScreen
import cz.razor.currrate.screens.CurrencyGraphScreen
import cz.razor.currrate.screens.CurrencyListScreen
import cz.razor.currrate.screens.FavouriteCurrencyListScreen
import cz.razor.currrate.screens.SettingsScreen
import cz.razor.currrate.theme.CurrRateAppTheme
import org.koin.android.ext.android.inject
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrRateAppTheme {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }

        val permissionHelper = PermissionHelper(this)
        permissionHelper.requestNotificationPermission()

        val notificationSchedulerHelper: NotificationSchedulerHelper by inject()
        notificationSchedulerHelper.scheduleNotificationWorker()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this,
                getString(R.string.notification_permission_granted), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,
                getString(R.string.notification_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        BottomNavItem.CurrencyList,
        BottomNavItem.FavouriteCurrency
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.currency_rates_app)) },
                navigationIcon = {
                    if (currentRoute == Routes.CurrencyDetail || currentRoute == Routes.CurrencyGraph || currentRoute == Routes.Settings) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }
                    }
                },
                actions = {
                    if (currentRoute != Routes.Settings) {
                        IconButton(onClick = { navController.navigate(Routes.Settings) }) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if(currentRoute != Routes.CurrencyDetail && currentRoute != Routes.CurrencyGraph && currentRoute != Routes.Settings) {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                navController.navigate(item.screenRoute) {
                                    navController.graph.startDestinationRoute?.let { screenRoute ->
                                        popUpTo(screenRoute) {
                                            saveState =
                                                true
                                        }
                                    }
                                    launchSingleTop =
                                        true
                                    restoreState =
                                        true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Navigation(navController = navController, innerPadding = innerPadding)
    }
}

@Composable
fun Navigation(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Routes.CurrencyList,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.CurrencyList) { CurrencyListScreen(navController) }
        composable(Routes.CurrencyDetail) { navBackStackEntry ->
            val to = navBackStackEntry.arguments?.getString("to")
            val date = navBackStackEntry.arguments?.getString("date")
            if (to != null && date != null) {
                CurrencyDetailScreen(navController, to, LocalDate.parse(date))
            }
        }
        composable(Routes.CurrencyGraph) { navBackStackEntry ->
            val to = navBackStackEntry.arguments?.getString("to")
            val date = navBackStackEntry.arguments?.getString("date")
            if (to != null && date != null) {
                CurrencyGraphScreen(to, LocalDate.parse(date))
            }
        }
        composable(Routes.FavouriteCurrencyList) { FavouriteCurrencyListScreen(navController) }
        composable(Routes.Settings) { SettingsScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CurrRateAppTheme {
        MainScreen(rememberNavController())
    }
}