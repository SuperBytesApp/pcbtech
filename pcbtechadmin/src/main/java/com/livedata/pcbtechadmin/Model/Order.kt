package com.livedata.pcbtechadmin.Model


data class Order(
    val userId: String = "",
    val products: List<Products> = listOf(),
    val totalCost: Double = 0.0,
    val address: Address = Address(),
    val purchaseTime: Long = 0
)

data class Products(
    val productName: String? = null,
    val price: String? = null,  // Change to String if Firebase stores it as String
    val quantity: Int? = null
)

data class Address(
    val serviceAddress: String = "",
    val landmark: String = "",
    val pinCode: String = "",
    val no: String = "",
    val category: String = ""
)
