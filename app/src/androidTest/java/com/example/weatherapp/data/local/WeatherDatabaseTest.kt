package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.data.models.FavoriteLocationEntity
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDatabaseTest {
    lateinit var database: WeatherDatabase
    lateinit var dao: WeatherDao


    @Before
    fun setUp(){
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        dao=database.getWeatherDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun addLocationToFavorites() = runTest {
        // Given
        val location = FavoriteLocationEntity(
            country = "Egy",
            name = "Cairo",
            latitude = 40.7128,
            longitude = -74.0060
        )

        // When
        val result = dao.addToFavorites(location)

        // Then
        assertTrue(result > 0)
    }


    @Test
    fun getFavoritesLocations() = runTest {
        // Given
        val location = FavoriteLocationEntity(
            country = "USA",
            name = "New York",
            latitude = 40.7128,
            longitude = -74.0060
        )
        dao.addToFavorites(location)


        // When
        val favorites = dao.getFavoritesLocations().first()

        // Then
        val found = favorites.any {
            it.name == location.name &&
                    it.country == location.country &&
                    it.latitude == location.latitude &&
                    it.longitude == location.longitude
        }
        assertTrue(found)
    }

}