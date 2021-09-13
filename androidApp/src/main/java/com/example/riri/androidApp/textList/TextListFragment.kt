package com.example.riri.androidApp.textList

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.riri.androidApp.R
import com.example.riri.shared.data.models.TextObjectDataModel
import java.util.*


class TextListFragment : Fragment() {

    private lateinit var viewModel: TextListViewModel
    private lateinit var textList: ArrayList<TextObjectDataModel>
    private lateinit var adapter: TextListAdapter
    private lateinit var tts: TextToSpeech
    private lateinit var playandstop: ImageView

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
        getTextList()

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
                            playandstop.setImageResource(R.drawable.filled_play)
                        }
                    }

                    override fun onError(utteranceId: String) {
                        Log.i("TextToSpeech", "On Error")
                    }
                })
            }
        })
    }

    private fun textListOnClick(textObject: TextObjectDataModel, view: View) {
        if (view.id == R.id.play_audio) {
            playAudio(textObject, view)
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
                    copyText(textObject)
                    true
                }
                R.id.action_share -> {
                    shareText(textObject)
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

    private fun copyText(textObject: TextObjectDataModel) {
        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
        val clip: ClipData = ClipData.newPlainText("extracted text", textObject.audioText)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(textObject: TextObjectDataModel) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textObject.audioText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun deleteText(textObject: TextObjectDataModel) {
        viewModel.deleteText(textObject)
        getTextList()
    }

    private fun playAudio(textObject: TextObjectDataModel, view: View) {
        playandstop = view.findViewById(R.id.play_audio)
        val toSpeak = textObject.audioText
        if (toSpeak == "") {
            Toast.makeText(context, "No text", Toast.LENGTH_SHORT).show()
        } else if (tts.isSpeaking && playandstop.tag == getString(R.string.stop)) {
            playandstop.setImageResource(R.drawable.filled_play)
            playandstop.tag = getString(R.string.play)
            tts.stop()
        } else {
            playandstop.setImageResource(R.drawable.stop)
            playandstop.tag = getString(R.string.stop)
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "audiotext")
        }
    }

    private fun getTextList() {
        textList = viewModel.getTextList() as ArrayList<TextObjectDataModel>
        adapter.submitList(textList)
    }

}