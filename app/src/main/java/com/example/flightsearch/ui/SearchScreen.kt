package com.example.flightsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.FavoriteFlight
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
) {
    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory)
    val searchUiState by searchViewModel.searchUiStateFlow.collectAsStateWithLifecycle()
    val searchText = searchViewModel.searchText

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = searchViewModel::onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.airport_name_or_iata_code)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_for_airport)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            shape = MaterialTheme.shapes.large
        )
        if (searchUiState.chosenAirport == null) {
            if (searchText == "") { // Display the favorite list
                Text(
                    text = stringResource(R.string.favorite_routes),
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                FavoriteFlightList(
                    favorites = searchUiState.favoriteFlights,
                    onFavoriteButtonClicked = searchViewModel::onFavoriteButtonClicked,
                )
            } else { // Display the suggestion based on the search text
                SuggestionList(
                    airports = searchUiState.searchedAirports,
                    onSuggestionClicked = searchViewModel::onSuggestionClicked
                )
            }
        } else { // Display the flights from the chosen airport
            Text(
                text = stringResource(R.string.flights_from, searchUiState.chosenAirport!!.iataCode),
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            FlightList(
                departure = searchUiState.chosenAirport!!,
                destinations = searchUiState.destinations,
                favorites = searchUiState.favoriteFlights,
                onFavoriteButtonClicked = searchViewModel::onFavoriteButtonClicked
            )
        }
    }
}

@Composable
fun FavoriteFlightList(
    favorites: List<FavoriteFlight>,
    onFavoriteButtonClicked: (Airport, Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.no_favorite))
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = favorites, key = { fav -> fav.id }) { fav ->
                val departure = Airport(0, iataCode = fav.departureCode, name = "", passengers = 0)
                val destination = Airport(0, iataCode = fav.destinationCode, name = "", passengers = 0)

                FlightCard(
                    departure = departure,
                    destination = destination,
                    isFavorite = true,
                    onFavoriteButtonClicked = onFavoriteButtonClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FlightList(
    departure: Airport,
    destinations: List<Airport>,
    favorites: List<FavoriteFlight>,
    onFavoriteButtonClicked: (Airport, Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    if (destinations.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.no_flight))
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(items = destinations, key = { destination -> destination.id }) { destination ->
                var isFavorite = false
                favorites.forEach {
                    if (it.departureCode == departure.iataCode && it.destinationCode == destination.iataCode) {
                        isFavorite = true
                        return@forEach
                    }
                }

                FlightCard(
                    departure = departure,
                    destination = destination,
                    isFavorite = isFavorite,
                    onFavoriteButtonClicked = onFavoriteButtonClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SuggestionList(
    airports: List<Airport>,
    onSuggestionClicked: (Airport) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding
    ) {
        if (airports.isEmpty()) {
            items(listOf("")) {
                Text(text = stringResource(R.string.no_suggestion))
            }
        } else {
            items(items = airports, key = { airport -> airport.id }) { airport ->
                SuggestionCard(
                    airport = airport,
                    onSuggestionClicked = onSuggestionClicked
                )
            }
        }
    }
}

@Composable
fun FlightCard(
    departure: Airport,
    destination: Airport,
    isFavorite: Boolean,
    onFavoriteButtonClicked: (Airport, Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(2.5f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.depart),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = departure.iataCode,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = departure.name)
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_airplane),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .rotate(70F)
                    .padding(4.dp)
                    .weight(1f)
            )
            Column(
                modifier = Modifier.weight(2.5f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.arrive),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = destination.iataCode,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = destination.name,
                    textAlign = TextAlign.End
                    // Modifier.align doesn't work here because the text already fills max width
                )
            }
            VerticalDivider(
                modifier = Modifier.padding(start = 8.dp, end = 4.dp)
            )
            IconButton(
                onClick = { onFavoriteButtonClicked(departure, destination) },
                modifier = Modifier.weight(0.5f)
            ) {
                if (isFavorite) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filled_star),
                        contentDescription = stringResource(R.string.favorite),
                        tint = Color(0xFFFFB701)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_outlined_star),
                        contentDescription = stringResource(R.string.not_favorite)
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionCard(
    airport: Airport,
    onSuggestionClicked: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable(
                onClickLabel = stringResource(R.string.press_to_choose),
                onClick = { onSuggestionClicked(airport) }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = (-8).dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = airport.iataCode,
                modifier = Modifier.padding(start = 4.dp, end = 12.dp),
                fontWeight = FontWeight.Bold
            )
            Text(text = airport.name)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlightCardPrev() {
    val departure = Airport(1, "FCO", "Leonardo da Vinci International Airport", 8000)
    val destination = Airport(2, "MUC", "Munich International Airport", 9000)

    FlightSearchTheme {
        FlightCard(departure, destination, true, { departure, destination -> })
    }
}

@Preview(showBackground = true)
@Composable
private fun SuggestionCardPrev() {
    val airport = Airport(2, "MUC", "Munich International Airport", 9000)

    FlightSearchTheme {
        SuggestionCard(airport, {})
    }
}