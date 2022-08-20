package com.example.isthisahangout.utils

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Query.asFlow(): Flow<QuerySnapshot> = callbackFlow {
    val registration = addSnapshotListener { snapshots, error ->
        if (error != null) {
            close(error)
        } else {
            if (snapshots != null) {
                this.trySend(snapshots).isSuccess
            }
        }
    }
    awaitClose { registration.remove() }
}

fun <T> Query.asResourceFlow(
    parseToObject: (QuerySnapshot) -> List<T>
): Flow<Resource<List<T>>> = callbackFlow {
    val registration = addSnapshotListener { snapshots, error ->
        if (error != null) {
            this.trySend(Resource.Error(throwable = error)).isFailure
        } else {
            if (snapshots != null) {
                val data = parseToObject(snapshots)
                this.trySend(Resource.Success(data)).isSuccess
            }
        }
    }
    awaitClose { registration.remove() }
}

fun DocumentReference.asFlow(): Flow<DocumentSnapshot> = callbackFlow {
    val registration = addSnapshotListener { value, error ->
        if(error !=null) {
            close(error)
        } else if(value != null) {
            this.trySend(value).isSuccess
        }
    }
    awaitClose { registration.remove() }
}