package com.example.marketplacelocal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.marketplacelocal.ui.screens.*
import com.example.marketplacelocal.viewmodel.AuthViewModel
import com.example.marketplacelocal.viewmodel.ProductViewModel

@Composable
fun MarketPlaceAppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val productViewModel: ProductViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Decide la pantalla de inicio basándose en si el usuario ya está autenticado.
    val startDestination = if (currentUser != null) Screen.ProductList.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de inicio de sesión
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { 
                    navController.navigate(Screen.ProductList.route) { 
                        popUpTo(Screen.Login.route) { inclusive = true } 
                    } 
                }
            )
        }

        // Pantalla de creación de cuenta
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { 
                    navController.navigate(Screen.ProductList.route) { 
                        popUpTo(Screen.Login.route) { inclusive = true } 
                    } 
                }
            )
        }

        // Pantalla principal con el catálogo de productos
        composable(Screen.ProductList.route) {
            ProductListScreen(
                viewModel = productViewModel,
                authViewModel = authViewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onAddProductClick = {
                    navController.navigate(Screen.AddProduct.route)
                },
                onNavigateToSaved = {
                    navController.navigate(Screen.Saved.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Pantalla de Favoritos
        composable(Screen.Saved.route) {
            SavedScreen(
                productViewModel = productViewModel,
                authViewModel = authViewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.ProductList.route) {
                        launchSingleTop = true
                    }
                },
                onAddProductClick = {
                    navController.navigate(Screen.AddProduct.route)
                }
            )
        }

        // Pantalla para agregar un nuevo producto
        composable(Screen.AddProduct.route) {
            AddProductScreen(
                productViewModel = productViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onProductAdded = { navController.popBackStack() }
            )
        }

        // Pantalla con la información detallada del producto
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                viewModel = productViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.ProductList.route) { inclusive = true }
                    }
                },
                onNavigateToSaved = {
                    navController.navigate(Screen.Saved.route) {
                        launchSingleTop = true
                    }
                },
                onAddProductClick = {
                    navController.navigate(Screen.AddProduct.route)
                }
            )
        }
    }
}
