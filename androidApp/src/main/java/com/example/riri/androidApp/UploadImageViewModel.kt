package com.example.riri.androidApp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.riri.shared.entity.Image
import com.example.riri.shared.network.RiRiApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.text.StringBuilder

class UploadImageViewModel : ViewModel() {

    var currentPhotoPath = MutableLiveData<String?>()

    private val storage = Firebase.storage

    private val storageReference = storage.reference

    private val mainScope = MainScope()
    private val api = RiRiApi()

    var image = MutableLiveData<Bitmap>()

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _imageStatus = MutableLiveData<String>()
    val imageStatus: LiveData<String>
        get() = _imageStatus

    private val _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text

    val sb = StringBuilder()

    fun retrieveImageResponse() {
        mainScope.launch {
            kotlin.runCatching {
                _status.value = "loading"
                api.getResponse()
            }.onSuccess {
                Log.d("test", it.toString())
                if (it.status == "succeeded") {
                    _status.value = "done"
                    if (it.analyzeResult != null && it.analyzeResult?.readResults?.get(0)?.lines?.isEmpty()!!) {
                        _text.value = "No text in image"
                    } else {
                        _text.value = extractText(it)
                    }
                    println(_text.value)

                } else {
                    _status.value = "loading"
                    delay(2000)
                    retrieveImageResponse()
                }
            }.onFailure {
                _status.value = "fn"
            }
        }
    }

    fun uploadImage(filePath: Uri?) {
        if (filePath != null) {
            val ref = storageReference.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref.putFile(filePath)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    api.imageUrl = downloadUri.toString()
                    _imageStatus.value = "succeeded"
                    println(api.imageUrl)
                } else {
                    // Handle failures
                    _imageStatus.value = "1failed"
                }
            }.addOnFailureListener {
                //handle failures
                _imageStatus.value = "2failed"
            }
        } else {
            //handle failures
            _imageStatus.value = "3failed"
        }
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath.value = absolutePath
        }
    }

    @Throws(FileNotFoundException::class)
    fun setPic(context: Context, uri: Uri?) {
        // Get the dimensions of the View
        val targetW: Int = 100
        val targetH: Int = 100

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        image.value = BitmapFactory.decodeStream(
            context.contentResolver
                .openInputStream(uri!!), null, bmOptions
        )
    }

    private fun extractText(image: Image): String {
        for (string in image.analyzeResult?.readResults!!) {
            for (line in string.lines) {
                sb.append(line.text)
                sb.append(" ")
            }
        }
        println(sb.toString())
        return sb.toString()
    }

}