package com.coddle.repository

import android.app.Application
import com.coddle.model.Medical
import com.coddle.util.AppSharedPreference.Companion.getUserId
import com.coddle.util.Constants.MEDICAL
import com.coddle.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MedicalRepository(private val application: Application) {

    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun addMedical(medical: Medical) {
        firebaseFirestore.collection(MEDICAL)
            .document(application.getUserId())
            .collection(MEDICAL)
            .add(medical)
            .await()
    }

    @ExperimentalCoroutinesApi
    fun getMedical():Flow<Resource<List<Medical>>>{
        return callbackFlow {
            firebaseFirestore.collection(MEDICAL)
                .document(application.getUserId())
                .collection(MEDICAL)
                .get()
                .addOnSuccessListener {
                    trySend(Resource(Resource.ResourceState.SUCCESS,it.toObjects(Medical::class.java)))
                }
                .addOnFailureListener {
                    trySend(Resource(Resource.ResourceState.ERROR,null,it.message.toString()))
                }
            awaitClose {  }
        }
    }

}