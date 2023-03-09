package com.example.isthisahangout.utils

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

val chatCollectionReference by lazy {
    FirebaseFirestore
        .getInstance()
        .collection("Messages")
}

val messagesQuery by lazy {
    chatCollectionReference
        .orderBy("time", Query.Direction.DESCENDING)
}

val newMessagesQuery by lazy {
    messagesQuery.whereGreaterThan("time", Timestamp.now())
}

val postCollectionReference by lazy {
    FirebaseFirestore.getInstance()
        .collection("Posts")
}

val postsQuery by lazy {
    postCollectionReference
        .orderBy("time", Query.Direction.DESCENDING)
}

val videoCollectionReference by lazy {
    FirebaseFirestore.getInstance()
        .collection("Videos")
}

val videoQuery by lazy {
    videoCollectionReference.orderBy("time", Query.Direction.DESCENDING)
}

val songCollectionReference by lazy {
    FirebaseFirestore.getInstance()
        .collection("Songs")
}
val songQuery by lazy {
    songCollectionReference.orderBy("time", Query.Direction.DESCENDING)
}

val firebaseAuth by lazy {
    FirebaseAuth.getInstance()
}

val currentUser get() = FirebaseAuth.getInstance().currentUser

