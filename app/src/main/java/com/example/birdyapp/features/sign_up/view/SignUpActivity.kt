package com.example.birdyapp.features.sign_up.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.MainActivity
import com.example.birdyapp.R
import com.example.birdyapp.Repository
import com.example.birdyapp.databinding.ActivitySignUpBinding
import com.example.birdyapp.extensions.makeLinks
import com.example.birdyapp.features.sign_in.view.SignInActivity
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
import java.util.*

class SignUpActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var channel: Channel
    private val toastManager: ToastManager by instance()

    val isLoading = MutableLiveData(false)
    val userForm = UserFields()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySignUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.lifecycleOwner = this
        binding.activity = this
        channel = ActivitiesUtil.initChannel()
        initFields()
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

    private fun initFields() {
        already_have_account_text_view.makeLinks(
            Pair("Log in!", View.OnClickListener {
                startActivity(
                    Intent(
                        this,
                        SignInActivity::class.java
                    )
                )
            })
        )
        birthDayDateInputLayout.setEndIconOnClickListener {
            val c = Calendar.getInstance()
            c.roll(Calendar.YEAR, -18)
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                this,
                { _, chosenYear, monthOfYear, dayOfMonth ->
                    val date = GregorianCalendar(chosenYear, monthOfYear, dayOfMonth).time
                    userForm.birthdayDate.value = date
                },
                year,
                month,
                day
            )
            dpd.datePicker.maxDate = c.timeInMillis
            dpd.show()
        }
    }

    private fun signUp() {
        Log.d("email", userForm.email.value!!.trim())
        Log.d("password", userForm.password.value!!.trim())

        Repository(channel).registerUser(
            email = userForm.email.value!!,
            password = userForm.password.value!!,
            user = userForm
        )
            .compose(ObservableTransformers.defaultSchedulersSingle())
            .doOnSubscribe {
                progress.visibility = View.VISIBLE
            }
            .subscribe({
                Log.d("res--", it.number.toString())
                if(it.number == 0) {
                    goToMainActivity()
                }
            }, {
                toastManager.short("Error!")
            })

            .addTo(compositeDisposable)
    }

    private fun goToMainActivity() {
        startActivity( Intent(
            this,
            MainActivity::class.java
        ))
    }
}