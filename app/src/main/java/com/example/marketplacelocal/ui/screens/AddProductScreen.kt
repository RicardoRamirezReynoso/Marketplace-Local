package com.example.marketplacelocal.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.marketplacelocal.R
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.ui.theme.*
import com.example.marketplacelocal.viewmodel.AuthViewModel
import com.example.marketplacelocal.viewmodel.ProductViewModel

@Composable
fun AddProductScreen(
    productViewModel: ProductViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit = {},
    onProductAdded: () -> Unit = {}
) {
    // Estado de carga y el usuario actual desde los ViewModels
    val isLoading by productViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    AddProductContent(
        isLoading = isLoading,
        onBack = onBack,
        onPublishClick = { name, price, description, location, condition, uris ->
            val product = Product(
                name = name,
                description = description,
                price = price.toDoubleOrNull() ?: 0.0,
                deliveryLocation = location,
                condition = condition,
                sellerEmail = currentUser?.email ?: ""
            )
            // Agregar producto en el ViewModel
            productViewModel.addProduct(product, uris) { success ->
                if (success) onProductAdded()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductContent(
    isLoading: Boolean,
    onBack: () -> Unit,
    onPublishClick: (String, String, String, String, String, List<Uri>) -> Unit
) {
    // Estados internos para los campos del formulario
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deliveryLocation by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Nuevo") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Lanzador para seleccionar múltiples imágenes (límite de 5)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val currentTotal = selectedImageUris.size
        val availableSlots = 5 - currentTotal
        if (availableSlots > 0) {
            selectedImageUris = selectedImageUris + uris.take(availableSlots)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.new_post),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                // Botón de acción principal: Publicar
                Button(
                    onClick = { 
                        onPublishClick(name, price, description, deliveryLocation, condition, selectedImageUris) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    // Se habilita con los datos mínimos y no se está cargando
                    enabled = name.isNotBlank() && price.isNotBlank() && deliveryLocation.isNotEmpty() && selectedImageUris.isNotEmpty() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                stringResource(R.string.publish_button),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.TrendingFlat, contentDescription = null)
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Contenedor para la carga y previsualización de fotos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedImageUris.isEmpty()) {
                        // Estado inicial sin fotos
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(LogoBackground, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(32.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(stringResource(R.string.add_photos), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextDark))
                        Text(stringResource(R.string.add_photos_subtitle), style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { launcher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.upload_photos_button))
                        }
                    } else {
                        // Carrusel horizontal de fotos seleccionadas con opción de eliminar
                        LazyRow(
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(selectedImageUris) { uri ->
                                Box(modifier = Modifier.size(120.dp)) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImageUris = selectedImageUris - uri },
                                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            // Permite añadir más fotos si el total es menor a 5
                            if (selectedImageUris.size < 5) {
                                item {
                                    Box(
                                        modifier = Modifier.size(120.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).clickable { launcher.launch("image/*") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Campos del formulario
            FormLabelField(
                label = stringResource(R.string.product_name_label),
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.product_name_placeholder),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            FormLabelField(
                label = stringResource(R.string.price_label),
                value = price,
                onValueChange = { price = it },
                placeholder = stringResource(R.string.price_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            FormLabelField(
                label = stringResource(R.string.description_label),
                value = description,
                onValueChange = { description = it },
                placeholder = stringResource(R.string.description_placeholder),
                modifier = Modifier.height(120.dp),
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            FormLabelField(
                label = stringResource(R.string.delivery_location_label),
                value = deliveryLocation,
                onValueChange = { deliveryLocation = it },
                placeholder = stringResource(R.string.delivery_location_placeholder),
                modifier = Modifier.height(100.dp),
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            // Selector para el estado del artículo
            Column {
                Text(
                    stringResource(R.string.product_condition_label),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = TextDark),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConditionButton(
                        text = stringResource(R.string.condition_new),
                        isSelected = condition == "Nuevo",
                        onClick = { condition = "Nuevo" },
                        modifier = Modifier.weight(1f)
                    )
                    ConditionButton(
                        text = stringResource(R.string.condition_used),
                        isSelected = condition == "Usado",
                        onClick = { condition = "Usado" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
        }
    }
}

// Componente reutilizable para los campos de texto
@Composable
fun FormLabelField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = TextDark),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}

// Selector para el estado del producto
@Composable
fun ConditionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) LogoBackground else Color(0xFFF7F7F7),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, PrimaryOrange) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) PrimaryOrange else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductPreview() {
    MarketPlaceLocalTheme {
        AddProductContent(
            isLoading = false,
            onBack = {},
            onPublishClick = { _, _, _, _, _, _ -> }
        )
    }
}
