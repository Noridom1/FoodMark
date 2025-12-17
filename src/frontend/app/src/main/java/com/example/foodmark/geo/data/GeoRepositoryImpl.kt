package com.example.foodmark.geo.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.foodmark.geo.domain.model.GeoPoint
import com.example.foodmark.geo.domain.repo.GeoRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GeoRepositoryImpl @Inject constructor(
    private val context: Context
) : GeoRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoPoint? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val loc = fusedLocationClient.lastLocation.await()
        return loc?.let { GeoPoint(it.longitude, it.latitude) }
    }

    override suspend fun getDistance(origin: GeoPoint, destination: GeoPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            origin.lat, origin.lng,
            destination.lat, destination.lng,
            results
        )
        Log.d("GeoRepositoryImpl", "Distance between $origin and $destination is ${results[0] / 1000} km")
        return results[0].toDouble() / 1000
    }
}