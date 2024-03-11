package com.example.iot

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }


    private fun askNotificationPermission() {
        // Check if the device's API level is >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Permission has been denied previously, but not permanently. Explain to the user why the permission is needed.
                showRationaleDialog()
            } else {
                // Permission has not been granted yet, and no rationale needs to be shown. Request the permission directly.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Required")
            .setMessage("This app requires permission to show notifications in order to provide you with important updates and alerts.")
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                // User clicked OK, request the permission directly.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                dialog.dismiss()
            }
            .setNegativeButton("No thanks") { dialog: DialogInterface, _: Int ->
                // User clicked No thanks, handle this case. For example, allow the user to continue without notifications.
                // TODO: Inform user that your app will not show notifications.
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private val LANGUAGE_KEY = "language_key"

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var sensorData:SensorData?=null

        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("data")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    sensorData=snapshot.getValue(SensorData::class.java)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        askNotificationPermission()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("TOKEN", token)
        })

        findViewById<CardView>(R.id.languageBtn).setOnClickListener{
            changeLanguage()
        }

        findViewById<Button>(R.id.chatBot).setOnClickListener{
                val i=Intent(this,MainActivity2::class.java)
                i.putExtra("sensor_data",sensorData)
                startActivity(i)
        }
    }



    private fun changeLanguage() {
        var languages = arrayOf("Hindi","English")

        val builder = AlertDialog.Builder(this)

        if(getSavedLocale()=="hi"){
            languages=arrayOf("हिंदी", "अंग्रेज़ी")
            builder.setTitle("भाषा चुनें")
        }else{
            languages=arrayOf("Hindi", "English")
            builder.setTitle("Choose Language")
        }
        builder.setItems(languages) { dialog, which ->
            // Handle language selection here
            val selectedLanguage = languages[which]

            if(selectedLanguage=="Hindi" || selectedLanguage=="हिंदी"){
                setLocal("hi")
            }else{
                setLocal("en")
            }
            // Do something based on the selected language, e.g., switch to Hindi or English


            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun setLocal(s: String) {
        val locale = Locale(s)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save selected language to SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(LANGUAGE_KEY, s).apply()

        recreate()
    }
    fun Context.getSavedLocale(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(LANGUAGE_KEY, "") ?: ""
    }


}