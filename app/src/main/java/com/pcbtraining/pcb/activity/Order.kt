package com.pcbtraining.pcb.activity


data class Order(
    val totalCost: Long = 0L, // Assume total cost is stored as Long
    val products: List<Map<String, Any>> = emptyList(), // Products stored as list of maps
    val address: Map<String, String> = emptyMap(), // Address stored as a map of strings
    val purchaseTime: Long = 0L // Purchase time stored as Long (timestamp)
)

