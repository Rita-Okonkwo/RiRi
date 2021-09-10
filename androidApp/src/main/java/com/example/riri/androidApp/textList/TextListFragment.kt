package com.example.riri.androidApp.textList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.riri.androidApp.R
import com.example.riri.shared.data.models.TextObjectDataModel

class TextListFragment : Fragment() {

    private lateinit var viewModel: TextListViewModel
    private lateinit var textList: ArrayList<TextObjectDataModel>
    private lateinit var adapter: TextListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.text_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(TextListViewModel::class.java)
        val recyclerView: RecyclerView = requireView().findViewById(R.id.listRV)
        adapter = TextListAdapter { text, view ->
            textListOnClick(text, view)
        }
        recyclerView.adapter = adapter
        textList = viewModel.getTextList() as ArrayList<TextObjectDataModel>
        if (textList.isNotEmpty()) {
            adapter.submitList(textList)
        }
    }

    private fun textListOnClick(textObject: TextObjectDataModel, view: View) {
        if (view.id == R.id.play_audio) {
            playAudio()
        } else if (view.id == R.id.three_dot) {
            showPopUpMenu(textObject, view)
        }

    }

    private fun showPopUpMenu(textObject: TextObjectDataModel, view: View) {
        val popUpMenu = PopupMenu(view.context, view)
        popUpMenu.inflate(R.menu.popup_menu)
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_copy -> {
                    copyText()
                    true
                }
                R.id.action_share -> {
                    shareText()
                    true
                }
                R.id.action_delete -> {
                    deleteText(textObject)
                    true
                }
                else -> false
            }
        }
        popUpMenu.show()
    }

    private fun copyText() {
        //to do
    }

    private fun shareText() {
        //to do
    }

    private fun deleteText(textObject: TextObjectDataModel) {
        viewModel.deleteText(textObject)
        textList = viewModel.getTextList() as ArrayList<TextObjectDataModel>
        adapter.submitList(textList)
    }

    private fun playAudio() {
        //to do
    }

}