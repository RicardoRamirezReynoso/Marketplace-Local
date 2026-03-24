package com.example.marketplacelocal.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.repository.ProductRepository
import com.example.marketplacelocal.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    // Flujo de productos
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    
    // Estado para la búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Flujo de productos filtrados por búsqueda
    val products: StateFlow<List<Product>> = combine(_products, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true) 
            }
        }
    }.catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo de productos favoritos
    private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favoriteProducts: StateFlow<List<Product>> = _favoriteProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado para manejar errores de favoritos
    private val _favoriteError = MutableStateFlow<String?>(null)
    val favoriteError: StateFlow<String?> = _favoriteError.asStateFlow()

    init {
        loadProducts()
    }
    // Carga los productos desde el repositorio
    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProducts().collect { productList ->
                _products.value = productList
                _isLoading.value = false
            }
        }
    }

    // Actualiza la búsqueda
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // Carga los productos favoritos del usuario
    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            repository.getFavoriteProducts(userId)
                .catch { e -> _favoriteError.value = e.message }
                .collectLatest { favorites -> _favoriteProducts.value = favorites }
        }
    }
   // Limpia el mensaje de error de favoritos
    fun clearFavoriteError() { _favoriteError.value = null }

    // Cambia el estado favorito de un producto
    fun toggleFavorite(userId: String, product: Product) {
        viewModelScope.launch { repository.toggleFavorite(userId, product) }
    }

    // Verifica si un producto es favorito
    fun isFavorite(userId: String, productId: String) = repository.isFavorite(userId, productId)

    // Subir un producto con imágenes y obtener su URL
    fun addProduct(product: Product, imageUris: List<Uri>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val uploadedUrls = mutableListOf<String>()
            var success = true
            for (uri in imageUris) {
                val path = "products/${UUID.randomUUID()}.jpg"
                val uploadResult = storageRepository.uploadImage(uri, path)
                if (uploadResult.isSuccess) uploadedUrls.add(uploadResult.getOrThrow())
                else { success = false; break }
            }
            if (success) {
                val result = repository.addProduct(product.copy(imageUrls = uploadedUrls))
                _isLoading.value = false
                onResult(result.isSuccess)
            } else {
                _isLoading.value = false
                onResult(false)
            }
        }
    }
}
