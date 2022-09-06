package com.example.isthisahangout.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.isthisahangout.models.User
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.utils.PreferencesManager
import com.example.isthisahangout.utils.currentUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class FirebaseAuthViewModel @Inject constructor(
    private val app: Application,
    val state: SavedStateHandle,
    private val mAuth: FirebaseAuth,
    @Named("UserDataRef") private val userRef: DatabaseReference,
    @Named("UserRef") private val userCollectionRef: CollectionReference,
    private val preferencesManager: PreferencesManager
) : AndroidViewModel(app) {

    companion object {
        const val LOGIN = "Login Successful"
        const val REGISTER = "Account Created"
        const val DEFAULTPFP =
            "https://firebasestorage.googleapis.com/v0/b/isthisahangout-61d93.appspot.com/o/pfp%2Fpfp_placeholder.jpg?alt=media&token=35fa14c3-6451-41f6-a8be-448a59996f75"
        const val DEFAULTHEADER =
            "https://firebasestorage.googleapis.com/v0/b/isthisahangout-61d93.appspot.com/o/pfp%2Fpfp_placeholder.jpg?alt=media&token=35fa14c3-6451-41f6-a8be-448a59996f75"
    }

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _userPfp = MutableStateFlow("")
    val userPfp: StateFlow<String> = _userPfp

    private val _userHeader = MutableStateFlow("")
    val userHeader: StateFlow<String> = _userHeader

    private val userDataFlow = preferencesManager.userData

    init {
        viewModelScope.launch {
            userDataFlow.collectLatest { profile ->
                _userId.value = profile.userId
                _username.value = profile.username
                _userPfp.value = profile.pfp
                _userHeader.value = profile.header
            }
        }
    }

    var loginEmail = state.get<String>("loginEmail") ?: ""
        set(value) {
            field = value
            state["loginEmail"] = loginEmail
        }
    var loginPassword = state.get<String>("loginPassword") ?: ""
        set(value) {
            field = value
            state["loginPassword"] = loginPassword
        }
    var registrationEmail = state.get<String>("registrationEmail") ?: ""
        set(value) {
            field = value
            state["registrationEmail"] = registrationEmail
        }
    var registrationPassword = state.get<String>("registrationPassword") ?: ""
        set(value) {
            field = value
            state["registrationPassword"] = registrationPassword
        }
    var registrationUsername = state.get<String>("registrationUsername") ?: ""
        set(value) {
            field = value
            state["registrationUsername"] = registrationUsername
        }
    var profilePfp: Uri? = state.get<Uri>("profilePfp")
        set(value) {
            field = value
            state["profilePfp"] = profilePfp
        }
    var profileHeader: Uri? = state.get<Uri>("profileHeader")
        set(value) {
            field = value
            state["profileHeader"] = profileHeader
        }
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.e("FirebaseAuthViewModel", "onReceive:$intent")
            val type = intent.getStringExtra(FirebaseUploadService.TYPE_OF_UPLOAD)
            if (type == "header")
                updateHeaderResult(intent)
            else if (type == "pfp")
                updatePfpResult(intent)
        }
    }
    private val authChannel = Channel<AuthEvent>()
    val loginFlow = authChannel.receiveAsFlow()
    val registrationFlow = authChannel.receiveAsFlow()
    val profileFlow = authChannel.receiveAsFlow()
    val imageTag = MutableStateFlow("pfp")

    fun updateUserData() {
        if (userId.value != mAuth.currentUser!!.uid) {
            userCollectionRef.document(mAuth.currentUser!!.uid)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val snapshot = it.result!!
                        viewModelScope.launch {
                            preferencesManager.updateUserId(mAuth.currentUser!!.uid)
                            preferencesManager.updateUserName(snapshot.getString("userName")!!)
                            preferencesManager.updateUserPfp(snapshot.getString("pfp")!!)
                            preferencesManager.updateUserHeader(snapshot.getString("header")!!)
                        }
                    }
                }
        }
    }

    fun onLoginClick() {
        if (loginPassword.isBlank()) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.LoginFailure("Password cannot be empty"))
            }
            return
        }
        if (loginEmail.isBlank()) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.LoginFailure("Email cannot be empty"))
            }
            return
        }
        mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
            .addOnCompleteListener { login ->
                if (login.isSuccessful) {
                    if (userId.value == mAuth.currentUser!!.uid) {
                        viewModelScope.launch {
                            authChannel.send(AuthEvent.LoginSuccess(LOGIN))
                        }
                    } else {
                        userCollectionRef.document(mAuth.currentUser!!.uid)
                            .get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val snapshot = it.result!!
                                    viewModelScope.launch {
                                        preferencesManager.updateUserId(mAuth.currentUser!!.uid)
                                        preferencesManager.updateUserName(snapshot.getString("userName")!!)
                                        preferencesManager.updateUserPfp(snapshot.getString("pfp")!!)
                                        preferencesManager.updateUserHeader(snapshot.getString("header")!!)
                                    }
                                    viewModelScope.launch {
                                        authChannel.send(AuthEvent.LoginSuccess(LOGIN))
                                    }
                                } else {
                                    mAuth.signOut()
                                    var msg = "An error occurred"
                                    if (it.exception != null) {
                                        msg = it.exception!!.localizedMessage ?: "An error occurred"
                                    }
                                    viewModelScope.launch {
                                        authChannel.send(AuthEvent.LoginFailure(msg))
                                    }
                                }
                            }
                    }
                } else {
                    viewModelScope.launch {
                        authChannel.send(AuthEvent.LoginFailure(login.exception.toString()))
                    }
                }
            }

    }

    fun onRegistrationClick() {
        if (registrationEmail.isBlank()) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.RegistrationFailure("Email cannot be empty"))
            }
            return
        }
        if (registrationPassword.isBlank()) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.RegistrationFailure("Password cannot be empty"))
            }
            return
        }
        mAuth.createUserWithEmailAndPassword(registrationEmail, registrationPassword)
            .addOnCompleteListener { registration ->
                if (registration.isSuccessful) {
                    userCollectionRef.document(mAuth.currentUser!!.uid)
                        .set(
                            User(
                                email = registrationEmail,
                                pfp = DEFAULTPFP,
                                userName = registrationUsername,
                                header = DEFAULTHEADER
                            )
                        ).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModelScope.launch {
                                    preferencesManager.updateUserId(mAuth.currentUser!!.uid)
                                    preferencesManager.updateUserName(registrationUsername)
                                    preferencesManager.updateUserPfp(DEFAULTPFP)
                                    preferencesManager.updateUserHeader(DEFAULTHEADER)
                                }
                                viewModelScope.launch {
                                    authChannel.send(AuthEvent.RegistrationSuccess(REGISTER))
                                }
                            } else {
                                viewModelScope.launch {
                                    authChannel.send(AuthEvent.RegistrationFailure(task.exception.toString()))
                                }
                            }
                        }
                } else {
                    viewModelScope.launch {
                        authChannel.send(AuthEvent.RegistrationFailure(registration.exception.toString()))
                    }
                }
            }
    }


    fun onUpdatePfpClick() {
        if (profilePfp == null) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.UpdateProfileFailure("Please select a profile picture"))
            }
            return
        }
        viewModelScope.launch {
            app.startService(
                Intent(app, FirebaseUploadService::class.java)
                    .putExtra(FirebaseUploadService.EXTRA_FILE_URI, profilePfp)
                    .putExtra("path", "pfp")
                    .setAction(FirebaseUploadService.ACTION_UPLOAD).apply {
                    }
            )
        }
    }

    fun onUpdateHeaderClick() {
        if (profileHeader == null) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.UpdateProfileFailure("Please select a header for your profile"))
            }
            return
        }
        viewModelScope.launch {
            app.startService(
                Intent(app, FirebaseUploadService::class.java)
                    .putExtra(FirebaseUploadService.EXTRA_FILE_URI, profileHeader)
                    .putExtra("path", "header")
                    .setAction(FirebaseUploadService.ACTION_UPLOAD).apply {
                    }
            )
        }
    }

    fun updatePfpResult(intent: Intent) {
        viewModelScope.launch {
            profilePfp = intent.getParcelableExtra(FirebaseUploadService.EXTRA_DOWNLOAD_URL)
            if (profilePfp == null) {
                authChannel.send(AuthEvent.UpdateProfileFailure("Image could not be updated"))
            } else {
                authChannel.send(AuthEvent.UpdateProfileSuccess("Image updates", profilePfp!!))
                preferencesManager.updateUserPfp(profilePfp.toString())
            }
        }
    }

    fun updateHeaderResult(intent: Intent) {
        viewModelScope.launch {
            profileHeader = intent.getParcelableExtra(FirebaseUploadService.EXTRA_DOWNLOAD_URL)
            if (profileHeader == null) {
                authChannel.send(AuthEvent.UpdateProfileFailure("Image could not be updated"))
            } else {
                authChannel.send(AuthEvent.UpdateProfileSuccess("Image updates", profileHeader!!))
                preferencesManager.updateUserHeader(profileHeader.toString())
            }
        }
    }

    fun sendEmailVerificationClick() {
        val user = currentUser
        if (user == null) {
            viewModelScope.launch {
                authChannel.send(AuthEvent.EmailVerificationError("You are not signed in"))
            }
        } else {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        viewModelScope.launch {
                            authChannel.send(AuthEvent.EmailVerificationSentSuccess(
                                "Please check the email sent to your inbox for verification"))
                        }
                    } else {
                        viewModelScope.launch {
                            authChannel.send(AuthEvent.EmailVerificationSentSuccess(
                                    "Email could not be sent to you for your account verification. Please check your internet connection"
                            ))
                        }
                    }
                }
        }
    }

    sealed class AuthEvent {
        data class LoginSuccess(val message: String) : AuthEvent()
        data class LoginFailure(val message: String) : AuthEvent()
        data class RegistrationSuccess(val message: String) : AuthEvent()
        data class RegistrationFailure(val message: String) : AuthEvent()
        data class UpdateProfileSuccess(val message: String, val uri: Uri) : AuthEvent()
        data class UpdateProfileFailure(val message: String) : AuthEvent()
        data class EmailVerificationSentSuccess(val message: String) : AuthEvent()
        data class EmailVerificationError(val message: String) : AuthEvent()
    }
}