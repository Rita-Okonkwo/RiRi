package com.tech.riri.androidApp.uploadImage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.tech.riri.androidApp.R
import com.tech.riri.androidApp.databinding.FragmentPasteLinkBinding
import com.tech.riri.androidApp.databinding.MyDialogBinding


class MyDialog : DialogFragment() {

    private var _binding: MyDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = MyDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewTexts.setOnClickListener {
            findNavController().navigate(R.id.action_myDialog_to_textListFragment)
        }

        binding.closeIcon.setOnClickListener {
            dismiss()
        }
    }
}