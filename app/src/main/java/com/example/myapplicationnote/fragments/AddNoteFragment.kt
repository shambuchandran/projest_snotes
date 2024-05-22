package com.example.myapplicationnote.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Entity
import com.example.myapplicationnote.MainActivity
import com.example.myapplicationnote.R
import com.example.myapplicationnote.adapter.AudioAdapter
import com.example.myapplicationnote.audioFileSharedFlow
import com.example.myapplicationnote.databinding.FragmentAddNoteBinding
import com.example.myapplicationnote.model.Note
import com.example.myapplicationnote.viewmodel.NoteViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Parcelize
data class AudioFile(val filePath: String, val fileName: String, val duration: String):Parcelable

class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding: FragmentAddNoteBinding? = null
    private val binding get() = addNoteBinding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView: View
    private lateinit var addAudioBtn: ImageButton
    private val audioFiles = mutableListOf<AudioFile>()
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var audioRecyclerView: RecyclerView
    private val args: AddNoteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        try {
            binding.etNoteContent.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.styleBar.visibility = View.VISIBLE
                    binding.etNoteContent.setStylesBar(binding.styleBar)
                } else {
                    binding.styleBar.visibility = View.GONE
                }
            }
        } catch (e: Throwable) {
            Log.d("Tag", e.stackTraceToString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioAdapter = AudioAdapter(audioFiles)
        audioRecyclerView=binding.audioRecyclerView
        audioRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        audioRecyclerView.adapter = audioAdapter


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        notesViewModel = (activity as MainActivity).noteViewModel

        addNoteView = view
        addAudioBtn = binding.addAudioBtn
        addAudioBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_addNoteFragment_to_audioRecordFragment)
            //addAudioLiveDataObserver()
            subscribeToAudiFileSharedFlow()
        }
    }

    private fun addAudioFileToList(audioFile: AudioFile) {
        audioFiles.add(audioFile)
        audioAdapter.notifyItemInserted(audioFiles.size -1)
//        audioAdapter.notifyItemInserted(audioFiles.size - 1)
    }
//    private fun addAudioLiveDataObserver(){
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("AUDIOFILE")?.observe(viewLifecycleOwner) { audioFile ->
//            val audioFileObj= Gson().fromJson(audioFile,AudioFile::class.java)
//            addAudioFileToList(audioFileObj)
//            Log.d("path","path received ")
//            findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>("AUDIIOFILE")
//        }
//    }
private fun subscribeToAudiFileSharedFlow(){
    var job : Job?= null
    job= CoroutineScope(Dispatchers.Main).launch {
        audioFileSharedFlow.collect {
            addAudioFileToList(it)
            Log.d("Shared flow", "AudioFile $it")
            job?.cancel()
        }
    }
}


    private fun saveNote(view: View) {
        val noteTitle = binding.addNoteTitle.text.toString().trim()
        //val noteDesc = binding.addNoteDesc.text.toString().trim()
        val noteDesc = binding.etNoteContent.getMD().trim()
        val date =
            SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().time)
        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteDesc, date,audioFiles)
            notesViewModel.addNote(note)
            Toast.makeText(addNoteView.context, "Note Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(addNoteView.context, "Please enter note title", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.addnote_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                saveNote(addNoteView)
                true
            }

            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }
}