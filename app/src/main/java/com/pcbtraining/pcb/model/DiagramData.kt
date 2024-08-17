package com.pcbtraining.pcb.model

import java.io.Serializable

data class DiagramData(
    val name: String = "",
    val key: String = "",
    val tdia: String = "",
    val link: String = "" // Make sure to provide a default value for link
) : Serializable {
    // Add a no-argument constructor
    constructor() : this("", "", "", "")
}
