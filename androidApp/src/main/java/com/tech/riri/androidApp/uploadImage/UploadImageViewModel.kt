package com.tech.riri.androidApp.uploadImage

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.tech.riri.androidApp.BuildConfig
import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.data.TextObjectRepository
import com.tech.riri.shared.data.local.TextObjectLocalDataSource
import com.tech.riri.shared.data.remote.TextObjectRemoteDataSource
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

class UploadImageViewModel(private val textObjectRepository: TextObjectRepository, private val coroutineDispatcher: CoroutineDispatcher) : ViewModel() {

    var currentPhotoPath = MutableLiveData<String?>()


    var image = MutableLiveData<Bitmap>()

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _imageStatus = MutableLiveData<String?>()
    val imageStatus: LiveData<String?>
        get() = _imageStatus

    private val _text = MutableLiveData<String?>()
    val text: LiveData<String?>
        get() = _text

    val sb = StringBuilder()

    private var tempImageRef: StorageReference? = null


    fun retrieveImageResponse() {
        viewModelScope.launch {
            kotlin.runCatching {
                _status.postValue("loading")
                textObjectRepository.getResponse(BuildConfig.API_KEY, BuildConfig.IMAGE_ENDPOINT, BuildConfig.CONTENT_TYPE)
            }.onSuccess {
                Log.d("test", it.toString())
                if (it.status == "succeeded") {
                    _status.postValue("done")
                    if (it.analyzeResult != null && it.analyzeResult?.readResults?.get(0)?.lines?.isEmpty()!!) {
                        _text.postValue("No text found in image")
                    } else {
                        _text.postValue(extractText(it))
                        tempImageRef?.delete()
                    }
                    println(_text.value)

                } else {
                    _status.postValue("loading")
                    delay(2000)
                    retrieveImageResponse()
                }
            }.onFailure {
                _status.postValue("fn")
                println(it.message)
                Log.d("azure", it.message.toString())
            }
        }

    }

    fun uploadImgUrl(url: String) {
        if (url.isEmpty()) {
            _imageStatus.value = null
            return
        }
        viewModelScope.launch {
            uploadImageUrl(url)
        }
    }

    private suspend fun uploadImageUrl(url: String) {
        val imageUrl = URL(url)
        val bmp = decodeBmp(imageUrl)
        println("url:" + bmp?.byteCount)
        textObjectRepository.changeUrl(url)
        _imageStatus.value = "succeeded"
  }

    private suspend fun decodeBmp(url: URL) =
        withContext(coroutineDispatcher) {
            try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e : IOException) {
                Log.d("bmp", e.toString())
                null
            }
        }

    fun uploadImage(filePath: Uri?) {
        val storage = Firebase.storage

        val storageReference = storage.reference
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
                    textObjectRepository.changeUrl(downloadUri.toString())
                    _imageStatus.value = "succeeded"
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
        viewModelScope.launch {
            if (audioText.isNotEmpty()) {
                textObjectRepository.addText(audioText)
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class UploadImageViewModelFactory (
    private val textObjectRepository: TextObjectRepository,
private val coroutineDispatcher: CoroutineDispatcher) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (UploadImageViewModel(textObjectRepository, coroutineDispatcher) as T)
}