package com.example.birdyapp.features.sign_in.view

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.R
import com.example.birdyapp.Repository
import com.example.birdyapp.databinding.ActivityMainBinding
import com.example.birdyapp.features.sign_in.model.Credentials
import com.example.birdyapp.identity.CredentialsProvider
import com.example.birdyapp.util.ActivitiesUtil
import io.grpc.Channel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class SignInActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val credentialsProvider: CredentialsProvider by instance()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var channel: Channel

    val isLoading = MutableLiveData(false)

    val fields: MutableMap<String, MutableLiveData<String>> = mutableMapOf(
        "email" to MutableLiveData(),
        "password" to MutableLiveData()
    )

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        binding.lifecycleOwner = this
        channel = ActivitiesUtil.initChannel()
        intButtons()
    }

    private fun intButtons() {
        login_button.setOnClickListener {

            ActivitiesUtil.hideKeyboard(this)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(fields["email"]?.value!!).matches()) {
                emailInputLayout.error = resources.getString(R.string.error_invalid_email)
            } else {
                emailInputLayout.error = null
            }
            signIn(
                fields["email"]?.value!!,
                fields["password"]?.value!!
            )
        }
    }

    private fun signIn(email: String, password: String) {
        Repository(channel).loginUser(email, password)
            .doOnSubscribe {
                isLoading.postValue(true)
            }
            .doOnComplete {
                isLoading.postValue(false)
            }
            .subscribe {
                //go to MainActivity
            }
            .addTo(compositeDisposable)
        credentialsProvider.setCredentials(Credentials(email, password))
    }
}