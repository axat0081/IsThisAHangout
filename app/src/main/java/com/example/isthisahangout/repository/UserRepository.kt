package com.example.isthisahangout.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.isthisahangout.models.ComfortCharacter
import com.example.isthisahangout.models.User
import com.example.isthisahangout.models.reminders.Reminder
import com.example.isthisahangout.utils.FirebaseResult
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.asFlow
import com.example.isthisahangout.utils.asResourceFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

const val LOGIN = "Login Successful"
const val REGISTER = "Account Created"
const val UPDATE = "Profile Updated"
const val DEFAULTPFP =
    "https://firebasestorage.googleapis.com/v0/b/isthisahangout-61d93.appspot.com/o/pfp%2Fpfp_placeholder.jpg?alt=media&token=35fa14c3-6451-41f6-a8be-448a59996f75"

@Singleton
class UserRepository @Inject constructor(
    private val mAuth: FirebaseAuth,
    @Named("UserDataRef")
    private val UserRef: DatabaseReference,
    @Named("PfpRef")
    private val pfpRef: StorageReference,
    @Named("ComfortCharacterRef")
    private val comfortCharacterRef: CollectionReference,
    @Named("RemindersRef")
    private val remindersRef: CollectionReference
) {
    fun login(email: String, password: String): FirebaseResult {
        val result = FirebaseResult(false, "")
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                result.message = task.exception.toString()
            } else {
                result.result = true
                result.message = LOGIN
            }
        }
        return result
    }

    suspend fun register(username: String, email: String, password: String): FirebaseResult {
        val result = FirebaseResult(false, "Not Done")
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                result.message = task.exception.toString()
            } else {
                val id = mAuth.currentUser!!.uid
                UserRef.child(id).setValue(
                    User(
                        userName = username,
                        email = email,
                        pfp = DEFAULTPFP,
                        header = DEFAULTPFP
                    )
                ).addOnCompleteListener { task2 ->
                    if (!task2.isSuccessful) {
                        result.message = task2.exception.toString()
                    } else {
                        Log.e("enic", "hello")
                        result.result = true
                        result.message = REGISTER
                    }
                }
            }
        }
        return result
    }

    fun updateProfile(username: String, pfp: Uri?, context: Context): FirebaseResult {
        val result = FirebaseResult(false, "")
        if (pfp != null) {
            pfpRef.putFile(pfp).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    result.message = task.exception.toString()
                } else {
                    UserRef.child(mAuth.currentUser!!.uid).child("pfp").setValue(username)
                        .addOnCompleteListener { task2 ->
                            if (!task2.isSuccessful) {
                                result.message = task2.exception.toString()
                            } else {
                                result.message = UPDATE
                            }
                        }
                }
            }
        }
        return result
    }


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

}