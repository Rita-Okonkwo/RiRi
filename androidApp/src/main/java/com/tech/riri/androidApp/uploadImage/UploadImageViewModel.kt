package com.tech.riri.androidApp.uploadImage

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tech.riri.androidApp.BuildConfig
import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.data.TextObjectRepository
import com.tech.riri.shared.data.local.TextSqlDelightDatabase
import com.tech.riri.shared.data.remote.RiRiApi
import com.tech.riri.shared.entity.Image
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.util.*

class UploadImageViewModel(application: Application) : AndroidViewModel(application) {

    var currentPhotoPath = MutableLiveData<String?>()

    private val storage = Firebase.storage

    private val storageReference = storage.reference

    private val api = RiRiApi()


    private val textObjectRepository =
        TextObjectRepository(TextSqlDelightDatabase(TextObjectDatabaseDriverFactory(application.applicationContext)))

    var image = MutableLiveData<Bitmap>()

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _imageStatus = MutableLiveData<String>()
    val imageStatus: LiveData<String>
        get() = _imageStatus

    private val _text = MutableLiveData<String?>()
    val text: LiveData<String?>
        get() = _text

    val sb = StringBuilder()

    private var tempImageRef: StorageReference? = null


    fun retrieveImageResponse() {
        viewModelScope.launch {
            kotlin.runCatching {
                _status.value = "loading"
                api.getResponse(BuildConfig.API_KEY, BuildConfig.IMAGE_ENDPOINT, BuildConfig.CONTENT_TYPE)
            }.onSuccess {
                Log.d("test", it.toString())
                if (it.status == "succeeded") {
                    _status.value = "done"
                    if (it.analyzeResult != null && it.analyzeResult?.readResults?.get(0)?.lines?.isEmpty()!!) {
                        _text.value = "No text found in image"
                    } else {
                        _text.value = extractText(it)
                        tempImageRef?.delete()
                    }
                    println(_text.value)

                } else {
                    _status.value = "loading"
                    delay(2000)
                    retrieveImageResponse()
                }
            }.onFailure {
                _status.value = "fn"
                println(it.message)
                Log.d("azure", it.message.toString())
            }
        }

    }

    fun uploadImgUrl(url: String) {
        viewModelScope.launch {
            uploadImageUrl(url)
        }
    }

    suspend fun uploadImageUrl(url: String) {
        val imageUrl = URL(url)
        val bmp = decodeBmp(imageUrl)
        println("url:" + bmp?.byteCount)
        api.imageUrl = url
        _imageStatus.value = "succeeded"
    }

    suspend fun decodeBmp(url: URL) =
        withContext(Dispatchers.IO) {
            return@withContext try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e : IOException) {
                Log.d("bmp", e.toString())
                null
            }
        }

    fun uploadImage(filePath: Uri?) {
        if (filePath != null) {
            val ref = storageReference.child("uploads/" + UUID.randomUUID().toString())
            tempImageRef = ref
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
                    _imageStatus.value = "failed"
                }
            }.addOnFailureListener {
                //handle failures
                _imageStatus.value = "failed"
            }
        } else {
            //handle failures
            _imageStatus.value = "nullfailed"
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

    fun saveText(audioText: String) {
        if (audioText.isNotEmpty()) {
            textObjectRepository.addText(audioText)
        }
    }

}