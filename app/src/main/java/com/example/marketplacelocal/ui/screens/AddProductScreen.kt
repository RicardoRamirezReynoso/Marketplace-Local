package com.example.marketplacelocal.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.ui.theme.MarketPlaceLocalTheme
import com.example.marketplacelocal.viewmodel.ProductViewModel

/**
 * `AddProductScreen` permite a los usuarios publicar un nuevo producto en el MarketPlace.
 * Ahora incluye la capacidad de seleccionar una imagen desde el dispositivo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ProductViewModel? = null,
    onBack: () -> Unit = {},
    onProductAdded: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val isLoading by viewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Vista previa de la imagen",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    FilledTonalButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                    ) {
                        Text("Cambiar")
                    }
                } else {
                    OutlinedCard(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("Seleccionar Imagen")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth())
            } else {
                Button(
                    onClick = {
                        val product = Product(
                            name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            imageUrl = "" // Se actualizará en el ViewModel tras subir la imagen
                        )
                        viewModel?.addProduct(product, imageUri) { success ->
                            if (success) onProductAdded()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank() && description.isNotBlank() && price.isNotBlank() && imageUri != null
                ) {
                    Text("Publicar Ahora")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddProductPreview() {
    MarketPlaceLocalTheme {
        AddProductScreen()
    }
}
