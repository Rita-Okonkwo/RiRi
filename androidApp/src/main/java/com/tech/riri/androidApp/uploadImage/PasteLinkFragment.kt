package com.tech.riri.androidApp.uploadImage

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.BitmapCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tech.riri.androidApp.R
import com.tech.riri.androidApp.databinding.FragmentPasteLinkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class PasteLinkFragment : Fragment() {

    private lateinit var viewModel: UploadImageViewModel
    private var _binding: FragmentPasteLinkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentPasteLinkBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(UploadImageViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pasteUploadBtn.setOnClickListener {
            if (binding.pasteLink.text.isNotEmpty()) {
                uploadImage()
            } else {
                Toast.makeText(context, "Please paste an image URL", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.status.observe(viewLifecycleOwner, { status ->
            if (status == "loading") {
                binding.progressBar3.visibility = View.VISIBLE
            } else {
                binding.progressBar3.visibility = View.GONE
            }
            if (status == "fn") {
                Toast.makeText(context, "Check internet connectivity and try again", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.imageStatus.observe(viewLifecycleOwner, { imageStatus ->
            if (imageStatus == "succeeded") {
                viewModel.retrieveImageResponse()
            }
        })

        viewModel.text.observe(viewLifecycleOwner, { text ->
            if (text != null) {
                println(text)
                findNavController().navigate(PasteLinkFragmentDirections.actionPasteLinkFragmentToResultFragment(text))
            }
        })
    }
    private fun uploadImage()  {
        lifecycleScope.launch {
            binding.progressBar3.visibility = View.VISIBLE
            val urlString = binding.pasteLink.text.toString().trim()
            val imageSize = downloadImage(urlString)
            if (imageSize != null) {
                if (imageSize > 4) {
                    binding.progressBar3.visibility = View.GONE
                    Toast.makeText(context, "Please upload an image less than 4MB", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.uploadImgUrl(urlString)
                }
            } else {
                Toast.makeText(context, "Please paste a valid image link", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun downloadImage(urlString: String) =
        withContext(Dispatchers.IO) {
            return@withContext try {
                saveImage(
                    try {
                        Glide.with(requireActivity().applicationContext)
                            .asBitmap()
                            .load(urlString)
                            .submit()
                            .get()
                    } catch (e : Exception){
                        null
                    }

                )
            } catch (e : IOException) {
                Log.d("bmp", e.toString())
                null
            }
        }

    private fun saveImage(get: Bitmap?) : Double? {
        if (get != null){
            val imageSize  = get.let { BitmapCompat.getAllocationByteCount(it) }
            return imageSize / 1048576.toDouble()
        }
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)  {
            R.id.bookmarks -> {
                findNavController().navigate(R.id.action_pasteLinkFragment_to_textListFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}