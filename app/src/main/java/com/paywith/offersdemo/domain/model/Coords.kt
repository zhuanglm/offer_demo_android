package com.paywith.offersdemo.domain.model

import android.location.Location
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlin.math.pow

@Parcelize
data class Coords(
    @SerializedName("lat")
    val latitude: Double = SearchQuery.DEFAULT_LAT,

    @SerializedName("long")
    val longitude: Double = SearchQuery.DEFAULT_LNG
) : Parcelable {

    enum class DistanceType {
        KILOMETERS,
        METERS,
        MILES
    }

    fun isDefault(): Boolean =
        latitude == SearchQuery.DEFAULT_LAT && longitude == SearchQuery.DEFAULT_LNG

    fun getDistance(location: Coords, distanceType: DistanceType): Double =
        getDistance(location.latitude, location.longitude, distanceType)

    fun getDistance(latitude: Double, longitude: Double, distanceType: DistanceType): Double =
        getDistance(this.latitude, this.longitude, latitude, longitude, distanceType)

    companion object {
        private const val METERS_TO_MILES_RATIO = 0.000621371
        private const val EARTH_RADIUS = 6371

        @JvmStatic
        fun fromLocation(location: Location): Coords =
            Coords(location.latitude, location.longitude)

        @JvmStatic
        fun getDistance(location1: Coords, location2: Coords, distanceType: DistanceType): Double =
            getDistance(location1.latitude, location1.longitude, location2.latitude, location2.longitude, distanceType)

        @JvmStatic
        fun getDistance(location: Coords, latitude: Double, longitude: Double, distanceType: DistanceType): Double =
            getDistance(location.latitude, location.longitude, latitude, longitude, distanceType)

        @JvmStatic
        fun getDistance(
            latitude1: Double,
            longitude1: Double,
            latitude2: Double,
            longitude2: Double,
            distanceType: DistanceType
        ): Double {
            val distance = distance(latitude1, latitude2, longitude1, longitude2, 0.0, 0.0)
            return when (distanceType) {
                DistanceType.METERS -> distance
                DistanceType.MILES -> distance * METERS_TO_MILES_RATIO
                DistanceType.KILOMETERS -> distance / 1000.0
            }
        }

        /**
         * Calculate distance between two points in latitude and longitude taking into account height difference.
         * Uses Haversine method.
         * Returns distance in meters.
         */
        private fun distance(
            lat1: Double,
            lat2: Double,
            lon1: Double,
            lon2: Double,
            elevation1: Double,
            elevation2: Double
        ): Double {
            val latDistance = Math.toRadians(lat2 - lat1)
            val lonDistance = Math.toRadians(lon2 - lon1)
            val a = Math.sin(latDistance / 2).pow(2.0) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(lonDistance / 2).pow(2.0)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val distance = EARTH_RADIUS * c * 1000 // meters
            val height = elevation1 - elevation2
            return Math.sqrt(distance * distance + height * height)
        }
    }
}

