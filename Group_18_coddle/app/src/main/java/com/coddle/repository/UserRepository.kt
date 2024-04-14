package com.coddle.repository

import com.coddle.model.User
import com.coddle.util.Constants.USERS
import com.coddle.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    @ExperimentalCoroutinesApi
    suspend fun isEmailExist(email: String): Flow<Resource<Boolean>> {
        return callbackFlow {
            if (firebaseAuth.currentUser == null) {
                firebaseAuth.signInAnonymously().addOnSuccessListener {
                    firebaseFirestore.collection(USERS)
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener {
                            trySend(Resource(Resource.ResourceState.SUCCESS, !it.isEmpty))
                        }
                        .addOnFailureListener {
                            trySend(
                                Resource(
                                    Resource.ResourceState.ERROR,
                                    null,
                                    it.message.toString()
                                )
                            )
                        }
                }
            } else {
                firebaseFirestore.collection(USERS)
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            trySend(Resource(Resource.ResourceState.SUCCESS, false))
                        } else {
                            trySend(Resource(Resource.ResourceState.SUCCESS, true))
                        }
                    }
                    .addOnFailureListener {
                        trySend(
                            Resource(
                                Resource.ResourceState.ERROR,
                                null,
                                it.message.toString()
                            )
                        )
                    }
            }
            awaitClose { }
        }
    }

    @ExperimentalCoroutinesApi
    fun addUser(user: User, password: String): Flow<Resource<Boolean>> {
        return callbackFlow {
            firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
                firebaseFirestore.collection(USERS)
                    .add(user)
                    .addOnSuccessListener {
                        trySend(Resource(Resource.ResourceState.SUCCESS, true))
                    }
                    .addOnFailureListener {
                        trySend(
                            Resource(
                                Resource.ResourceState.ERROR,
                                false,
                                it.message.toString()
                            )
                        )
                    }
            }.addOnFailureListener {
                trySend(Resource(Resource.ResourceState.ERROR, false, it.message.toString()))
            }
            awaitClose { }
        }
    }

    @ExperimentalCoroutinesApi
    fun login(email: String, password: String):Flow<Resource<User>>{
       return callbackFlow {
           firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
               firebaseFirestore.collection(USERS)
                   .whereEqualTo("email",email)
                   .limit(1)
                   .get()
                   .addOnSuccessListener {
                       trySend(Resource(Resource.ResourceState.SUCCESS,it.documents[0].toObject(User::class.java)))
                   }
                   .addOnFailureListener {
                       trySend(Resource(Resource.ResourceState.ERROR,null,it.message.toString()))
                   }
           }.addOnFailureListener {
               trySend(Resource(Resource.ResourceState.ERROR,null,it.message.toString()))
           }
           awaitClose {  }
       }
    }

    fun updateUser(user: User){
        firebaseFirestore.collection(USERS)
            .whereEqualTo("userId",user.userId)
            .limit(1)
            .get()
            .addOnSuccessListener {
                firebaseFirestore.collection(USERS)
                    .document(it.documents[0].id)
                    .update(
                        mapOf(
                            "username" to user.username,
                            "petName" to user.petName,
                            "breed" to user.breed,
                            "type" to user.type,
                            "dob" to user.dob,
                        )
                    )
            }
    }

}