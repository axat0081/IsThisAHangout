package com.example.isthisahangout.cache.stockMarket

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isthisahangout.models.stockCompanies.StockCompany
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMarketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockCompany(
        companyListingEntities: List<StockCompany>
    )

    @Query("DELETE FROM stock_companies")
    suspend fun clearCompanyListings()

    @Query("SELECT * FROM stock_companies")
    fun searchCompanyListing(): Flow<List<StockCompany>>
}