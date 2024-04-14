package com.coddle.model

import java.util.*

data class Grooming(
    val id: String = UUID.randomUUID().toString(),
    var description: String = "",
    var date: String = ""
)
