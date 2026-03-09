package com.example.marketplacelocal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplacelocal.model.Product
import com.example.marketplacelocal.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `ProductViewModel` gestiona la lógica de negocio relacionada con los productos.
 * Se encarga de cargar la lista de productos desde el repositorio y exponerla a la UI.
 */
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
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
     * `addProduct` guarda un nuevo producto en el repositorio y notifica el resultado.
     * @param product El objeto producto a guardar.
     * @param onResult Callback que devuelve true si se guardó con éxito.
     */
    fun addProduct(product: Product, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addProduct(product)
            _isLoading.value = false
            onResult(result.isSuccess)
        }
    }
}
