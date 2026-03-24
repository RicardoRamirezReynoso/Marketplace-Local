package com.example.marketplacelocal.repository

import com.example.marketplacelocal.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun addProduct(product: Product): Result<Unit>
    
    // Favoritos
    fun getFavoriteProducts(userId: String): Flow<List<Product>>
    suspend fun toggleFavorite(userId: String, product: Product): Result<Unit>
    fun isFavorite(userId: String, productId: String): Flow<Boolean>
}
