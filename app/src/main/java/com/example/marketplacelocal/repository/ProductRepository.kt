package com.example.marketplacelocal.repository

import com.example.marketplacelocal.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    // Nueva función para guardar productos
    suspend fun addProduct(product: Product): Result<Unit>
}
