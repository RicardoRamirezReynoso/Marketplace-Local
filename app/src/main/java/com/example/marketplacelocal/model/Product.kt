package com.example.marketplacelocal.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val deliveryLocation: String = "",
    val condition: String = "Nuevo",
    val sellerEmail: String = "" // Campo para el correo del vendedor
)
