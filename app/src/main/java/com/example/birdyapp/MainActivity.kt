package com.example.birdyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var channel : Channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        channel = ManagedChannelBuilder
            .forAddress("178.150.141.36", 1488)
            .usePlaintext()
            .build()

        registerBtn.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                Repository().registerUser(channel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (channel as ManagedChannel?)?.shutdownNow()
    }

}