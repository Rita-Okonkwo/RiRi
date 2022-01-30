package com.tech.riri.androidApp.uploadImage

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tech.riri.androidApp.R
import com.tech.riri.androidApp.databinding.FragmentPasteLinkBinding
import com.tech.riri.androidApp.databinding.FragmentResultBinding
import com.tech.riri.shared.cache.TextObjectDatabaseDriverFactory
import com.tech.riri.shared.data.TextObjectRepository
import com.tech.riri.shared.data.local.TextObjectLocalDataSource
import com.tech.riri.shared.data.remote.TextObjectRemoteDataSource
import kotlinx.coroutines.Dispatchers
import java.util.*


class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<UploadImageViewModel> {
        UploadImageViewModelFactory( TextObjectRepository(Dispatchers.IO,
            TextObjectRemoteDataSource(), TextObjectLocalDataSource(
            TextObjectDatabaseDriverFactory(requireActivity().applicationContext)
        )
        ), Dispatchers.IO)
    }
    private val args : ResultFragmentArgs by navArgs()
    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resultText.text = args.resultText

        binding.resultSaveImg.setOnClickListener {
            viewModel.saveText(binding.resultText.text.toString())
            findNavController().navigate(R.id.action_resultFragment_to_myDialog)
            binding.resultSaveImg.visibility = View.INVISIBLE
        }

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
    }

    private fun playAndStop() {
        val toSpeak = binding.resultText.text.toString()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bookmarks -> {
                findNavController().navigate(R.id.action_resultFragment_to_textListFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        tts.stop()
        tts.shutdown()
        super.onDestroyView()
    }
}