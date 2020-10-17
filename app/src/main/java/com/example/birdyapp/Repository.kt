package com.example.birdyapp

import android.util.Log
import birdy_grpc_.Birdy
import birdy_grpc_.MainEndpointGrpc.newBlockingStub
import io.grpc.Channel
import java.text.SimpleDateFormat
import java.util.*

class Repository(private val channel: Channel) {

    fun registerUser() {

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

    fun findBirdByName(name: String) {
        val blockingStub = newBlockingStub(channel)

        val findBirdRequest = Birdy.FindBirdRequest.newBuilder().setName(name).build()
        val response = blockingStub.findBird(findBirdRequest)
        val res = response.res
        Log.d("result", res.name)
    }
}