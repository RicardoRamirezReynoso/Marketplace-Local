package com.example.marketplacelocal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.marketplacelocal.R
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.ui.theme.*
import com.example.marketplacelocal.viewmodel.AuthViewModel
import com.example.marketplacelocal.viewmodel.ProductViewModel
import java.util.Locale


@Composable
fun SavedScreen(
    productViewModel: ProductViewModel,
    authViewModel: AuthViewModel,
    onProductClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onAddProductClick: () -> Unit
) {

    val currentUser by authViewModel.currentUser.collectAsState()
    val favoriteProducts by productViewModel.favoriteProducts.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val favoriteError by productViewModel.favoriteError.collectAsState()
    
    // Estado para controlar el Snackbar (avisos flotantes)
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar favoritos al iniciar
    LaunchedEffect(currentUser) {
        currentUser?.let { productViewModel.loadFavorites(it.uid) }
    }

    // Mostrar Snackbar si hay un error (ej. límite de 30 alcanzado)
    LaunchedEffect(favoriteError) {
        favoriteError?.let {
            snackbarHostState.showSnackbar(it)
            productViewModel.clearFavoriteError() // Se limpia el error tras mostrarlo
        }
    }

    SavedContent(
        favoriteProducts = favoriteProducts,
        isLoading = isLoading,
        snackbarHostState = snackbarHostState,
        onProductClick = onProductClick,
        onRemoveClick = { product ->
            currentUser?.let { user ->
                productViewModel.toggleFavorite(user.uid, product)
            }
        },
        onNavigateToHome = onNavigateToHome,
        onAddProductClick = onAddProductClick
    )
}


@Composable
fun SavedContent(
    favoriteProducts: List<Product>,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onProductClick: (String) -> Unit,
    onRemoveClick: (Product) -> Unit,
    onNavigateToHome: () -> Unit,
    onAddProductClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Host para los avisos
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Text(
                    text = stringResource(R.string.my_favorites_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        },
        bottomBar = {
            Box(contentAlignment = Alignment.Center) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(80.dp)
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToHome,
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    
                    Spacer(Modifier.weight(1f))

                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Saved Seleccionado */ },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_saved)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryOrange,
                            selectedTextColor = PrimaryOrange,
                            indicatorColor = LogoBackground
                        )
                    )
                }

                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = PrimaryOrange,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .offset(y = (-20).dp)
                        .size(60.dp),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Sell", modifier = Modifier.size(32.dp))
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else if (favoriteProducts.isEmpty()) {
            // Estado vacío cuando no hay favoritos guardados
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_favorites_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            // Lista vertical de productos favoritos
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteProducts) { product ->
                    FavoriteProductItem(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onRemove = { onRemoveClick(product) }
                    )
                }
            }
        }
    }
}

// Componente reutilizable para los ítems de la lista de favoritos
@Composable
fun FavoriteProductItem(
    product: Product,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen principal del producto
        AsyncImage(
            model = product.imageUrls.firstOrNull(),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    ),
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "${product.deliveryLocation} - ${product.condition}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio formateado
                Text(
                    text = "$${String.format(Locale.getDefault(), "%.2f", product.price)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                )

                // Botón para eliminar de la lista
                Row(
                    modifier = Modifier.clickable { onRemove() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.remove_button),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedPreview() {
    MarketPlaceLocalTheme {
        SavedContent(
            favoriteProducts = listOf(
                Product(id = "1", name = "Vintage Camera Bag", price = 45.0, deliveryLocation = "Downtown", imageUrls = listOf("")),
                Product(id = "2", name = "Ceramic Mug Set", price = 28.0, deliveryLocation = "Westside", imageUrls = listOf(""))
            ),
            isLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onProductClick = {},
            onRemoveClick = {},
            onNavigateToHome = {},
            onAddProductClick = {}
        )
    }
}
