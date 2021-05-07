package com.example.riri.androidApp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.riri.shared.network.RiRiApi
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UploadImageViewModel : ViewModel() {

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private val mainScope = MainScope()
    private val api = RiRiApi()

    private val _status = MutableLiveData<String>()
    val status : LiveData<String>
    get() = _status


    fun retrieveImageResponse() {
        mainScope.launch {
            kotlin.runCatching {
                _status.value = "loading"
                api.getResponse()
            }.onSuccess {
                Log.d("test", it.toString())
                if (it.status == "succeeded") {
                    _status.value = "done"
                } else {
                    _status.value = "loading"
                    delay(2000)
                    retrieveImageResponse()
                }
            }.onFailure {
                _status.value = "failed"
            }
        }
    }
}