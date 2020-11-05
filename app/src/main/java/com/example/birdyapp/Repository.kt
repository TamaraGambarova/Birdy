package com.example.birdyapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import birdy_grpc.Birdy
import birdy_grpc.MainEndpointGrpc.newBlockingStub
import birdy_grpc.MainEndpointGrpc.newFutureStub
import com.google.protobuf.ByteString

import io.grpc.Channel
import io.reactivex.Completable
import io.reactivex.rxkotlin.toSingle
import java.text.SimpleDateFormat
import java.util.*

class Repository(private val channel: Channel) {

       //TODO:: not implemented yet
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
    @RequiresApi(Build.VERSION_CODES.N)
    fun findBirdByName(name: String) {
        val blockingStub = newBlockingStub(channel)
        val findBirdRequest = Birdy.FindBirdByNameRequest
            .newBuilder()
            .setName("вор")
            .build()
        val matchedBirds: Iterator<Birdy.FindBirdByNameResponse> =
            blockingStub.findBirdByName(findBirdRequest)

        try {
            matchedBirds.forEachRemaining {
                Log.d("ptenchick", it.encInfo.description)
                Log.d("ptenchick", it.encInfo.name)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBirdLocation(photo: ByteString, lat: Double, long: Double) {
        val blockingStub = newBlockingStub(channel)
        val setLocationRequest =
            Birdy.AddBirdWithDataRequest.newBuilder()
                .setPhoto(photo)
                .setInfo(
                    Birdy.UserBirdInfo.newBuilder()
                        .setFoundPoint(
                            Birdy.UserBirdInfo.Point.newBuilder()
                                .setLatitude(lat)
                                .setLongitude(long)
                                .build()
                        )
                        .setFinderEmail("test@gmail.com")
                        .setFoundTime(Calendar.getInstance().time.toString())
                ).build()

        val response =
            blockingStub.addBirdWithData(setLocationRequest).toBuilder().build()
        Log.d("test-loc", response.birdName)
    }

    fun loginUser(email: String, password: String): Completable {
        val blockingStub = newBlockingStub(channel)
        val loginRequest = Birdy.LoginRequest.newBuilder()
            .setEmail(email)
            .setPassword(password)
            .build()

        val response = blockingStub.loginUser(loginRequest)

        return response.result.toSingle().ignoreElement()
    }
}