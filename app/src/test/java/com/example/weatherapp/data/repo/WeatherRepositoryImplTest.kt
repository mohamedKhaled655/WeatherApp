package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.FakeLocalDataSource
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.Sys
import com.example.weatherapp.data.models.Weather
import com.example.weatherapp.data.models.Wind
import com.example.weatherapp.data.remote.FakeRemoteDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

     val fakeFavList = listOf(
        FavoriteLocationEntity( name = "Cairo", country = "Eg", longitude = 31.0, latitude = 30.0),
        FavoriteLocationEntity( name = "Alex", country = "Eg", longitude = 31.0, latitude = 30.0),
        FavoriteLocationEntity( name = "Alex", country = "Eg", longitude = 31.0, latitude = 30.0),
    )

    private val fakeWeather = CurrentWeatherModel(
        id = 1,
        coord = Coord(lon = 31.0, lat = 30.0),
        weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
        base = "stations",
        main = Main(
            temp = 25.0,
            feels_like = 26.0,
            temp_min = 24.0,
            temp_max = 27.0,
            pressure = 1012,
            humidity = 60
        ),
        visibility = 10000,
        wind = Wind(speed = 5.0, deg = 270),
        clouds = Clouds(all = 0),
        dt = System.currentTimeMillis(),
        sys = Sys(country = "EG", sunrise = 1680230000, sunset = 1680273200),
        timezone = 7200,
        name = "Cairo",
        cod = 200
    )

     lateinit var repository: WeatherRepositoryImpl
     lateinit var fakeLocalDataSource: FakeLocalDataSource
     lateinit var fakeRemoteDataSource: FakeRemoteDataSource

    @Before
    fun setup() {
        fakeLocalDataSource = FakeLocalDataSource(fakeFavList)
        fakeRemoteDataSource = FakeRemoteDataSource(
            currentWeather = fakeWeather,
        )
        repository = WeatherRepositoryImpl.getInstance(fakeRemoteDataSource, fakeLocalDataSource)
    }


    @Test
    fun `getAllFavLocations returns correct list`() = runTest {
        //given

        //when
        val result = repository.getAllFavLocations().first()
        //then
        assertEquals(fakeFavList, result)
    }


    @Test
    fun addFavLocation_shouldAddLocation_returnSuccessfully() = runTest {
        // Given
        val newFav = FavoriteLocationEntity( name = "Cairo", country = "Eg", longitude = 31.0, latitude = 30.0)

        // when
        repository.addFavLocation(newFav)


        val result = repository.getAllFavLocations().first()

        // then
        if (result != null) {
            assertEquals(4, result.size)
        }
        if (result != null) {
            assertEquals(newFav, result.last())
        }
    }

    @Test
    fun `deleteFavLocation should remove location successfully`() = runTest {
        // Given
        val locationToDelete = fakeFavList[0]

        //when
        repository.removeFavLocation(locationToDelete)


        val result = repository.getAllFavLocations().first()

        // then
        if (result != null) {
            assertEquals(2, result.size)
        }
        if (result != null) {
            assert(!result.contains(locationToDelete))
        }
    }



}