package com.example.riri.androidApp

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer

class UploadImageFragment : Fragment() {


    private lateinit var viewModel : UploadImageViewModel
    private val tv : TextView by lazy {
        view!!.findViewById(R.id.textview)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.upload_image_fragment, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UploadImageViewModel::class.java)
        viewModel.retrieveImageResponse()
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            tv.text = status
        })
    }

}