package com.example.riri.androidApp.textList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.riri.androidApp.R

class TextListAdapter(private val onClick : (String, View) -> Unit) : ListAdapter<String, TextListAdapter.TextListViewHolder>(TextListDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text_row, parent, false)
        return TextListViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: TextListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TextListViewHolder(itemView: View, val onClick : (String, View) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val audioText : TextView = itemView.findViewById(R.id.audio_text)
        private val threeDot : ImageView = itemView.findViewById(R.id.three_dot)
        private val playAudio : ImageView = itemView.findViewById(R.id.play_audio)
        private var sampleT : String? = null

        init {
            playAudio.setOnClickListener {
                sampleT?.let {
                    onClick(it, playAudio)
                }
            }

            threeDot.setOnClickListener {
                sampleT?.let {
                    onClick(it, threeDot)
                }
            }
        }

        fun bind (sample : String) {
            sampleT = sample
            audioText.text = sample
        }

    }

    object TextListDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == oldItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem == oldItem
        }

    }
}

