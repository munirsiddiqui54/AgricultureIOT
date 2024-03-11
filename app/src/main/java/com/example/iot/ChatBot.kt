package com.example.iot

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatBot : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)

        findViewById<Button>(R.id.skip).setOnClickListener{
            startActivity(Intent(this,MainActivity2::class.java))
            finish()
        }
        val cropPrediction:Fragment=CropRecommendation()
        val geminiBot:Fragment=GeminiBot()
        val currentData=CurrentData()

        var active:Fragment=currentData
        fun makeActive(f:Fragment){
            active=f
        }
        makeCurrentFragment(currentData)


        findViewById<Button>(R.id.next).setOnClickListener{
            if(active==currentData){
                //naigate to 2nd
                makeCurrentFragment(cropPrediction)
                makeActive(cropPrediction)
            }
            else if(active==cropPrediction){
                //navigate to 3rd
                makeCurrentFragment(geminiBot)
                makeActive(geminiBot)
            }
            else if(active==geminiBot){
                startActivity(Intent(this,MainActivity2::class.java))
                finish()
            }
        }

    }
    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.to_replace,fragment)
            commit()
        }
    }

}