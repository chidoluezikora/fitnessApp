package com.cj1_project.googlesignin

import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*

class GeoLocation(context: Context) {
    private val context: Context = context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var startedLocationTracking = false

    init {
        setupLocationProviderClient()
    }

    private fun setupLocationProviderClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }


    fun startLocationTracking(locationCallback: LocationCallback) {
        if (!startedLocationTracking) {
            //noinspection MissingPermission
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    UPDATE_INTERVAL_MILLISECONDS).setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MILLISECONDS).build(),
                locationCallback,
                Looper.getMainLooper())

            this.locationCallback = locationCallback

            startedLocationTracking = true
        }
    }

    fun stopLocationTracking() {
        if (startedLocationTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    companion object {
        const val UPDATE_INTERVAL_MILLISECONDS: Long = 0
        const val FASTEST_UPDATE_INTERVAL_MILLISECONDS = UPDATE_INTERVAL_MILLISECONDS / 2
    }
}