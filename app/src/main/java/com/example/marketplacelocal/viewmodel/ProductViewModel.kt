package com.example.marketplacelocal.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.repository.ProductRepository
import com.example.marketplacelocal.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * `ProductViewModel` gestiona la lógica de negocio relacionada con los productos.
 */
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    // Estado interno para la lista de productos
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    // Estado expuesto para que la UI observe los cambios en la lista
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Estado para indicar si se están cargando datos
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * `loadProducts` inicia la recolección del flujo de productos desde el repositorio.
     * Actualiza el estado de `_products` cada vez que hay cambios en la base de datos.
     */
    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProducts().collect { productList ->
                _products.value = productList
                _isLoading.value = false
            }
        }
    }

    /**
     * `addProduct` sube la imagen (si existe) y luego guarda el producto en Firestore.
     */
    fun addProduct(product: Product, imageUri: Uri?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            
            var finalProduct = product
            
            if (imageUri != null) {
                val path = "products/${UUID.randomUUID()}.jpg"
                val uploadResult = storageRepository.uploadImage(imageUri, path)
                if (uploadResult.isSuccess) {
                    finalProduct = product.copy(imageUrl = uploadResult.getOrThrow())
                } else {
                    _isLoading.value = false
                    onResult(false)
                    return@launch
                }
            }
            
            val result = repository.addProduct(finalProduct)
            _isLoading.value = false
            onResult(result.isSuccess)
        }
    }
}
