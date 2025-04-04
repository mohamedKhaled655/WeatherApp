package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.data.models.FavoriteLocationEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest {

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao
    private lateinit var alertDao: AlertDao
    private lateinit var localDataSource: LocalDataSource


    @Before
    fun setUp(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        weatherDao=database.getWeatherDao()
        alertDao=database.alertDao()
        localDataSource=WeatherLocalDataSource(weatherDao,alertDao)
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertFavLocation_insertsLocationSuccessfully() = runTest {
        // Given:
        val favoriteLocation = FavoriteLocationEntity(
            name = "Eg",
            country = "Fayoum",
            latitude = 51.5074,
            longitude = -0.1278
        )

        // When:
        val result = localDataSource.insertFavLocation(favoriteLocation)

        // Then:
        assertTrue(result > 0)


        val allLocations = localDataSource.getAllFavLocations().first()


        val found = allLocations.any {
            it.name == favoriteLocation.name &&
                    it.country == favoriteLocation.country &&
                    it.latitude == favoriteLocation.latitude &&
                    it.longitude == favoriteLocation.longitude
        }

        assertTrue(found)
    }


    @Test
    fun getAllFavLocations_returnsAllInsertedLocations() = runTest {
        // Given:
        val london = FavoriteLocationEntity(
            name = "London",
            country = "UK",
            latitude = 51.5074,
            longitude = -0.1278
        )

        val tokyo = FavoriteLocationEntity(
            name = "Tokyo",
            country = "Japan",
            latitude = 35.6762,
            longitude = 139.6503
        )

        val cairo = FavoriteLocationEntity(
            name = "Cairo",
            country = "Egypt",
            latitude = 30.0444,
            longitude = 31.2357
        )


        localDataSource.insertFavLocation(london)
        localDataSource.insertFavLocation(tokyo)
        localDataSource.insertFavLocation(cairo)

        // When:
        val allLocations = localDataSource.getAllFavLocations().first()

        // Then:
        assertEquals(3, allLocations.size)


        assertTrue(allLocations.any { it.name == "London" && it.country == "UK" })
        assertTrue(allLocations.any { it.name == "Tokyo" && it.country == "Japan" })
        assertTrue(allLocations.any { it.name == "Cairo" && it.country == "Egypt" })
    }

}