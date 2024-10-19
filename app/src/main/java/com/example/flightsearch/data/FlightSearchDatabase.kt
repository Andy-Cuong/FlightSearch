package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.FavoriteFlight

@Database(entities = [Airport::class, FavoriteFlight::class], version = 1, exportSchema = false)
abstract class FlightSearchDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao

    abstract fun favFlightDao(): FavoriteFlightDao

    companion object {
        @Volatile
        private var Instance: FlightSearchDatabase? = null

        fun getDatabase(context: Context) =
            Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = FlightSearchDatabase::class.java,
                    name = "flight_search_database"
                )
                    .fallbackToDestructiveMigration()
//                    .createFromAsset("databases/flight_search.db") // Comment out this after the first run to retain user data
                    .build()
            }.also { Instance = it }
    }
}