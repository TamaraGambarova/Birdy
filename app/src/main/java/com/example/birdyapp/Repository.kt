package com.example.birdyapp

import android.util.Log
import birdy_grpc_.Birdy
import birdy_grpc_.MainEndpointGrpc.newBlockingStub
import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.*

class Repository {

    fun registerUser(channel: Channel) {

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
}