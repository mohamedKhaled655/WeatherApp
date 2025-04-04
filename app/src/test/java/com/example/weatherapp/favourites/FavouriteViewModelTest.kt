package com.example.weatherapp.favourites

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.repo.DummyWeatherRepo
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.utils.Response
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest {
    lateinit var favouriteViewModel: FavouriteViewModel
    lateinit var repo: WeatherRepository
    val fakeFavList = listOf(
        FavoriteLocationEntity( name = "Cairo", country = "Eg", longitude = 31.0, latitude = 30.0),
        FavoriteLocationEntity( name = "Alex", country = "Eg", longitude = 31.0, latitude = 30.0),
    )
    @Before
    fun setup(){

        //repo=DummyWeatherRepo()
        repo= mockk(relaxed = true)
        coEvery { repo.getAllFavLocations() } returns flowOf(fakeFavList)
        favouriteViewModel=FavouriteViewModel(repo)
    }


    @Test
    fun `addLocationToFav returns success message when location is added`() = runTest {
        // Given: A favorite location entity to add
        val locationToAdd = FavoriteLocationEntity(
            country = "Egypt",
            name = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357
        )


        coEvery { repo.addFavLocation(locationToAdd) } returns 1

        // When:
        favouriteViewModel.addLocationToFav(locationToAdd)

        // Then:
        assertEquals("Added Location Successfully", favouriteViewModel.message.value)
    }


    @Test
    fun `addLocationToFav returns error message when location cannot be added`() = runTest {
        // Given:
        val locationToAdd = FavoriteLocationEntity(
            country = "Egypt",
            name = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357
        )

        // Mock the repository method to return a negative number (failure)
        coEvery { repo.addFavLocation(locationToAdd) } returns 0

        // When:
        favouriteViewModel.addLocationToFav(locationToAdd)

        // Then:
        assertEquals("this Location is not in favorites", favouriteViewModel.message.value)
    }


    @Test
    fun `addLocationToFav returns error message when location data is null`() = runTest {
        // Given: A null location
        val locationToAdd: FavoriteLocationEntity? = null

        // When:
        favouriteViewModel.addLocationToFav(locationToAdd)

        // Then:
        assertEquals("Could not add Location, missing data", favouriteViewModel.message.value)
    }


    /////////////

    @Test
    fun `removeLocationFromFav returns success message when location is removed`() = runTest {
        // Given:
        val locationToRemove = FavoriteLocationEntity(
            country = "Egypt",
            name = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357
        )


        coEvery { repo.removeFavLocation(locationToRemove) } returns 1

        // When:
        favouriteViewModel.removeLocationFromFav(locationToRemove)

        // Then:
        assertEquals("Removed Location Successfully", favouriteViewModel.message.value)

    }



}