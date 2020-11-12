package com.example.birdyapp.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.databinding.FragmentOfflineBirdsBinding
import com.example.birdyapp.databinding.FragmentProfileBinding
import com.example.birdyapp.features.sign_up.model.UserFields
import com.example.birdyapp.identity.CredentialsProvider
import com.example.birdyapp.util.ScopedFragment
import com.example.birdyapp.util.ToastManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val toastManager: ToastManager by instance()
    private val credentialsProvider: CredentialsProvider by instance()

    val userForm = MutableLiveData<UserFields>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
    }

    private fun getUserData() {
        //userForm.value.firstName = credentialsProvider.getCredentials().
    }

    companion object {
        fun getInstance() = ProfileFragment()
    }
}