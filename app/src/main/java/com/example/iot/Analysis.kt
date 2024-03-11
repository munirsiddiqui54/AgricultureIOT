package com.example.iot

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Analysis.newInstance] factory method to
 * create an instance of this fragment.
 */
class Analysis : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var sensorData: SensorData? = null

    private val baseUrl = "https://croprecommendation-i6pm.onrender.com/"
    private lateinit var apiService: ApiService

    fun setYourObject(s: SensorData?) {
        Log.d("SENSOR",s.toString())
        sensorData = s
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_analysis, container, false)

        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("data")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    sensorData=snapshot.getValue(SensorData::class.java)
                    updateData(sensorData!!,view)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return view
    }

    fun updateData(sensordata: SensorData,view:View){

        val currentTime = Date()
        val formatter = SimpleDateFormat("HH:mm")

        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Format the date as desired (e.g., "YYYY-MM-DD")
        val date="$dayOfMonth/$month/$year"

        // Convert day of week integer to a string representation
        val day= when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of ApiService
        apiService = retrofit.create(ApiService::class.java)

        // Example data to send to the API
        val data = mapOf(
            "n" to sensordata.nitrogen.toString(),
            "p" to sensordata.phosphorus.toString(),
            "k" to sensordata.potassium.toString(),
            "t" to sensordata.temperatureC.toString(),
            "h" to sensordata.humidity.toString(),
            "ph" to sensordata.pH.toString(),
            "r" to sensordata.rainfall.toString()
        )

        apiService.predictCrop(data).enqueue(object : Callback<CropResponse> {
            override fun onResponse(call: Call<CropResponse>, response: Response<CropResponse>) {
                if (response.isSuccessful) {
                    val cropResponse = response.body()
                    cropResponse?.let {
                        // Handle successful response here
                        val crop = it.crop
                        val textView: TextView = view.findViewById<TextView>(R.id.prediction)

                        val fullText = "You should grow ${crop} in your farm."

                        // Create a SpannableString

                        // Create a SpannableString
                        val spannableString = SpannableString(fullText)

                        // Find the start and end index of the word "Papaya"

                        // Find the start and end index of the word "Papaya"
                        val startIndex = fullText.indexOf(crop)
                        val endIndex = startIndex + crop.length

                        // Apply color to the word "Papaya"

                        // Apply color to the word "Papaya"
                        spannableString.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            startIndex,
                            endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Set the text with SpannableString

                        // Set the text with SpannableString
                        textView.text = spannableString
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<CropResponse>, t: Throwable) {
                // Handle network errors
            }
        })



        view.findViewById<TextView>(R.id.v_nitrogen).text=sensordata.nitrogen.toString()

        view.findViewById<TextView>(R.id.v_phosphorous).text=sensordata.phosphorus.toString()

        view.findViewById<TextView>(R.id.v_potassium).text=sensordata.potassium.toString()

        view.findViewById<TextView>(R.id.date).text=date
        view.findViewById<TextView>(R.id.soil_moisture).text=String.format("%.1f", sensordata.soilMoisture)+"%"
        view.findViewById<TextView>(R.id.day).text=day
        view.findViewById<TextView>(R.id.ph).text=String.format("%.3f", sensordata.pH)
        view.findViewById<TextView>(R.id.moisture_percentage).text=String.format("%.1f", sensordata.moisturePercentage)
        view.findViewById<TextView>(R.id.air_quality).text=sensordata.airQuality.toString()
        view.findViewById<TextView>(R.id.water_level).text=sensordata.waterLevel.toString()
        view.findViewById<TextView>(R.id.temperature_c).text=String.format("%.2f", sensordata.temperatureC)+"°C"
        view.findViewById<TextView>(R.id.temperature_f).text=String.format("%.2f", sensordata.temperatureF).toString()+"°F"
        view.findViewById<TextView>(R.id.time).text= formatter.format(currentTime)+"PM"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Analysis.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Analysis().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}