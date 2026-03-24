package com.example.marketplacelocal.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    //Funcion para logear al usuario
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    override fun login(email: String, pass: String, onResult: (Result<Unit>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(Result.success(Unit))
                } else {
                    onResult(Result.failure(task.exception ?: Exception("Login failed")))
                }
            }
    }
     //Funcion para registrar al usuario
    override fun register(email: String, pass: String, onResult: (Result<Unit>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(Result.success(Unit))
                } else {
                    onResult(Result.failure(task.exception ?: Exception("Registration failed")))
                }
            }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
