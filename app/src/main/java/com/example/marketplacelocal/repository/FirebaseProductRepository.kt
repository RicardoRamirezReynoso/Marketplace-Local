package com.example.marketplacelocal.repository

import com.example.marketplacelocal.model.Product
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    //Escucha cambios en tiempo real en la colección "products" de Firestore, retorna un Flow con la lista actualizada de productos
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
        // Remueve el listener cuando el Flow se cierra
        awaitClose { subscription.remove() }
    }

    // Obtiene un producto individual por su ID
    override suspend fun getProductById(id: String): Product? {
        return try {
            firestore.collection("products").document(id).get().await()
                .toObject(Product::class.java)?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    // Guarda un nuevo producto en Firestore
    override suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            firestore.collection("products").add(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Funciones para favoritos
    override fun getFavoriteProducts(userId: String): Flow<List<Product>> = callbackFlow {
        if (userId.isBlank()) { // Si el usuario no está autenticado emite una lista vacía
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val subscription = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error -> //Listener en tiempo real al documento del usuario
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                // Extrae el array de IDs de productos favoritos
                val favoriteIds = snapshot?.get("favorites") as? List<String> ?: emptyList()
                
                if (favoriteIds.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Límite de Firestore para 'whereIn'
                val limit = 30
                val idsToQuery = favoriteIds.take(limit) // Toma solo los primeros 30

                // Consulta de los detalles de los productos
                launch {
                    try {
                        val querySnapshot = firestore.collection("products")
                            .whereIn(FieldPath.documentId(), idsToQuery)
                            .get()
                            .await() 

                        val products = querySnapshot.documents.mapNotNull { doc ->
                            doc.toObject(Product::class.java)?.copy(id = doc.id)
                        }
                        
                        trySend(products) // Emite la lista al Flow

                        // Si supera el límite, se manda el mensaje de error
                        if (favoriteIds.size > limit) {
                            close(Exception("Has alcanzado el máximo de favoritos"))
                        }

                    } catch (e: Exception) {
                        close(Exception("Error al obtener los productos favoritos", e))
                    }
                }
            }
        awaitClose { subscription.remove() }
    }

    // Alterna el estado de un producto entre favorito y no favorito
    override suspend fun toggleFavorite(userId: String, product: Product): Result<Unit> {
        if (userId.isBlank()) return Result.failure(IllegalArgumentException("User ID cannot be empty"))
        
        return try {
            val userRef = firestore.collection("users").document(userId)
            val snapshot = userRef.get().await()
            
            if (!snapshot.exists()) {
                userRef.set(mapOf("favorites" to listOf(product.id))).await()
            } else {
                val favorites = snapshot.get("favorites") as? List<String> ?: emptyList()
                if (favorites.contains(product.id)) {
                    userRef.update("favorites", FieldValue.arrayRemove(product.id)).await()
                } else {
                    userRef.update("favorites", FieldValue.arrayUnion(product.id)).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Verifica si un producto específico está en la lista de favoritos del usuario
    override fun isFavorite(userId: String, productId: String): Flow<Boolean> = callbackFlow {
        if (userId.isBlank()) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val favorites = snapshot?.get("favorites") as? List<String> ?: emptyList()
                trySend(favorites.contains(productId))
            }
        awaitClose { subscription.remove() }
    }
}
