package com.coddle.repository

import com.coddle.model.Blog
import com.coddle.util.Constants.BLOGS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class BlogRepository {
    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun getBlogs(): List<Blog> {
        return firebaseFirestore.collection(BLOGS)
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .await()
            .toObjects(Blog::class.java)
    }

}