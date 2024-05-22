package com.example.myapplicationnote.adapter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnote.R
import com.example.myapplicationnote.fragments.AddNoteFragmentDirections
import com.example.myapplicationnote.fragments.AudioFile
import com.example.myapplicationnote.fragments.AudioPlayerFragmentDirections


class AudioAdapter(private val audioFiles: List<AudioFile>) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {


    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.tvAudioFilename)
        val audioDuration: TextView = itemView.findViewById(R.id.tvAudioDuration)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.audioeachitem, parent, false)
        return AudioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return audioFiles.size
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audioFile = audioFiles[position]
        holder.fileName.text = audioFile.fileName
        holder.audioDuration.text = audioFile.duration
        holder.itemView.setOnClickListener {
//            audioFiles[position].filePath
//            audioFiles[position].fileName
//            it.findNavController().navigate(R.id.audioPlayerFragment)
            val args = bundleOf(
                "filePath" to audioFile.filePath,
                "fileName" to audioFile.fileName,
                "audioDuration" to audioFile.duration
            )
            it.findNavController().navigate(R.id.audioPlayerFragment,args)


        }
//        holder.itemView.setOnLongClickListener {
//            println("long click")
//
//            return@setOnLongClickListener true
//        }
    }
}