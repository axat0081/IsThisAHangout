package com.example.isthisahangout.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    @Named("UserDataRef")
    fun providesFirebaseRDUserRef(): DatabaseReference = FirebaseDatabase.getInstance().getReference()
        .child("Users")

    @Provides
    @Singleton
    @Named("PfpRef")
    fun providesPfpStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("pfp")

    @Provides
    @Singleton
    @Named("HeaderRef")
    fun providesHeaderStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("header")

    @Provides
    @Singleton
    @Named("PostImagesRef")
    fun providesPostImagesStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("postImages")

    @Provides
    @Singleton
    @Named("SongUrlRef")
    fun providesSongUrlStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("SongUrl")

    @Provides
    @Singleton
    @Named("ThumbnailRef")
    fun providesThumbnailStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("Thumbnails")

    @Provides
    @Singleton
    @Named("VideoUrlRef")
    fun providesVideoStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("Videos")

    @Provides
    @Singleton
    @Named("CommentsUrlRef")
    fun providesCommentsStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("Comments")

    @Provides
    @Singleton
    @Named("ComfortCharacterUrlRef")
    fun providesComfortCharacterStorageRef(): StorageReference =
        FirebaseStorage.getInstance().getReference("ComfortCharacters")


    @Provides
    @Singleton
    @Named("MessagesRef")
    fun providesMessagesRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("Messages")

    @Provides
    @Singleton
    @Named("PostsRef")
    fun providesPostsRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("Posts")

    @Provides
    @Singleton
    @Named("SongRef")
    fun providesSongRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("Songs")

    @Provides
    @Singleton
    @Named("VideoRef")
    fun providesVideosRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("Videos")

    @Provides
    @Singleton
    @Named("CommentsRef")
    fun providedCommentsRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("Comments")

    @Provides
    @Singleton
    @Named("ComfortCharacterRef")
    fun providesComfortCharacterRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("ComfortCharacters")

    @Provides
    @Singleton
    @Named("UserRef")
    fun providesUserRef(): CollectionReference =
        FirebaseFirestore.getInstance().collection("Users")
}