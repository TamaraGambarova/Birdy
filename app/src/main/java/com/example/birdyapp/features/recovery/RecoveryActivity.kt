package com.example.birdyapp.features.recovery

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class RecoveryActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    val fields: MutableMap<String, MutableLiveData<String>> = mutableMapOf(
        "email" to MutableLiveData(),
        "password" to MutableLiveData()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


}