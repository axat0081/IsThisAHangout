package com.example.isthisahangout.repository

import Reminder
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.models.ComfortCharacter
import com.example.isthisahangout.utils.FirebaseResult
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.asFlow
import com.example.isthisahangout.utils.asResourceFlow
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @Named("ComfortCharacterRef")
    private val comfortCharacterRef: CollectionReference,
    @Named("RemindersRef")
    private val remindersRef: CollectionReference
) {

    fun getComfortCharacters(userId: String): Flow<List<ComfortCharacter>> =
        comfortCharacterRef.document(userId).collection("comfort_characters")
            .orderBy("priority")
            .asFlow().map { snapshots ->
                snapshots.map {
                    it.toObject(ComfortCharacter::class.java)
                }
            }

    fun getReminders(userId: String): Flow<Resource<List<Reminder>>> =
        remindersRef.document(userId).collection("reminders")
            .orderBy("time")
            .asResourceFlow { snapshots ->
                snapshots.toObjects(Reminder::class.java)
            }

    fun createReminder(userId: String, reminder: Reminder): Flow<Resource<FirebaseResult>> = flow {
        emit(Resource.Loading())
        val id = userId + System.currentTimeMillis()
        try {
            remindersRef.document(userId).collection("reminders")
                .document(id)
                .set(reminder)
                .await()
            emit(Resource.Success(FirebaseResult(result = true, message = "Reminder added")))
        } catch (exception: Exception) {
            emit(Resource.Error(exception))
        }
    }

    suspend fun sendTokenToServer(token: String) {
        FirebaseFirestore.getInstance().collection("Tokens")
            .document(MainActivity.userId).set(token).await()
    }

}