package com.example.riri.androidApp.uploadImage

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.OpenableColumns
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.riri.androidApp.R
import com.example.riri.androidApp.databinding.UploadImageFragmentBinding
import java.io.FileOutputStream
import java.util.*
import java.util.jar.Manifest

class UploadImageFragment : Fragment() {
    private var _binding: UploadImageFragmentBinding? = null
    private val binding get() = _binding!!
    private val PICK_IMAGE = 50
    private var filePath: Uri? = null
    private lateinit var viewModel: UploadImageViewModel
    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UploadImageFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UploadImageViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.status.observe(viewLifecycleOwner, { status ->
            if (status == "loading") {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
            if (status == "fn") {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.text.observe(viewLifecycleOwner, { text ->
            binding.extractedtext.text = text
        })

        binding.frame.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission()
            } else {
                launchGallery()
            }
        }

        binding.selectBtn.setOnClickListener {
            uploadImage()
        }

        viewModel.imageStatus.observe(viewLifecycleOwner, { imageStatus ->
            if (imageStatus == "succeeded") {
                viewModel.retrieveImageResponse()
            }
        })

        viewModel.image.observe(viewLifecycleOwner, { image ->
            binding.image.setImageBitmap(image)
        })

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.UK
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.i("TextToSpeech", "On Start")
                    }

                    override fun onDone(utteranceId: String) {
                        Log.i("TextToSpeech", "On Done")
                        requireActivity().runOnUiThread {
                            binding.playandstop.setImageResource(R.drawable.play)
                        }
                    }

                    override fun onError(utteranceId: String) {
                        Log.i("TextToSpeech", "On Error")
                    }
                })
            }
        }

        binding.playandstop.setOnClickListener {
            playAndStop()
        }

        binding.save.setOnClickListener {
            viewModel.saveText(binding.extractedtext.text.toString())
            findNavController().navigate(R.id.action_uploadImageFragment_to_textListFragment)
        }

    }

    private fun checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        } else {
            launchGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (permissions[0] == android.Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery()
            }
        }
    }

    private fun playAndStop() {
        val toSpeak = binding.extractedtext.text.toString()
        if (toSpeak == "") {
            Toast.makeText(context, "No text", Toast.LENGTH_SHORT).show()
        } else if (tts.isSpeaking && binding.playandstop.tag == getString(R.string.stop)) {
            binding.playandstop.setImageResource(R.drawable.play)
            binding.playandstop.tag = getString(R.string.play)
            tts.stop()
        } else {
            binding.playandstop.setImageResource(R.drawable.stop)
            binding.playandstop.tag = getString(R.string.stop)
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "audiotext")
        }
    }

    private fun uploadImage() {
        Log.d("uri", filePath.toString())
        val urlString = binding.url.text.toString()
        Log.d("edit", urlString)
        if (urlString.isEmpty()) {
            viewModel.uploadImage(filePath)
        } else {

            viewModel.uploadImgUrl(urlString)
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
            binding.frame.visibility = View.GONE
            if (data == null || data.data == null) {
                return
            }
            val mimeType: String? = data.data?.let { returnUri ->
                context?.contentResolver?.getType(returnUri)
            }
            var imageSize : Double? = null
           data.data?.let { returnUri ->
                context?.contentResolver?.query(returnUri, null, null, null, null)
            }?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                imageSize = cursor.getDouble(sizeIndex)
            }

            if (imageSize == null) {
                return
            }
            imageSize = imageSize!! / 1048576.toDouble()
            

            if (mimeType == null) {
                Toast.makeText(context, "Please upload an image", Toast.LENGTH_SHORT).show()
                return
            }

            if (imageSize!! > 4) {
                Toast.makeText(context, "Please upload an image less than 4MB", Toast.LENGTH_SHORT).show()
                return
            }

            if (mimeType != "image/jpeg" && mimeType != "image/png" && mimeType != "image/jpg") {
                Toast.makeText(context, "Please upload an image", Toast.LENGTH_SHORT).show()
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