package com.example.marketplacelocal.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ProductList : Screen("product_list")
    object AddProduct : Screen("add_product")
    object ProductDetail : Screen("product_detail/{productId}") {
        // Crea la ruta con el ID del producto específico
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Saved : Screen("saved")
}
