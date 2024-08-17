package com.pcbtraining.pcb.model


data class Order(
    val userId: String = "",
    val status: String = "",
    val productName: String = "",
    val productId: String = "",
    val address: String = "",
    val price: String = "",
    val img: String = "",
    val qty : String = ""
) {
    // Default (no-argument) constructor required for Firebase
    constructor() : this("", "", "", "", "", "", "","")
}
