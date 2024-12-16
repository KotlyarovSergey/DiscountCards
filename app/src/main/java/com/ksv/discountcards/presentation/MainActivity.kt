package com.ksv.discountcards.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ksv.discountcards.MyApp
import com.ksv.discountcards.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApp.init(applicationContext)
    }
}