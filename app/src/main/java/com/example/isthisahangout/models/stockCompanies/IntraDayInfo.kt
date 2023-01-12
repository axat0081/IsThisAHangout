package com.example.isthisahangout.models.stockCompanies

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class IntradayInfo(
    val date: LocalDateTime,
    val close: Double
)

data class IntraDayInfoDto(
    val timestamp: String,
    val close: Double
)

@RequiresApi(Build.VERSION_CODES.O)
fun IntraDayInfoDto.toIntraDayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}

