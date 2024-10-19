package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flightsearch.model.FavoriteFlight
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteFlightDao {
    @Query("SELECT * FROM favorite")
    fun getAllFavoriteFlights(): Flow<List<FavoriteFlight>>

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun addToFavorite(favoriteFlight: FavoriteFlight)

    @Query("DELETE FROM favorite " +
            "WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun removeFromFavorite(departureCode: String, destinationCode: String)
}