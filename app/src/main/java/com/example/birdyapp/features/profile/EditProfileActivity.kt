package com.example.birdyapp.features.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.R
import com.example.birdyapp.databinding.ActivityEditProfileBinding
import com.example.birdyapp.features.sign_in.model.Credentials
import com.example.birdyapp.features.sign_up.model.UserFields
import com.example.birdyapp.identity.CredentialsProvider
import com.example.birdyapp.identity.KycProvider
import com.example.birdyapp.util.ActivitiesUtil
import com.example.birdyapp.util.ToastManager
import io.grpc.Channel
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class EditProfileActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()
    private val credentialsProvider: CredentialsProvider by instance()
    private val kycProvider: KycProvider by instance()
    private lateinit var channel: Channel
    private val toastManager: ToastManager by instance()

    val isLoading = MutableLiveData(false)
    val userForm = UserFields()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityEditProfileBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding.lifecycleOwner = this
        binding.activity = this
        channel = ActivitiesUtil.initChannel()
        initToolbar()
        initFields()
        initButtons()
    }

    private fun initToolbar() {
        toolbar.title_text_view.text = getString(R.string.edit_profile)
    }

    private fun initFields() {
        val activeKyc = kycProvider.getKyc()
        userForm.firstName.value = activeKyc?.firstName?.value!!
        userForm.lastName.value = activeKyc.lastName.value!!
        userForm.middleName.value = activeKyc.middleName.value!!
        userForm.birthdayDate.value = activeKyc.birthdayDate.value!!
        userForm.city.value = activeKyc.city.value!!

        emailInputLayout.editText?.setText(credentialsProvider.getCredentials()?.email)
    }

    private fun initButtons() {
        save_button.setOnClickListener {
            if (userForm.validate()) {
                update()
            } else {
                toastManager.long(R.string.empty_fields)
            }
        }
    }

    private fun update() {
       /* Repository(channel).updateUserInfo(userForm)
            .compose(ObservableTransformers.defaultSchedulersCompletable())
            .doOnSubscribe {
                isLoading.postValue(true)
            }
            .subscribeBy(
                onComplete = {
                    toastManager.short("Data has been updated")
                }, onError = {
                    it.printStackTrace()
                }
            )*/

        val oldCreds = credentialsProvider.getCredentials()
        oldCreds?.let {
            credentialsProvider.setCredentials(Credentials(it.password, emailInputLayout.editText?.text.toString()))
            kycProvider.setKyc(userForm)
        }
    }
}