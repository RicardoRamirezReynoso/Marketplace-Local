package com.example.marketplacelocal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.ui.theme.MarketPlaceLocalTheme
import com.example.marketplacelocal.viewmodel.ProductViewModel

/**
 * `ProductDetailScreen` muestra la información completa de un producto seleccionado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductViewModel? = null,
    productPreview: Product? = null,
    onBack: () -> Unit = {}
) {
    val productState = remember { mutableStateOf<Product?>(productPreview) }

    // Buscamos el producto en el ViewModel si está disponible
    LaunchedEffect(productId) {
        if (viewModel != null) {
            productState.value = viewModel.products.value.find { it.id == productId }
        }
    }

    val product = productState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text("Producto no encontrado", modifier = Modifier.padding(16.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = product.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Categoría: ${product.category}", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = product.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    MarketPlaceLocalTheme {
        ProductDetailScreen(
            productId = "1",
            productPreview = Product(
                id = "1",
                name = "Smartphone de Prueba",
                description = "Esta es una descripción detallada de un producto de prueba para la vista previa.",
                price = 599.99,
                category = "Electrónica"
            )
        )
    }
}
