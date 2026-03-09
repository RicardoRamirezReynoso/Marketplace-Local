package com.example.marketplacelocal.repository

import com.example.marketplacelocal.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * `FirebaseProductRepository` implementa la interfaz `ProductRepository` utilizando Firebase Firestore.
 */
class FirebaseProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    /**
     * `getProducts` escucha cambios en tiempo real en la colección "products" de Firestore.
     * Retorna un `Flow` que emite la lista actualizada de productos.
     */
    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        val subscription = firestore.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(products)
            }
        // Se asegura de remover el listener cuando el Flow se cierra
        awaitClose { subscription.remove() }
    }

    /**
     * `getProductById` obtiene un único producto por su ID de documento.
     */
    override suspend fun getProductById(id: String): Product? {
        return try {
            firestore.collection("products").document(id).get().await()
                .toObject(Product::class.java)?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * `addProduct` guarda un nuevo objeto `Product` en la colección "products".
     */
    override suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            firestore.collection("products").add(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
