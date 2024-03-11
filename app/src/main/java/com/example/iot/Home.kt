package com.example.iot

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var imageUri: Uri?=null
    private var sensorData: SensorData? = null

    fun setYourObject(s: SensorData?) {
        Log.d("SENSOR",s.toString())
        sensorData = s
    }

    private val baseUrl = "https://croprecommendation-i6pm.onrender.com/"
    private lateinit var apiService: ApiService

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
        val view=inflater.inflate(R.layout.fragment_home, container, false)

        val image=view.findViewById<ImageView>(R.id.addphoto)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of ApiService
        apiService = retrofit.create(ApiService::class.java)

        // Example data to send to the API
        val data = mapOf(
            "n" to sensorData?.nitrogen.toString(),
            "p" to sensorData?.phosphorus.toString(),
            "k" to sensorData?.potassium.toString(),
            "t" to sensorData?.temperatureC.toString(),
            "h" to sensorData?.humidity.toString(),
            "ph" to sensorData?.pH.toString(),
            "r" to sensorData?.rainfall.toString()
        )



        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode== Activity.RESULT_OK) {
//                result.resultCode
                // There are no request codes
                val data: Intent? = result.data
                imageUri=data!!.data
                Picasso.get().load(imageUri).into(image)
            }
        }

        image.setOnClickListener{
            val intent= Intent()
            intent.type="image/*"
            intent.action= Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }
        view.findViewById<Button>(R.id.predict_crop_btn).setOnClickListener{
            view.findViewById<CircularProgressIndicator>(R.id.crop_loader).visibility=View.VISIBLE
            apiService.predictCrop(data).enqueue(object : retrofit2.Callback<CropResponse> {
                override fun onResponse(call: retrofit2.Call<CropResponse>, response: retrofit2.Response<CropResponse>) {
                    if (response.isSuccessful) {
                        val cropResponse = response.body()
                        cropResponse?.let {
                            // Handle successful response here
                            val crop = it.crop
                            Toast.makeText(context,crop+" is",Toast.LENGTH_LONG).show()
                            Toast.makeText(context,crop.toString(), Toast.LENGTH_LONG).show()

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

                            textView.visibility=View.VISIBLE
                            view.findViewById<CircularProgressIndicator>(R.id.crop_loader).visibility=View.GONE
                        }
                    } else {
                        // Handle unsuccessful response
                    }
                }

                override fun onFailure(call: retrofit2.Call<CropResponse>, t: Throwable) {
                    // Handle network errors
                }
            })

        }

        view.findViewById<Button>(R.id.detect).setOnClickListener{
            view.findViewById<CircularProgressIndicator>(R.id.disease_loader).visibility=View.VISIBLE
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
// Convert Bitmap to Base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            val encodedImage: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

// Make HTTP POST request to Flask API
            val url = "http://127.0.0.1:3000/"
            val requestBody = FormBody.Builder()
                .add("image", encodedImage)
                .build()

            val request = Request.Builder()
                .url(url+"/disease-predict")
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle failure
                }

                override fun onResponse(call: Call, response: Response) {
                    Toast.makeText(context,"Responded success",Toast.LENGTH_SHORT).show()
                    view.findViewById<CircularProgressIndicator>(R.id.disease_loader).visibility=View.GONE

                }
            })
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}