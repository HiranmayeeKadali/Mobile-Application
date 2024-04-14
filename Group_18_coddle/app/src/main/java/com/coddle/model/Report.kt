package com.coddle.model

import com.google.firebase.Timestamp

data class Report(
    val id:String = System.currentTimeMillis().toString(),
    var userId:String = "",
    var title:String = "",
    var description:String = "",
    val timestamp: Timestamp = Timestamp.now()
)
