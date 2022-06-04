package com.example.isthisahangout.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "messages")
@Parcelize
data class Message(
    @PrimaryKey
    val id: String,
    val text: String,
    val senderId: String,
    val time: Long = System.currentTimeMillis(),
    val username: String
) : Parcelable

@Parcelize
data class FirebaseMessage(
    val id: String? = null,
    val text: String? = "",
    val senderId: String? = "",
    val time: Timestamp = Timestamp.now(),
    val username: String? = "",
): Parcelable