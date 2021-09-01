package com.example.riri.androidApp.textList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.riri.androidApp.R

class TextListFragment : Fragment() {

    companion object {
        fun newInstance() = TextListFragment()
    }

    private lateinit var viewModel: TextListViewModel
    private lateinit var textList : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        textList =  arrayListOf("rita", "nkem", "okonkwo")
        return inflater.inflate(R.layout.text_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TextListViewModel::class.java)
        val recyclerView : RecyclerView = requireView().findViewById(R.id.listRV)
        val adapter = TextListAdapter{ text, view ->
            textListOnClick(text, view)
        }
        adapter.submitList(textList)
        recyclerView.adapter = adapter

    }

    private fun textListOnClick(sample : String, view : View) {
        if (view.id == R.id.play_audio) {
            Toast.makeText(context, "play audio", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "open three dots", Toast.LENGTH_SHORT).show()
        }

    }

}