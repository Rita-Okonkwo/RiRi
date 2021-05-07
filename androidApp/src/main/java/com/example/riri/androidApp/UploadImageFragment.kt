package com.example.riri.androidApp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import java.io.FileOutputStream

class UploadImageFragment : Fragment() {
    private val PICK_IMAGE = 50
    private var filePath: Uri? = null
    private lateinit var viewModel: UploadImageViewModel
    private val tv: TextView by lazy {
        requireView().findViewById(R.id.textview)
    }

    private val choose: Button by lazy {
        requireView().findViewById(R.id.choose)
    }

    private val upload: Button by lazy {
        requireView().findViewById(R.id.upload)
    }

    private val imageView: ImageView by lazy {
        requireView().findViewById(R.id.image)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.upload_image_fragment, container, false)
        viewModel = ViewModelProvider(this).get(UploadImageViewModel::class.java)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            tv.text = status
        })
        choose.setOnClickListener {
            launchGallery()
        }

        upload.setOnClickListener {
            Log.d("uri", filePath.toString())
            viewModel.uploadImage(filePath)
        }

        viewModel.imageStatus.observe(viewLifecycleOwner, Observer { imageStatus ->
            if (imageStatus == "succeeded") {
                viewModel.retrieveImageResponse()
            }
        })

        viewModel.image.observe(viewLifecycleOwner, Observer { image ->
            imageView.setImageBitmap(image)
        })
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