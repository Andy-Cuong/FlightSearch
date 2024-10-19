package com.example.flightsearch.data

import com.example.flightsearch.model.FavoriteFlight
import kotlinx.coroutines.flow.Flow

interface FavoriteFlightRepository {
    fun getAllFavoriteFlights(): Flow<List<FavoriteFlight>>

    suspend fun addToFavorite(favoriteFlight: FavoriteFlight)

    suspend fun removeFromFavorite(departureCode: String, destinationCode: String)
}

class OfflineFavoriteFlightRepository(
    private val favFlightDao: FavoriteFlightDao
) : FavoriteFlightRepository {
    override fun getAllFavoriteFlights(): Flow<List<FavoriteFlight>> =
        favFlightDao.getAllFavoriteFlights()

    override suspend fun addToFavorite(favoriteFlight: FavoriteFlight) =
        favFlightDao.addToFavorite(favoriteFlight = favoriteFlight)

    override suspend fun removeFromFavorite(departureCode: String, destinationCode: String) =
        favFlightDao.removeFromFavorite(departureCode = departureCode, destinationCode = destinationCode)
}