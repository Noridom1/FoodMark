package com.example.foodmark.foodtour.presentation

import android.R.id.message
import android.util.Log
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodmark.auth.domain.use_cases.GetLoggedInUserUseCase
import com.example.foodmark.core.domain.model.RepoResult
import com.example.foodmark.foodtour.domain.model.DestinationNode
import com.example.foodmark.foodtour.domain.model.RouteRecommendation
import com.example.foodmark.foodtour.domain.usecase.FoodTourDirectionUseCase
import com.example.foodmark.foodtour.domain.usecase.FoodTourRecommendUseCase
import com.example.foodmark.geo.domain.model.GeoPoint
import com.example.foodmark.geo.domain.usecase.GetCurrentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodTourViewModel @Inject constructor(
    private val getLoggedInUserUseCase: GetLoggedInUserUseCase,
    private val foodTourRecommendUseCase: FoodTourRecommendUseCase,
    private val getTourDirectionUseCase: FoodTourDirectionUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodTourUIState())
    val uiState: StateFlow<FoodTourUIState> = _uiState.asStateFlow()

    private val _location = MutableStateFlow<GeoPoint?>(null)
    val location: StateFlow<GeoPoint?> = _location

    fun loadCurrentLocation() {
        viewModelScope.launch {
            _location.value = getCurrentLocationUseCase()
        }
    }

    fun getFoodTour(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRecommending = true, error = null) }

            try {
                val user = getLoggedInUserUseCase()
                if (user == null) {
                    _uiState.update {
                        it.copy(
                            isRecommending = false,
                            error = "User not logged in"
                        )
                    }
                    Log.d("FoodTourViewModel", "User not logged in")
                    return@launch

                }

                val userId = user.id

                val foodTourResult = foodTourRecommendUseCase(userId.toString(), lat, lng)

                if (foodTourResult is RepoResult.Success) {
                    _uiState.update {
                        it.copy(
                            isRecommending = false,
                            routeRecommendation = foodTourResult.data,
                            error = null
                        )
                    }
                    // Fetch route path after recommendations
                    getTourDirection(lat, lng)
                } else if (foodTourResult is RepoResult.Error) {
                    _uiState.update {
                        it.copy(
                            isRecommending = false,
                            error = foodTourResult.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isRecommending = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    suspend fun getTourDirection(startLat: Double, startLng: Double) {
        val recommendation = _uiState.value.routeRecommendation ?: return

        val destinationNodes = buildList {
            add(DestinationNode(startLat, startLng)) // start point
            recommendation.route.forEach { store ->
                add(DestinationNode(store.lat, store.lng))
            }
        }

        try {
            val result = getTourDirectionUseCase(destinationNodes)
            when (result) {
                is RepoResult.Success -> {
                    _uiState.update { it.copy(routePath = result.data) }
                }
                is RepoResult.Error -> {
                    Log.e("FoodTourViewModel", "Failed to get directions: ${result.message}")
                    _uiState.update { it.copy(routePath = emptyList()) }
                }
            }
        } catch (e: Exception) {
            Log.e("FoodTourViewModel", "Exception getting directions", e)
            _uiState.update { it.copy(routePath = emptyList()) }
        }
    }



    fun clearRecommendations() {
        _uiState.update { it.copy(routeRecommendation = null, error = null) }
    }

}