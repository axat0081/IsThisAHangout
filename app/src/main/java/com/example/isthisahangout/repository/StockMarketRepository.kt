package com.example.isthisahangout.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.example.isthisahangout.api.CryptoCoinAPI
import com.example.isthisahangout.api.StockMarketAPI
import com.example.isthisahangout.cache.stockMarket.StockMarketDao
import com.example.isthisahangout.cache.stockMarket.StockMarketDatabase
import com.example.isthisahangout.models.cryptocoin.Coin
import com.example.isthisahangout.models.cryptocoin.CoinDetail
import com.example.isthisahangout.models.cryptocoin.toCoin
import com.example.isthisahangout.models.cryptocoin.toCoinDetail
import com.example.isthisahangout.models.stockCompanies.IntradayInfo
import com.example.isthisahangout.models.stockCompanies.StockCompany
import com.example.isthisahangout.models.stockCompanies.toStockCompany
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.csv.IntraDayInfoParser
import com.example.isthisahangout.utils.csv.StockCompaniesParser
import com.example.isthisahangout.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockMarketRepository @Inject constructor(
    private val csvParser: StockCompaniesParser,
    private val stockMarketAPI: StockMarketAPI,
    private val stockMarketDao: StockMarketDao,
    private val stockMarketDatabase: StockMarketDatabase,
    private val intraDayInfoParser: IntraDayInfoParser,
    private val cryptoCoinAPI: CryptoCoinAPI
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getIntraDayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = stockMarketAPI.getIntraDayInfo(symbol)
            val results = intraDayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch (exception: IOException) {
            Resource.Error(throwable = exception)
        } catch (exception: HttpException) {
            Resource.Error(throwable = exception)
        }
    }

    suspend fun getCompanyInfo(symbol: String): Resource<StockCompany> {
        return try {
            val result = stockMarketAPI.getCompanyInfo(symbol = symbol)
            Resource.Success(result.toStockCompany())
        } catch (exception: IOException) {
            Resource.Error(throwable = exception)
        } catch (exception: HttpException) {
            Resource.Error(throwable = exception)
        }
    }


    fun getCoins(): Flow<Resource<List<Coin>>> = flow {
        emit(Resource.Loading())
        try {
            val coins = cryptoCoinAPI.getCoins().map { coinDto ->
                coinDto.toCoin()
            }
            Log.e("coins", coins.toString())
            emit(Resource.Success(coins))
        } catch (exception: IOException) {
            emit(Resource.Error(throwable = exception))
        } catch (exception: HttpException) {
            emit(Resource.Error(throwable = exception))
        }
    }

    fun getCoinDetail(coinId: String): Flow<Resource<CoinDetail>> = flow {
        emit(Resource.Loading())
        try {
            val coinDetail = cryptoCoinAPI.getCoinById(coinId).toCoinDetail()
            emit(Resource.Success(coinDetail))
        } catch (exception: IOException) {
            emit(Resource.Error(throwable = exception))
        } catch (exception: HttpException) {
            emit(Resource.Error(throwable = exception))
        }
    }

}