package com.example.marketplacelocal.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun login(email: String, pass: String, onResult: (Result<Unit>) -> Unit)
    fun register(email: String, pass: String, onResult: (Result<Unit>) -> Unit)
    fun logout()
}
