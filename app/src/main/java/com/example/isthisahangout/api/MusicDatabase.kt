package com.example.isthisahangout.api

import com.example.isthisahangout.models.SongDto
import com.example.isthisahangout.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val MEDIA_ROOT_ID = "root_id"
class MusicDatabase @Inject constructor() {
    suspend fun getAllSongs(): List<SongDto> {
        return try{
            FirebaseFirestore.getInstance().collection("Songs")
                .get().await().toObjects(SongDto::class.java)
        } catch (exception: Exception){
            emptyList()
        }
    }
}