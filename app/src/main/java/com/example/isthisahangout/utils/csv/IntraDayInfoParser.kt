package com.example.isthisahangout.utils.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.isthisahangout.models.stockCompanies.IntradayInfo
import com.example.isthisahangout.models.stockCompanies.IntraDayInfoDto
import com.example.isthisahangout.models.stockCompanies.toIntraDayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntraDayInfoParser @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntraDayInfoDto(timestamp, close.toDouble())
                    dto.toIntraDayInfo()
                }
                .filter {
                    it.date.dayOfMonth == LocalDate.now().minusDays(4).dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}