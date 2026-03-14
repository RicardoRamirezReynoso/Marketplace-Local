package com.example.marketplacelocal.di

import com.example.marketplacelocal.repository.AuthRepository
import com.example.marketplacelocal.repository.FirebaseAuthRepository
import com.example.marketplacelocal.repository.FirebaseProductRepository
import com.example.marketplacelocal.repository.FirebaseStorageRepository
import com.example.marketplacelocal.repository.ProductRepository
import com.example.marketplacelocal.repository.StorageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideProductRepository(firestore: FirebaseFirestore): ProductRepository {
        return FirebaseProductRepository(firestore)
    }

    //Inyecta FirebaseAuth para construir el repositorio de autenticación
    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return FirebaseAuthRepository(auth)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository {
        return FirebaseStorageRepository(storage)
    }
}
