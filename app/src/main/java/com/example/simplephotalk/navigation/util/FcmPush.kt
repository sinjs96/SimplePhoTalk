package com.example.simplephotalk.navigation.util

import com.example.simplephotalk.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class FcmPush {
    //var JSON = MediaType.parse("application/json; charset=utf-8")
    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var url = "https://fcm.googleapis.com/fcm/send"

    //API키 그냥 따라썼어요----------------------------------------------------
    var serverKey = "AIzaSyAxYZf9XAFiJ3ehEK9cXZrniIuHsHi2nFU"


    var gson : Gson? = null
    var okHttpClient : OkHttpClient? = null
    companion object{
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }
    fun sendMessage(destinationUid : String, title : String, message : String){
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
                task ->
            if(task.isSuccessful){
                var token = task?.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                //var body = RequestBody.create(JSON,gson?.toJson(pushDTO))
                var body = gson?.toJson(pushDTO)?.let { RequestBody.create(JSON, it) }
                var request = body?.let {
                    Request.Builder()
                        .addHeader("Content-Type","application/json")
                        .addHeader("Authorization","key="+serverKey)
                        .url(url)
                        .post(it)
                        .build()
                }
//                var request = Request.Builder()
//                    .addHeader("Content-Type","application/json")
//                    .addHeader("Authorization","key="+serverKey)
//                    .url(url)
//                    .post(body)
//                    .build()

                if (request != null) {
                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: okio.IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response.body?.string())
                        }

                    })
                }
//                okHttpClient?.newCall(request)?.enqueue(object : Callback{
//                    override fun onFailure(call: Call?, e: java.io.IOException?) {
//
//                    }
//
//                    override fun onResponse(call: Call?, response: Response?) {
//                        println(response?.body()?.string())
//                    }
//
//                })
            }
        }
    }
}