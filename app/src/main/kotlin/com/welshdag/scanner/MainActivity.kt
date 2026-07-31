package com.welshdag.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.welshdag.scanner.ui.screens.HomeScreen
import com.welshdag.scanner.ui.screens.WalletConnectScreen
import com.welshdag.scanner.ui.screens.BalanceScreen
import com.welshdag.scanner.ui.theme.WelshDagTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelshDagTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController = navController)
                    }
                    composable("wallet_connect") {
                        WalletConnectScreen(navController = navController)
                    }
                    composable("balance/{address}") { backStackEntry ->
                        val address = backStackEntry.arguments?.getString("address") ?: ""
                        BalanceScreen(address = address, navController = navController)
                    }
                }
            }
        }
    }
}
