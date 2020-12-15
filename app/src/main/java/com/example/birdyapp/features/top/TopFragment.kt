package com.example.birdyapp.features.top

import com.example.birdyapp.util.ScopedFragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

class TopFragment : ScopedFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    companion object {
        fun getInstance() = TopFragment()
    }
}