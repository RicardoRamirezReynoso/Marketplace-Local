package com.example.marketplacelocal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import java.util.Locale

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToSaved: () -> Unit = {},
    onAddProductClick: () -> Unit = {}
) {
    // Obtenemos la lista de productos y se busca el actual
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }
    
    // Obtenemos el usuario actual para la lógica de favoritos
    val currentUser by authViewModel.currentUser.collectAsState()

    // Verificamos si este producto es favorito del usuario actual
    val isFavFlow = if (currentUser != null && product != null) {
        viewModel.isFavorite(currentUser!!.uid, product.id)
    } else {
        null
    }
    val isFavorite by isFavFlow?.collectAsState(initial = false) ?: remember { mutableStateOf(false) }

    ProductDetailContent(
        product = product,
        isFavorite = isFavorite,
        onFavoriteToggle = {
            currentUser?.let { user ->
                product?.let { p -> viewModel.toggleFavorite(user.uid, p) }
            }
        },
        onBack = onBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToSaved = onNavigateToSaved,
        onAddProductClick = onAddProductClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    product: Product?,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onAddProductClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
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
                
                // Botón central para publicar
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
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Carrusel de Imágenes
                val pagerState = rememberPagerState(pageCount = { product.imageUrls.size })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = product.imageUrls[page],
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Paginación (puntos)
                    if (product.imageUrls.size > 1) {
                        Row(
                            Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(product.imageUrls.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.White.copy(alpha = 0.5f)
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(8.dp)
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        // Botón de Favorito con estilo circular
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .background(LogoBackground, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color.Red else PrimaryOrange
                            )
                        }
                    }

                    Text(
                        text = "$${String.format(Locale.getDefault(), "%.2f", product.price)}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.description_label),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDark)
                    )
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, lineHeight = 22.sp),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.delivery_location_label),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDark)
                    )
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = product.deliveryLocation,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Etiqueta de condición (Nuevo/Usado)
                    Surface(
                        color = LogoBackground,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = product.condition,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(color = PrimaryOrange, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Tarjeta de contacto del vendedor
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LogoBackground.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.email_seller),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = product.sellerEmail.ifEmpty { "Correo no proporcionado" },
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextDark, fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Button(
                                onClick = { /* Proximo desarrollo: Acción de contactar */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        stringResource(R.string.contact_seller),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    MarketPlaceLocalTheme {
        ProductDetailContent(
            product = Product(
                id = "1",
                name = "Handmade Solid Oak Dining Table",
                description = "Beautifully crafted solid oak dining table, handmade in our local workshop.",
                price = 1250.0,
                deliveryLocation = "Colonia Centro, Ciudad de México",
                condition = "Nuevo",
                sellerEmail = "james.miller.woodworks@localmail.com",
                imageUrls = listOf("")
            ),
            isFavorite = true,
            onFavoriteToggle = {},
            onBack = {},
            onNavigateToHome = {},
            onNavigateToSaved = {},
            onAddProductClick = {}
        )
    }
}
