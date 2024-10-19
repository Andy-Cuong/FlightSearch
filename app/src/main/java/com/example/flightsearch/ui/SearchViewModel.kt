package com.example.flightsearch.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.AirportRepository
import com.example.flightsearch.data.FavoriteFlightRepository
import com.example.flightsearch.data.UserInputPreferencesRepository
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.FavoriteFlight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val airportRepository: AirportRepository,
    private val favoriteFlightRepository: FavoriteFlightRepository,
    private val userInputPreferencesRepository: UserInputPreferencesRepository
) : ViewModel() {
    private val _searchUiStateFlow = MutableStateFlow(SearchUiState())
    val searchUiStateFlow = _searchUiStateFlow.asStateFlow()

    var searchText by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            searchText = userInputPreferencesRepository.lastUserInput.filterNotNull().first()

            _searchUiStateFlow.update { // it is the SearchUiStateFlow
                it.copy(
                    favoriteFlights = favoriteFlightRepository.getAllFavoriteFlights()
                        .filterNotNull()
                        .first(),
                    searchedAirports = airportRepository.searchAirport(searchText)
                        .filterNotNull()
                        .first()
                )
            }
        }
    }

    fun onSearchTextChange(newValue: String) {
        searchText = newValue
        viewModelScope.launch {
            userInputPreferencesRepository.saveLastUserInput(newValue) // Save the last input

            _searchUiStateFlow.update {
                it.copy(
                    chosenAirport = null,
                    searchedAirports = airportRepository.searchAirport(searchText)
                        .filterNotNull()
                        .first()
                )
            }
        }
    }

    fun onSuggestionClicked(airport: Airport) {
        searchText = airport.iataCode
        viewModelScope.launch {
            userInputPreferencesRepository.saveLastUserInput(airport.iataCode)

            _searchUiStateFlow.update {
                it.copy(
                    chosenAirport = airport,
                    destinations = airportRepository.getAllAirportsExceptIata(airport.iataCode)
                        .filterNotNull()
                        .first()
                )
            }
        }
    }

    fun onFavoriteButtonClicked(departure: Airport, destination: Airport) {
        viewModelScope.launch {
            val favoriteFlights = _searchUiStateFlow.filterNotNull().first().favoriteFlights
            var alreadyInFavorite = false
            favoriteFlights.forEach { // If the flight is already in favorites, remove it
                if (it.departureCode == departure.iataCode && it.destinationCode == destination.iataCode) {
                    favoriteFlightRepository.removeFromFavorite(departure.iataCode, destination.iataCode)
                    alreadyInFavorite = true
                    return@forEach
                }
            }

            if (!alreadyInFavorite) { // Otherwise, add it to favorites
                val favorite = FavoriteFlight(departureCode = departure.iataCode, destinationCode = destination.iataCode)
                favoriteFlightRepository.addToFavorite(favorite)
            }

            _searchUiStateFlow.update {
                it.copy(
                    favoriteFlights = favoriteFlightRepository.getAllFavoriteFlights()
                        .filterNotNull()
                        .first()
                )
            }
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as FlightSearchApplication
                val container = application.container
                SearchViewModel(
                    airportRepository = container.airportRepository,
                    favoriteFlightRepository = container.favFlightRepository,
                    userInputPreferencesRepository = container.userInputPreferencesRepository
                )
            }
        }
    }
}

data class SearchUiState(
    val favoriteFlights: List<FavoriteFlight> = listOf<FavoriteFlight>(),
    val chosenAirport: Airport? = null,
    val searchedAirports: List<Airport> = listOf<Airport>(),
    val destinations: List<Airport> = listOf<Airport>()
)