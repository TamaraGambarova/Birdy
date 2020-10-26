package com.example.birdyapp

import android.app.Application
import com.example.birdyapp.db.BirdsDatabase
import com.example.birdyapp.util.ConnectivityInterceptor
import com.example.birdyapp.util.ToastManager
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class App: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@App))

        bind() from provider {ToastManager(instance())}

        //db
        bind() from provider { BirdsDatabase (instance()) }

        bind() from provider { instance<BirdsDatabase>().offlineBirdsInfo() }

        //network
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptor() }
    }

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

    }
}