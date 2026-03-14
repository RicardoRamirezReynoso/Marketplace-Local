package com.example.marketplacelocal.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadImage(uri: Uri, path: String): Result<String> {
        return try {
            val storageRef = storage.reference.child(path)
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
