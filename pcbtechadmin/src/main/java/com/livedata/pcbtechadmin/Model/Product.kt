package com.livedata.pcbtechadmin.Model

import java.io.Serializable

data class Product(
    val pkey: String = "",
    val pdisc: String = "",
    val pimg: String = "",
    val pimg2: String = "",
    val pimg3: String = "",
    val pname: String = "",
    val pprice: String = "",
    val pstock: String = "",
    val pdel: String = ""
)  : Serializable
