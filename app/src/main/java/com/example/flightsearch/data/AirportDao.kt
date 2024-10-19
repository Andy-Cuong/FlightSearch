package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport " +
            "WHERE name LIKE '%' || :nameOrIata || '%' " +
            "OR iata_code LIKE '%' || :nameOrIata || '%' " +
            "ORDER BY passengers DESC")
    fun searchAirport(nameOrIata: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport " +
            "WHERE NOT iata_code = :iata " +
            "ORDER BY passengers DESC")
    fun getAllAirportsExceptIata(iata: String): Flow<List<Airport>>
}