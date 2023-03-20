package com.example.isthisahangout.service.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onReceive(context: Context?, intent: Intent?) {
        val reminderName = intent?.getStringExtra("reminder_name") ?: return
        val reminderUserId = intent.getStringExtra("reminder_user_id")
        val reminderDesc = intent.getStringExtra("reminder_desc")
        val reminderIsDone = intent.getBooleanExtra("reminder_done", false)
        /*
        Current logged in user is not the one who set the reminder
         */
        if(mAuth.currentUser?.uid != reminderUserId) {
            return
        }

    }
}