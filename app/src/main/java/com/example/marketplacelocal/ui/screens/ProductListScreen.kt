package com.example.marketplacelocal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.ui.theme.MarketPlaceLocalTheme
import com.example.marketplacelocal.viewmodel.ProductViewModel

/**
 * `ProductListScreen` muestra una lista de todos los productos disponibles.
 * Incluye un botón flotante (FAB) para agregar nuevos productos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel? = null,
    productsList: List<Product> = emptyList(),
    isLoadingState: Boolean = false,
    onProductClick: (String) -> Unit = {},
    onAddProductClick: () -> Unit = {}
) {
    // Si tenemos un viewModel, usamos sus estados. Si no (como en Preview), usamos los parámetros.
    val products by viewModel?.products?.collectAsState() ?: remember { mutableStateOf(productsList) }
    val isLoading by viewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(isLoadingState) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MarketPlace Local") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

/**
 * `ProductItem` representa una tarjeta individual para cada producto en la lista.
 */
@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    MarketPlaceLocalTheme {
        ProductListScreen(
            productsList = listOf(
                Product("1", "Smartphone", "Un gran teléfono", 999.99, ""),
                Product("2", "Laptop", "Potente para trabajar", 1499.99, "")
            )
        )
    }
}
