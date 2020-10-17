package com.example.birdyapp.features.searching_by_name.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.databinding.FragmentFindBirdByNameBinding

class SearchBirdByNameFragment : Fragment() {

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

    companion object{
        fun getInstance() = SearchBirdByNameFragment()
    }

}