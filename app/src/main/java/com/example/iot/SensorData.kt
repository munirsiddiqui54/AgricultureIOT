package com.example.iot

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDate

class SensorData(): Serializable {
    var soilMoisture: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    override fun toString(): String {
        return "SensorData(soilMoisture=$soilMoisture, " +
                "moisturePercentage=$moisturePercentage, " +
                "temperatureC=$temperatureC, " +
                "temperatureF=$temperatureF, " +
                "pH=$pH, " +
                "airQuality=$airQuality, " +
                "waterLevel=$waterLevel, "
    }
    var moisturePercentage: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var temperatureC: Float = 0.0f
        get() = field
        set(value) {
            field = value

        }

    var nitrogen: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }
    var rainfall: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var humidity: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var phosphorus: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var potassium: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var temperatureF: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var pH: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var airQuality: Int = 0
        get() = field
        set(value) {
            field = value
        }

    var waterLevel: Float = 0.0f
        get() = field
        set(value) {
            field = value
        }

    var time: String = "12:00"
        get() = field
        set(value) {
            field = value
        }

    var date: String = "00/00"
        get() = field
        set(value) {
            field = value
        }

    var day: String = "Day"
        get() = field
        set(value) {
            field = value
        }


}
