package com.example.isthisahangout.utils.csv

import com.example.isthisahangout.models.stockCompanies.StockCompany
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockCompaniesParser @Inject constructor() {
    suspend fun parse(stream: InputStream): List<StockCompany> = withContext(Dispatchers.IO) {
        val csvReader = CSVReader(InputStreamReader(stream))
        csvReader
            .readAll()
            .drop(1)
            .mapNotNull { line ->
                val symbol = line.getOrNull(0)
                val name = line.getOrNull(1)
                val exchange = line.getOrNull(2)
                StockCompany(
                    name = name ?: return@mapNotNull null,
                    symbol = symbol ?: return@mapNotNull null,
                    description = "",
                    country = "",
                    industry = exchange ?: return@mapNotNull null,
                    fetchedAt = System.currentTimeMillis()
                )
            }.also {
                csvReader.close()
            }
    }
}