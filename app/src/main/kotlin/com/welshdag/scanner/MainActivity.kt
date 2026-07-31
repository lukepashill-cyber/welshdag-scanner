package com.welshdag.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.welshdag.scanner.ui.screens.AddressScreen
import com.welshdag.scanner.ui.screens.CrashScreen
import com.welshdag.scanner.ui.screens.ExplorerScreen
import com.welshdag.scanner.ui.screens.WatchlistScreen
import com.welshdag.scanner.ui.theme.WelshDagTheme
import com.welshdag.scanner.util.CrashReporter
import dagger.hilt.android.AndroidEntryPoint

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val TABS = listOf(
    Tab("explorer", "Explorer", Icons.Filled.Explore),
    Tab("wallet", "My addresses", Icons.Filled.AccountBalanceWallet)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelshDagTheme {
                val context = LocalContext.current
                var pendingCrash by remember { mutableStateOf(CrashReporter.lastCrash(context)) }

                val crash = pendingCrash
                if (crash != null) {
                    CrashScreen(
                        trace = crash,
                        onDismiss = {
                            CrashReporter.clear(context)
                            pendingCrash = null
                        }
                    )
                } else {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
private fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        // Kept unconditional: swapping the bottomBar slot in and out during a
        // navigation transition is a needless source of layout churn.
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TABS.forEach { tab ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "explorer",
            modifier = Modifier.padding(padding)
        ) {
            composable("explorer") {
                ExplorerScreen(navController = navController)
            }
            composable("wallet") {
                WatchlistScreen(navController = navController)
            }
            composable("address/{address}") { entry ->
                AddressScreen(
                    address = entry.arguments?.getString("address").orEmpty(),
                    navController = navController
                )
            }
        }
    }
}
