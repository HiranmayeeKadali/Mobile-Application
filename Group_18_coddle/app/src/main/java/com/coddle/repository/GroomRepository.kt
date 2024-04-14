package com.coddle.repository

import android.app.Application
import com.coddle.model.Grooming
import com.coddle.util.AppSharedPreference.Companion.getUserId
import com.coddle.util.Constants.GROOMING
import com.coddle.util.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GroomRepository(private val application: Application) {

    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun addGrooming(grooming: Grooming) {
        firebaseFirestore.collection(GROOMING)
            .document(application.getUserId())
            .collection(GROOMING)
            .add(grooming)
            .await()
    }

    @ExperimentalCoroutinesApi
    fun getGrooming():Flow<Resource<List<Grooming>>>{
        return callbackFlow {
            firebaseFirestore.collection(GROOMING)
                .document(application.getUserId())
                .collection(GROOMING)
                .get()
                .addOnSuccessListener {
                    trySend(Resource(Resource.ResourceState.SUCCESS,it.toObjects(Grooming::class.java)))
                }
                .addOnFailureListener {
                    trySend(Resource(Resource.ResourceState.ERROR,null,it.message.toString()))
                }
            awaitClose {  }
        }
    }

}