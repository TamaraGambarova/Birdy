package com.example.birdyapp.features.sign_up.view

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.MainActivity
import com.example.birdyapp.R
import com.example.birdyapp.Repository
import com.example.birdyapp.databinding.ActivitySignUpBinding
import com.example.birdyapp.features.sign_up.model.UserFields
import com.example.birdyapp.util.ActivitiesUtil
import com.example.birdyapp.util.ObservableTransformers
import com.example.birdyapp.util.ToastManager
import io.grpc.Channel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class SignUpActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var channel: Channel
    private val toastManager: ToastManager by instance()

    val isLoading = MutableLiveData(false)
    val userForm = UserFields()
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.lifecycleOwner = this
        channel = ActivitiesUtil.initChannel()
        initButtons()
    }

    private fun initButtons() {
        sign_up_button.setOnClickListener {
            if (userForm.validate()) {
                signUp()
            } else {
                toastManager.short(R.string.empty_fields)
            }
        }
    }

    private fun signUp() {
        Repository(channel).registerUser(
            email = userForm.email.value!!,
            password = userForm.password.value!!,
            user = userForm
        )
            .compose(ObservableTransformers.defaultSchedulersCompletable())
            .doOnSubscribe {
                isLoading.postValue(true)
            }
            .doOnEvent {
                isLoading.postValue(false)
            }
            .subscribe {
                goToMainActivity()
            }
            .addTo(compositeDisposable)
    }

    private fun goToMainActivity() {
        Intent(
            this,
            MainActivity::class.java
        )
    }
}