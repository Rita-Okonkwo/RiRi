package com.example.riri.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.riri.shared.network.RiRiApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainScope = MainScope()
    private val api = RiRiApi()
    private val tv: TextView by lazy {
        findViewById(R.id.text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retrieveImageResponse()

    }

    fun retrieveImageResponse() {
        mainScope.launch {
            kotlin.runCatching {
                api.getResponse()
            }.onSuccess {
                Log.d("test", it.toString())
                if (it.status == "succeeded") {
                    tv.text = it.status
                } else {
                    delay(2000)
                    retrieveImageResponse()
                }
            }.onFailure {
                Log.e("message", it.message!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
