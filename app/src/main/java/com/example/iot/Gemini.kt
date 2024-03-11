package com.example.iot

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Gemini.newInstance] factory method to
 * create an instance of this fragment.
 */
class Gemini : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var chat:Chat?=null
    val list=ArrayList<MyChat>()

    private var sensorData: SensorData? = null

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

        val view=inflater.inflate(R.layout.fragment_gemini, container, false)

        view.findViewById<LinearProgressIndicator>(R.id.loader).visibility=View.VISIBLE

        val adapter=ChatAdapter(requireContext(),list)
        val recyclerView: RecyclerView =view.findViewById(R.id.myRecyclerView)

        val layoutManager= LinearLayoutManager(context)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter

        lifecycleScope.launch {


            try {
                val appInfo = requireContext().packageManager.getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
                val bundle = appInfo.metaData
                val apiKey = bundle.getString("apiKey")
                val generativeModel = GenerativeModel(
                    // Use a model that's applicable for your use case (see "Implement basic use cases" below)
                    modelName = "gemini-pro",
                    // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                    apiKey = apiKey!!
                )

                chat = generativeModel.startChat(
                    history = listOf(
                        content(role = "user") { text("Hey Gemini !") },
                        content(role = "model") { text("Great to meet you. What would you like to know?") }
                    )
                )

                startConvo(sensorData,chat!!,view)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        view.findViewById<ImageView>(R.id.send).setOnClickListener{
            val q=view.findViewById<EditText>(R.id.query)
            send(q.text.toString(),chat!!,view)
            q.setText("")
        }

        return view
    }

    private suspend fun startConvo(sensorData: SensorData?, chat: Chat,view:View) {
if(sensorData!=null){
            val response=chat.sendMessage("Sensor Data of the Crops: ${sensorData.toString()}. Give a Short Analysis in 4 lines. easy to understand")
            val c1:MyChat=MyChat("","Sensor Data of the Crops: ${sensorData.toString()}. Give a Short Analysis in 2-3 words",false)
            Log.d("CHAT",response.text.toString())
            val c2:MyChat=MyChat(response.text.toString(),"",true)

            list.add(c2)
            view.findViewById<RecyclerView>(R.id.myRecyclerView).adapter=ChatAdapter(requireContext(),list)
    view.findViewById<LinearProgressIndicator>(R.id.loader).visibility=View.GONE
        }

    }
    private fun send(q:String,chat: Chat,view:View){


        val c1:MyChat=MyChat("",q.toString(),false)
        list.add(c1)
        view.findViewById<RecyclerView>(R.id.myRecyclerView).adapter=ChatAdapter(requireContext(),list)
        lifecycleScope.launch { // E

            view.findViewById<LinearProgressIndicator>(R.id.loader).visibility=View.VISIBLE
            val response = chat.sendMessage(q)
            Log.d("CHAT", response.text.toString())
            val c2: MyChat = MyChat(response.text.toString(), "", true)
            list.add(c2)

            view.findViewById<LinearProgressIndicator>(R.id.loader).visibility=View.GONE

            view.findViewById<RecyclerView>(R.id.myRecyclerView).adapter=ChatAdapter(requireContext(),list)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Gemini.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Gemini().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}