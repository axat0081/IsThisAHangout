package com.example.isthisahangout.cache.stockMarket

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.isthisahangout.models.stockCompanies.StockCompany

@Database(
    entities = [StockCompany::class],
    version = 1
)
abstract class StockMarketDatabase : RoomDatabase() {
    abstract fun getStockMarketDao(): StockMarketDao
}