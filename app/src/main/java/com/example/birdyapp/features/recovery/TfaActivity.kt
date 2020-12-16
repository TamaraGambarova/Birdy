package com.example.birdyapp.features.recovery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.R
import com.example.birdyapp.databinding.ActivityTfaBinding
import com.example.birdyapp.util.ActivitiesUtil
import com.example.birdyapp.util.ToastManager
import io.grpc.Channel
import kotlinx.android.synthetic.main.activity_tfa.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class TfaActivity: AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var channel: Channel
    private val toastManager: ToastManager by instance()

    val otpCode = MutableLiveData<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityTfaBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_recovery)
        binding.lifecycleOwner = this
        binding.activity = this

        channel = ActivitiesUtil.initChannel()
        initButtons()
    }

    private fun initButtons() {
        continue_button.setOnClickListener {

        }
    }
}