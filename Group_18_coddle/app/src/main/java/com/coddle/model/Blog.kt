package com.coddle.model

import com.google.firebase.Timestamp

data class Blog(
        val id: String = System.currentTimeMillis().toString(),
        var title: String = "",
        var link: String = "",
        val timestamp: Timestamp = Timestamp.now()
)
