package com.example.myapplicationnote.adapter


import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnote.R
import com.example.myapplicationnote.fragments.AudioFile
import java.io.File


class AudioAdapter(private val audioFiles: MutableList<AudioFile>) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {


    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.tvAudioFilename)
        val audioDuration: TextView = itemView.findViewById(R.id.tvAudioDuration)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.audio_eachitem_layout, parent, false)
        return AudioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return audioFiles.size
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audioFile = audioFiles[position]
        Log.d("audioFile onBind", "$audioFiles")
        Log.d("audioFile.path onBind", audioFile.filePath)
        holder.fileName.text = audioFile.fileName

        holder.audioDuration.text = audioFile.duration
        holder.itemView.setOnClickListener {
//            audioFiles[position].filePath
//            audioFiles[position].fileName
//            it.findNavController().navigate(R.id.audioPlayerFragment)
            val args = bundleOf(
                "filePath" to audioFile.filePath,
                "fileName" to audioFile.fileName
            )
            it.findNavController().navigate(R.id.audioPlayerFragment,args)
            Log.d("filepath audioAdapter click", audioFile.filePath)
            Log.d("filename audioAdapter click", audioFile.fileName)


        }
        holder.itemView.setOnLongClickListener {
            println("long click")
            if (File(audioFile.filePath).exists()){
                AlertDialog.Builder(it.context).apply {
                    setTitle("Delete audio!")
                    setMessage("Do you want to delete this audio?")
                    setPositiveButton("Delete"){_,_ ->
                        if (File(audioFile.filePath).delete()){
                            val adapterPosition = holder.adapterPosition
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                audioFiles.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                                notifyItemRangeChanged(adapterPosition, audioFiles.size)
                                Toast.makeText(it.context, "Audio deleted", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(it.context, "Failed to delete audio", Toast.LENGTH_SHORT).show()
                        }
//                        audioFiles.removeAt(holder.adapterPosition)
//                        notifyItemRemoved(holder.adapterPosition)
//                        notifyItemRangeChanged(holder.adapterPosition, audioFiles.size)
//                        Toast.makeText(context, "audio deleted", Toast.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Cancel",null)
                }.create().show()
            }
            return@setOnLongClickListener true
        }
    }
}