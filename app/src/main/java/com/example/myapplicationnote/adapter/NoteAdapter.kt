package com.example.myapplicationnote.adapter

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplicationnote.R
import com.example.myapplicationnote.databinding.NoteLayoutBinding
import com.example.myapplicationnote.fragments.AudioFile
import com.example.myapplicationnote.fragments.HomeFragmentDirections
import com.example.myapplicationnote.model.Note
import java.io.IOException

class NoteAdapter(private val context: Context) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(val itemBinding: NoteLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var audioPlayerBtn: ImageButton

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteTitle == newItem.noteTitle &&
                    oldItem.noteDesc == newItem.noteDesc
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val currentNote = differ.currentList[position]
        holder.itemBinding.noteTitle.text = currentNote.noteTitle
        holder.itemBinding.noteDesc.text = currentNote.noteDesc
        holder.itemBinding.showTime.text = currentNote.date
        if (currentNote.audioFiles.isNotEmpty()) {
            audioPlayerBtn = holder.itemBinding.audioPlayerBtn
            audioPlayerBtn.visibility = View.VISIBLE
            audioPlayerBtn.background = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.round_play_arrow_24,
                context.theme
            )
            mediaPlayer= MediaPlayer()
            mediaPlayer.apply {
                try {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(currentNote.audioFiles[0].filePath)
                    mediaPlayer.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error: Unable to play audio", Toast.LENGTH_SHORT)
                        .show()
                }
                setOnCompletionListener {
                    audioPlayerBtn.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.round_play_arrow_24,
                        context.theme
                    )
                }
            }
            audioPlayerBtn.setOnClickListener {
                playPausePlayer(context)
            }

        } else {
            holder.itemBinding.audioPlayerBtn.visibility = View.GONE
            releaseMediaPlayer()
        }

//        holder.itemView.setOnClickListener {
//            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
//            it.findNavController().navigate(direction)
//        }
        holder.itemBinding.editNoteBtn.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }


    }

    private fun playPausePlayer(context: Context) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            audioPlayerBtn.background =
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.round_pause_24,
                    context.theme
                )
        } else {
            mediaPlayer.pause()
            audioPlayerBtn.background = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.round_play_arrow_24,
                context.theme
            )
        }
    }

    fun releaseMediaPlayer() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        //mediaPlayer.release()
    }

}