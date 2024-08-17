package com.pcbtraining.pcb.model

data class ChatMessage(
    var senderId: String = "",
    var senderName: String = "",
    var message: String = "",
    var timestamp: Long = 0,
    var imageUrl: String? = null  // Assuming imageUrl is a String
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", 0, null)
}

