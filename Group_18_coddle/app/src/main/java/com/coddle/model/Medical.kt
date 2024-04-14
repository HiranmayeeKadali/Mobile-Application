package com.coddle.model

import java.util.*

data class Medical(
    val id: String = UUID.randomUUID().toString(),
    var description: String = "",
    var date: String = ""
)
