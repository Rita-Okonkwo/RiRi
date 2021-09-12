package com.example.riri.androidApp.uploadImage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.riri.androidApp.R
import java.io.FileOutputStream
import java.util.*

class UploadImageFragment : Fragment() {
    private val PICK_IMAGE = 50
    private var filePath: Uri? = null
    private lateinit var viewModel: UploadImageViewModel
    private lateinit var tts: TextToSpeech
    private val tv: TextView by lazy {
        requireView().findViewById(R.id.helloText)
    }

    private val imageView: ImageView by lazy {
        requireView().findViewById(R.id.image)
    }

    private val imageTxt: TextView by lazy {
        requireView().findViewById(R.id.extractedtext)
    }


    private val select: ConstraintLayout by lazy {
        requireView().findViewById(R.id.frame)
    }

    private val upload: Button by lazy {
        requireView().findViewById(R.id.select_btn)
    }

    private val progress: ProgressBar by lazy {
        requireView().findViewById(R.id.progressBar)
    }

    private val playandstop: ImageView by lazy {
        requireView().findViewById(R.id.playandstop)
    }

    private val urlImage: EditText by lazy {
        requireView().findViewById(R.id.url)
    }

    private val saveBtn: Button by lazy {
        requireView().findViewById(R.id.save)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.upload_image_fragment, container, false)
        viewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UploadImageViewModel::class.java)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            if (status == "loading") {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
            }
            if (status == "fn") {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.text.observe(viewLifecycleOwner, Observer { text ->
            imageTxt.text = text
        })

        select.setOnClickListener {
            launchGallery()
        }

        upload.setOnClickListener {
            uploadImage()
        }

        viewModel.imageStatus.observe(viewLifecycleOwner, Observer { imageStatus ->
            if (imageStatus == "succeeded") {
                viewModel.retrieveImageResponse()
            }
        })

        viewModel.image.observe(viewLifecycleOwner, Observer { image ->
            imageView.setImageBitmap(image)
        })

        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.UK
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.i("TextToSpeech", "On Start")
                    }

                    override fun onDone(utteranceId: String) {
                        Log.i("TextToSpeech", "On Done")
                        requireActivity().runOnUiThread {
                            playandstop.setImageResource(R.drawable.play)
                        }
                    }

                    override fun onError(utteranceId: String) {
                        Log.i("TextToSpeech", "On Error")
                    }
                })
            }
        })

        playandstop.setOnClickListener {
            playAndStop()
        }

        saveBtn.setOnClickListener {
            viewModel.saveText(imageTxt.text.toString())
            findNavController().navigate(R.id.action_uploadImageFragment_to_textListFragment)
        }

    }

    private fun playAndStop() {
        val toSpeak = imageTxt.text.toString()
        if (toSpeak == "") {
            Toast.makeText(context, "No text", Toast.LENGTH_SHORT).show()
        } else if (tts.isSpeaking && playandstop.tag == getString(R.string.stop)) {
            playandstop.setImageResource(R.drawable.play)
            playandstop.tag = getString(R.string.play)
            tts.stop()
        } else {
            playandstop.setImageResource(R.drawable.stop)
            playandstop.tag = getString(R.string.stop)
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "audiotext")
        }
    }

    private fun uploadImage() {
        Log.d("uri", filePath.toString())
        val urlString = urlImage.text.toString()
        Log.d("edit", urlString)
        if (urlString.isEmpty()) {
            viewModel.uploadImage(filePath)
        } else {
            viewModel.uploadImageUrl(urlString)
        }
    }

    private fun launchGallery() {
        val photoIntent = Intent()
        photoIntent.type = "image/*"
        photoIntent.action = Intent.ACTION_GET_CONTENT
        viewModel.createImageFile(requireContext())
        startActivityForResult(Intent.createChooser(photoIntent, "Select Picture"), PICK_IMAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            select.visibility = View.GONE
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            viewModel.setPic(requireContext(), filePath)
            val outputStream =
                FileOutputStream(viewModel.currentPhotoPath.value!!)
            viewModel.image.value?.compress(
                Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
            outputStream.close()
        }
    }

}