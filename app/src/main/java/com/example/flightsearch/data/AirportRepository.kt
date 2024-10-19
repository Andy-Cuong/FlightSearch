package com.example.flightsearch.data

import com.example.flightsearch.model.Airport
import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    fun searchAirport(nameOrIata: String): Flow<List<Airport>>

    fun getAllAirportsExceptIata(iata: String): Flow<List<Airport>>
}

class OfflineAirportRepository(
    private val airportDao: AirportDao
) : AirportRepository {
    override fun searchAirport(nameOrIata: String): Flow<List<Airport>> =
        airportDao.searchAirport(nameOrIata = nameOrIata)

    override fun getAllAirportsExceptIata(iata: String): Flow<List<Airport>> =
        airportDao.getAllAirportsExceptIata(iata = iata)
}