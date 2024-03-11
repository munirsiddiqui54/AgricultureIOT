package com.example.iot

class MyChat (
    private var model: String = "",
        private var sender: String = "",
        private var isModel:Boolean=true
){
//    private var model: String = ""
//    private var sender: String = ""
//    private var isModel:Boolean=true
    // Accessor methods to get and set the properties
    fun getModel(): String {
        return model
    }

    fun setModel(newModel: String) {
        model = newModel
    }

    fun getSender(): String {
        return sender
    }

    fun isModel():Boolean{
        return isModel
    }
    fun setSender(newSender: String) {
        sender = newSender
    }


}