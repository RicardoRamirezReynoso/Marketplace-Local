package com.example.marketplacelocal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketplacelocal.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Usuario actualmente autenticado
    private val _currentUser = MutableStateFlow<FirebaseUser?>(repository.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Estado de carga para las operaciones de auth
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensajes de error para mostrar en la UI
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // autentica a un usuario con su email y contraseña
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.login(email, pass) { result ->
                _isLoading.value = false
                if (result.isSuccess) {
                    _currentUser.value = repository.currentUser
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            }
        }
    }

    // Crea una nueva cuenta de usuario en Firebase
    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.register(email, pass) { result ->
                _isLoading.value = false
                if (result.isSuccess) {
                    _currentUser.value = repository.currentUser
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            }
        }
    }

    // Cierra la sesión actual del usuario
    fun logout() {
        repository.logout()
        _currentUser.value = null
    }
}
