package com.example.marketplacelocal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.marketplacelocal.R
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.ui.theme.*
import com.example.marketplacelocal.viewmodel.AuthViewModel
import com.example.marketplacelocal.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel,
    onProductClick: (String) -> Unit = {},
    onAddProductClick: () -> Unit = {},
    onNavigateToSaved: () -> Unit = {}
) {

    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    ProductListContent(
        products = products,
        isLoading = isLoading,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) }, // Actualiza la búsqueda
        isFavorite = { productId -> 
            viewModel.isFavorite(currentUser?.uid ?: "", productId) 
        },
        onFavoriteToggle = { product ->
            currentUser?.let { user ->
                viewModel.toggleFavorite(user.uid, product)
            }
        },
        onProductClick = onProductClick,
        onAddProductClick = onAddProductClick,
        onNavigateToSaved = onNavigateToSaved
    )
}


@Composable
fun ProductListContent(
    products: List<Product>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isFavorite: (String) -> Flow<Boolean>,
    onFavoriteToggle: (Product) -> Unit,
    onProductClick: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onNavigateToSaved: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(LogoBackground, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icono de logo
                        Image(
                            painter = painterResource(id = R.drawable.marketplace),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                }
                // Campo de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search_placeholder), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = BorderLightTwo,
                        focusedContainerColor = BorderLightTwo,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = PrimaryOrange
                    ),
                    singleLine = true
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
                        selected = true,
                        onClick = { /* Home Seleccionado */ },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryOrange,
                            selectedTextColor = PrimaryOrange,
                            indicatorColor = LogoBackground
                        )
                    )

                    Spacer(Modifier.weight(1f)) // Espacio para el FAB central

                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToSaved,
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_saved)) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }

                // FAB Naranja central para publicar nuevos productos
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
                    Icon(Icons.Default.Add, contentDescription = "Publicar", modifier = Modifier.size(32.dp))
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = stringResource(R.string.fresh_finds),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDark),
                modifier = Modifier.padding(16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            } else if (products.isEmpty() && searchQuery.isNotEmpty()) {
                // Mensaje cuando no hay resultados de búsqueda
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No se encontraron resultados para \"$searchQuery\"", color = Color.Gray)
                }
            } else {
                // Grid de productos en 2 columnas
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        // Verificamos estado de favorito de forma reactiva
                        val isFav by isFavorite(product.id).collectAsState(initial = false)
                        ProductGridItem(
                            product = product,
                            isFavorite = isFav,
                            onFavoriteToggle = { onFavoriteToggle(product) },
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}

// Componente reutilizable para los ítems del grid
@Composable
fun ProductGridItem(
    product: Product,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Badge flotante con el precio
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = "$${product.price.toInt()}",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = product.deliveryLocation,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true, name = "Vista Principal")
@Composable
fun ProductListPreview() {
    MarketPlaceLocalTheme {
        ProductListContent(
            products = listOf(
                Product(id = "1", name = "Vintage Lamp", price = 45.0, deliveryLocation = "Downtown", imageUrls = listOf("")),
                Product(id = "2", name = "Green Sofa", price = 200.0, deliveryLocation = "Mission District", imageUrls = listOf(""))
            ),
            isLoading = false,
            searchQuery = "",
            onSearchQueryChange = {},
            isFavorite = { flowOf(false) },
            onFavoriteToggle = {},
            onProductClick = {},
            onAddProductClick = {},
            onNavigateToSaved = {}
        )
    }
}
