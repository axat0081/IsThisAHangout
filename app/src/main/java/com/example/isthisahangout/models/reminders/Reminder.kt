import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(
    val name: String? = null,
    val desc: String? = null,
    val time: Long = 0,
    val isDone: Boolean = false
): Parcelable