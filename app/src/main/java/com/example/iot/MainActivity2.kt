package com.example.iot

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity2 : AppCompatActivity() {


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val gemini=Gemini()
        val analysis=Analysis()
        val home=Home()
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("data")



        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val sensorData=snapshot.getValue(SensorData::class.java)
                    gemini.setYourObject(sensorData)
                    analysis.setYourObject(sensorData)
                    home.setYourObject(sensorData)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        val navbar=findViewById<BottomNavigationView>(R.id.btmnav)
        navbar.background=null
        navbar.selectedItemId=R.id.home

        makeCurrentFragment(home)

        navbar.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home->makeCurrentFragment(home)
                R.id.analysis->makeCurrentFragment(analysis)
                R.id.gemini->makeCurrentFragment(gemini)
            }
            true
        }
        askNotificationPermission()


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("TOKEN", token)
        })





    }
    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.wrapper,fragment)
            commit()
        }
    }

}