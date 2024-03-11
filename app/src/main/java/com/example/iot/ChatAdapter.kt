package com.example.iot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(mContext: Context, mChats:ArrayList<MyChat>)
    : RecyclerView.Adapter<ChatAdapter.ViewHolder?>() {

    private var mContext: Context
    private val mChats:ArrayList<MyChat>
    init {
        this.mContext=mContext
        this.mChats=mChats
    }

    class ViewHolder(itemView: View, context: Context):RecyclerView.ViewHolder(itemView){
        var modelMsg: TextView
        var userMsg:TextView
        init{
            modelMsg=itemView.findViewById(com.example.iot.R.id.model_msg)
            userMsg=itemView.findViewById(com.example.iot.R.id.user_msg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view:View
        if(viewType==1){
            //model Text
            view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false)
        }else{
            view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false)
        }
        return ChatAdapter.ViewHolder(view,mContext)

    }
    override fun getItemCount(): Int {
        return mChats.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myChat:MyChat =mChats[position]
        holder.userMsg.text=myChat!!.getSender()
        holder.modelMsg.text=myChat!!.getModel()
    }

    override fun getItemViewType(position: Int): Int {
        return if(mChats[position].isModel()==true){
            1
        }else{
            0
        }
    }
}