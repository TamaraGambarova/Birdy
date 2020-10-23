package com.example.birdyapp.features.searching_by_name.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.Repository
import com.example.birdyapp.databinding.FragmentFindBirdByNameBinding
import io.grpc.Channel
import kotlinx.android.synthetic.main.fragment_find_bird_by_name.*

class SearchBirdByNameFragment(val channel: Channel) : Fragment() {

    val birdName = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentFindBirdByNameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initButtons()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initButtons(){
        searchBtn.setOnClickListener {
            Log.d("FINDBIRD", "search started!")
            Repository(channel).findBirdByName("вор")
            Log.d("FINDBIRD", "ready!")

        }
    }

    companion object{
        fun getInstance(channel: Channel) = SearchBirdByNameFragment(channel)
    }

}