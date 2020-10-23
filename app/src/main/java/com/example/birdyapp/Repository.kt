package com.example.birdyapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import birdy_grpc.Birdy
import birdy_grpc.MainEndpointGrpc.newBlockingStub

import io.grpc.Channel

class Repository(private val channel: Channel) {

    /*   fun registerUser() {

           val blockingStub = newBlockingStub(channel)

           val request = Birdy.RegistrationRequest.newBuilder()
               .setEmail("test@gmail.com")
               .setPassword("123")
               .setBirthDate(
                   SimpleDateFormat(
                       "dd-MMM-yyyy",
                       Locale.ENGLISH
                   ).format(Calendar.getInstance().time)
               )
               .build()

           val response = blockingStub.registerUser(request)

           val res = response.result

           Log.d("result", res.name)
       }
   */
    @RequiresApi(Build.VERSION_CODES.N)
    fun findBirdByName(name: String) {
        val blockingStub = newBlockingStub(channel)
        val findBirdRequest = Birdy.FindBirdRequest.newBuilder().setName("вор").build()
        val matchedBirds: Iterator<Birdy.FindBirdResponse> = blockingStub.findBird(findBirdRequest)

        try{
            matchedBirds.forEachRemaining {
                Log.d("ptenchick", it.encInfo.description)
                Log.d("ptenchick", it.encInfo.name)
            }
        } catch(e: Exception){
            e.printStackTrace()
        }


    }
}