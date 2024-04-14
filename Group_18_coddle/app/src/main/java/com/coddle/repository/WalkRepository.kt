package com.coddle.repository

import android.app.Application
import com.coddle.model.Walking
import com.coddle.util.AppSharedPreference.Companion.getUserId
import com.coddle.util.Constants.GROOMING
import com.coddle.util.Constants.WALKING
import com.coddle.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class WalkRepository(private val application: Application) {

    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun addWalking(walking: Walking) {
        firebaseFirestore.collection(WALKING)
            .document(application.getUserId())
            .collection(WALKING)
            .add(walking)
            .await()
    }

    @ExperimentalCoroutinesApi
    fun getWalking(): Flow<Resource<List<Walking>>> {
        return callbackFlow {
            firebaseFirestore.collection(WALKING)
                .document(application.getUserId())
                .collection(WALKING)
                .get()
                .addOnSuccessListener {
                    trySend(
                        Resource(
                            Resource.ResourceState.SUCCESS,
                            it.toObjects(Walking::class.java)
                        )
                    )
                }
                .addOnFailureListener {
                    trySend(Resource(Resource.ResourceState.ERROR, null, it.message.toString()))
                }
            awaitClose { }
        }
    }

    fun updateWalking(walking: Walking) {
        firebaseFirestore.collection(WALKING)
            .document(application.getUserId())
            .collection(WALKING)
            .whereEqualTo("id", walking.id)
            .limit(1)
            .get()
            .addOnSuccessListener {
                firebaseFirestore.collection(WALKING)
                    .document(application.getUserId())
                    .collection(WALKING)
                    .document(it.documents[0].id)
                    .update(
                        mapOf(
                            "distance" to walking.distance,
                            "steps" to walking.steps
                        )
                    )
            }
    }

}