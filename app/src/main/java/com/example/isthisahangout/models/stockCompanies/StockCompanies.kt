package com.example.isthisahangout.models.stockCompanies

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class StockCompanyDto(
    @field:Json(name = "Symbol") val symbol: String?,
    @field:Json(name = "Description") val description: String?,
    @field:Json(name = "Name") val name: String?,
    @field:Json(name = "Country") val country: String?,
    @field:Json(name = "Industry") val industry: String?,
)

@Entity(tableName = "stock_companies")
data class StockCompany(
    @PrimaryKey val name: String,
    val symbol: String,
    val description: String,
    val country: String,
    val industry: String,
    val fetchedAt: Long = System.currentTimeMillis()
)

data class StockCompanyDetail(
    val symbol: String,
    val description: String,
    val name: String,
    val country: String,
    val industry: String,
)

fun StockCompanyDto.toStockCompany(): StockCompany =
    StockCompany(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )

