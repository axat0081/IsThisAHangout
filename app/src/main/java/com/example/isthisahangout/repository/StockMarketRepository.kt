package com.example.isthisahangout.repository

import androidx.room.withTransaction
import com.example.isthisahangout.api.StockMarketAPI
import com.example.isthisahangout.cache.stockMarket.StockMarketDao
import com.example.isthisahangout.cache.stockMarket.StockMarketDatabase
import com.example.isthisahangout.models.stockCompanies.StockCompany
import com.example.isthisahangout.utils.CSVParser
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockMarketRepository @Inject constructor(
    private val csvParser: CSVParser,
    private val stockMarketAPI: StockMarketAPI,
    private val stockMarketDao: StockMarketDao,
    private val stockMarketDatabase: StockMarketDatabase
) {

    fun getStockMarkets(
        isForcedRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<StockCompany>>> = networkBoundResource(
        query = {
            stockMarketDao.searchCompanyListing()
        },
        fetch = {
            stockMarketAPI.getListings()
        },
        saveFetchResult = { response ->
            val stockCompanies = csvParser.parse(response.byteStream())
            stockMarketDatabase.withTransaction {
                stockMarketDao.clearCompanyListings()
                stockMarketDao.insertStockCompany(stockCompanies)
            }
        },
        shouldFetch = {
            isForcedRefresh ||
                    it.isEmpty() ||
                    System.currentTimeMillis() - it.first().fetchedAt >= TimeUnit.MINUTES.toMillis(
                60
            )
        },
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = onFetchFailed
    )
}