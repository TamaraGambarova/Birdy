package com.example.birdyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.birdyapp.databinding.ActivityMainBinding
import com.example.birdyapp.features.messages.MessagesFragment
import com.example.birdyapp.features.searching_by_name.view.OfflineFragment
import com.example.birdyapp.features.searching_by_name.view.SearchBirdByNameFragment
import com.example.birdyapp.features.top.TopFragment
import com.example.birdyapp.util.ActivitiesUtil.initChannel
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var channel: Channel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        channel = initChannel()

        initBottomNavBar()


        /*  registerBtn.setOnClickListener {
              GlobalScope.launch(Dispatchers.IO) {
                  Repository(channel).registerUser()
              }
          }*/
    }

    private fun initBottomNavBar() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            val selectedFragment: Fragment = when (it.itemId) {
                R.id.find_bird -> {
                    SearchBirdByNameFragment.getInstance(channel)
                }
                R.id.top -> {
                    TopFragment.getInstance()
                }
                R.id.messages -> {
                    MessagesFragment.getInstance()
                }
                R.id.profile -> {
                    OfflineFragment.getInstance(channel)
                }
                else -> return@setOnNavigationItemSelectedListener false
            }

            //this.onBackPressedListener = selectedFragment

            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment).commit()

            return@setOnNavigationItemSelectedListener true
        }
        binding.bottomNavigation.selectedItemId = R.id.find_bird
    }



    override fun onDestroy() {
        super.onDestroy()
        (channel as ManagedChannel?)?.shutdownNow()
    }

}