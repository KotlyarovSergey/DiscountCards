package com.ksv.discountcards

import android.content.Context
import androidx.room.Room
import com.ksv.discountcards.data.AppDatabase

object MyApp {
    private lateinit var _applicationContext: Context
    val appContext get() = _applicationContext

    fun init(applicationContext: Context) {
        _applicationContext = applicationContext
    }

    private val appDatabase: AppDatabase by lazy{
        Room.databaseBuilder(_applicationContext, AppDatabase::class.java, "database.db")
            .build()
    }

    fun getCardsDao() = appDatabase.getCardsDao()
}