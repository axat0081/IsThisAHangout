package com.example.isthisahangout.models.reminders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(
    val id: String? = null,
    val name: String? = null,
    val desc: String? = null,
    val time: Long = 0,
    val done: Boolean = false,
    val userId: String? = null
): Parcelable