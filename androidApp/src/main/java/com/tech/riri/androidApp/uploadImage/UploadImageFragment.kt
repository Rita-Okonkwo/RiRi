package com.tech.riri.androidApp.uploadImage

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.OpenableColumns
import android.speech.tts.TextToSpeech
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tech.riri.androidApp.R
import com.tech.riri.androidApp.databinding.UploadImageFragmentBinding
import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.data.TextObjectRepository
import com.tech.riri.shared.data.local.TextObjectLocalDataSource
import com.tech.riri.shared.data.remote.TextObjectRemoteDataSource
import kotlinx.coroutines.Dispatchers
import java.io.FileOutputStream
import java.util.*

class UploadImageFragment : Fragment() {
    private var _binding: UploadImageFragmentBinding? = null
    private val binding get() = _binding!!
    private val PICK_IMAGE = 50
    private var filePath: Uri? = null
    private val viewModel by viewModels<UploadImageViewModel> {
        UploadImageViewModelFactory( TextObjectRepository(Dispatchers.IO, TextObjectRemoteDataSource(), TextObjectLocalDataSource(
            TextObjectDatabaseDriverFactory(requireActivity().applicationContext)
        )), Dispatchers.IO)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = UploadImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view : View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.status.observe(viewLifecycleOwner, { status ->
            if (status == "loading") {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
            if (status == "fn") {
                Toast.makeText(context, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show()
                binding.selectBtn.isEnabled = true
            }
        })

      viewModel.text.observe(viewLifecycleOwner, { text ->
            if (text != null) {
                findNavController().navigate(UploadImageFragmentDirections.actionUploadImageFragmentToResultFragment(text))
            }
        })

        binding.frame.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission()
            } else {
                launchGallery()
            }
        }

       binding.selectBtn.setOnClickListener {
           if (binding.image.drawable != null) {
               binding.progressBar.visibility = View.VISIBLE
               uploadImage()
               binding.selectBtn.isEnabled = false
           } else {
               Toast.makeText(context, "Please select an image", Toast.LENGTH_LONG).show()
           }
        }

        viewModel.imageStatus.observe(viewLifecycleOwner, { imageStatus ->
            if (imageStatus == "succeeded") {
                viewModel.retrieveImageResponse()
            }
        })

        viewModel.image.observe(viewLifecycleOwner, { image ->
            binding.image.setImageBitmap(image)
        })

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


   private fun uploadImage() {
       viewModel.uploadImage(filePath)
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
            binding.addImage.visibility = View.GONE
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

            binding.selectText.text = "Change image"
            binding.selectText.setTextColor(Color.parseColor("#FEFEE3"))

            binding.gradient.visibility = View.VISIBLE

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)  {
            R.id.bookmarks -> {
                findNavController().navigate(R.id.action_uploadImageFragment_to_textListFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}