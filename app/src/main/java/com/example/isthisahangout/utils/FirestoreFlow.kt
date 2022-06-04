package com.example.isthisahangout.utils

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