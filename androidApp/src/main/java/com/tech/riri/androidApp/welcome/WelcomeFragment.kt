package com.tech.riri.androidApp.welcome

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tech.riri.androidApp.R
import com.tech.riri.androidApp.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    private var _binding : FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomePasteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_pasteLinkFragment)
        }
        binding.welcomeSelectBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_uploadImageFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when (item.itemId)  {
            R.id.bookmarks -> {
                findNavController().navigate(R.id.action_welcomeFragment_to_textListFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}