package com.tech.riri.androidApp.textList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tech.riri.androidApp.R
import com.tech.riri.shared.data.models.TextObjectDataModel


class TextListAdapter(private val onClick: (TextObjectDataModel, View) -> Unit) :
    ListAdapter<TextObjectDataModel, TextListAdapter.TextListViewHolder>(TextListDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text_row, parent, false)
        return TextListViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: TextListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TextListViewHolder(itemView: View, val onClick: (TextObjectDataModel, View) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val audioText: TextView = itemView.findViewById(R.id.audio_text)
        private val threeDot: ImageView = itemView.findViewById(R.id.three_dot)
        private val playAudio: ImageView = itemView.findViewById(R.id.play_audio)
        private var textObjectModel: TextObjectDataModel? = null

        init {

            playAudio.setOnClickListener {
                textObjectModel?.let {
                    onClick(it, playAudio)
                }
            }

            threeDot.setOnClickListener {
                textObjectModel?.let {
                    onClick(it, threeDot)
                }
            }
        }

        fun bind(textObjectDataModel: TextObjectDataModel) {
            textObjectModel = textObjectDataModel
            audioText.text = textObjectDataModel.audioText
        }

    }

    object TextListDiffCallback : DiffUtil.ItemCallback<TextObjectDataModel>() {

        override fun areItemsTheSame(
            oldItem: TextObjectDataModel,
            newItem: TextObjectDataModel
        ): Boolean {
            return oldItem.id == oldItem.id
        }

        override fun areContentsTheSame(
            oldItem: TextObjectDataModel,
            newItem: TextObjectDataModel
        ): Boolean {
            return oldItem == oldItem
        }

    }
}

