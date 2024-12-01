package com.mkl.inscanner
import HomePage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mkl.inscanner.Pages.CreateScreen
import com.mkl.inscanner.Pages.IntroPage
import com.mkl.inscanner.Pages.QRCodeScannerScreen
import com.mkl.inscanner.Pages.SplashScreen

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomePage(navController) }
            composable("getstarted") { IntroPage() }
            composable("QRCodeScannerScreen") { QRCodeScannerScreen() }
            composable("createScreen") {CreateScreen(navController)}
        }
    }
}

