package com.example.birdyapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import birdy_grpc.Birdy
import birdy_grpc.MainEndpointGrpc.newBlockingStub
import com.example.birdyapp.features.searching_by_name.model.BirdModel
import com.example.birdyapp.features.sign_up.model.UserFields
import com.google.protobuf.ByteString
import io.grpc.Channel
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import java.util.*

class Repository(private val channel: Channel) {


    @RequiresApi(Build.VERSION_CODES.N)
    fun findBirdByName(name: String): Single<List<BirdModel>> {
        val blockingStub = newBlockingStub(channel)
        val findBirdRequest = Birdy.FindBirdByNameRequest
            .newBuilder()
            .setName(name)
            .build()
        val birds: Iterator<Birdy.FindBirdByNameResponse> =
            blockingStub.findBirdByName(findBirdRequest)

        val matchedBirds = mutableListOf<BirdModel>()
        birds.forEachRemaining {
            matchedBirds.add(
                BirdModel(
                    it.encInfo.name,
                    it.encInfo.description,
                    it.encInfo.photo.toByteArray()
                )
            )
            Log.d("ptenchick", it.encInfo.photo.toByteArray().size.toString())
            Log.d("ptenchick", it.encInfo.name)
        }
        return matchedBirds.toSingle()
    }

    fun setBirdLocation(photo: ByteString, lat: Double, long: Double, finder: String) {
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
                        .setFinderEmail(finder)
                        .setFoundTime(Calendar.getInstance().time.toString())
                ).build()

        val response =
            blockingStub.addBirdWithData(setLocationRequest).toBuilder().build()
        Log.d("test-loc", response.birdName)
    }

    fun loginUser(email: String, password: String): Single<Pair<Birdy.LoginResponse.Result, UserFields>>  {
        return try {
            val blockingStub = newBlockingStub(channel)
            val loginRequest = Birdy.LoginRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build()

            val response = blockingStub.loginUser(loginRequest)

            val user = UserFields(
                response.firstName,
                response.lastName,
                response.middleName,
                Date(),
                response.city
            )

            (response.result to user).toSingle()
            //response.result.toSingle()
        } catch (e: Exception) {
            e.printStackTrace()
            Single.error(e)
        }
    }

    fun registerUser(email: String, password: String, user: UserFields): Single<Birdy.RegistrationResponse.Result> {
        try {
            val blockingStub = newBlockingStub(channel)

            val request = Birdy.RegistrationRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .setFirstName(user.firstName.value)
                .setLastName(user.lastName.value)
                .setMiddleName(user.middleName.value)
                .setCity(user.city.value)
                .setBirthDate(user.birthdayDate.value.toString())
                .build()

            val response = blockingStub.registerUser(request)
            return response.result.toSingle()

        } catch (e: Exception) {
            e.printStackTrace()
            return Single.error(e)
        }
    }
}