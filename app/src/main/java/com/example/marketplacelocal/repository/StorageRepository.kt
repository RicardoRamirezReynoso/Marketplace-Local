package com.example.marketplacelocal.repository

import android.net.Uri

interface StorageRepository {
    suspend fun uploadImage(uri: Uri, path: String): Result<String>
}
