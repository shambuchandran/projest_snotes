package com.example.myapplicationnote.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.core.view.marginBottom
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnote.MainActivity
import com.example.myapplicationnote.R
import com.example.myapplicationnote.adapter.AudioAdapter
import com.example.myapplicationnote.audioFileSharedFlow
import com.example.myapplicationnote.databinding.FragmentEditNoteBinding
import com.example.myapplicationnote.model.Note
import com.example.myapplicationnote.viewmodel.NoteViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note
    private lateinit var audioEditAdapter: AudioAdapter
    private lateinit var audioEditRecyclerView: RecyclerView
    private lateinit var audioEditBtn:ImageButton

    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        try {
            binding.etNoteContent.setOnFocusChangeListener { v, hasFocus ->
                val layoutParams=binding.editNoteFab.layoutParams as ViewGroup.MarginLayoutParams
                if (hasFocus){
                    binding.styleBar.visibility=View.VISIBLE
                    binding.etNoteContent.setStylesBar(binding.styleBar)
                    layoutParams.bottomMargin=resources.getDimensionPixelSize(R.dimen.margin_64dp)
                    binding.editNoteFab.layoutParams=layoutParams
                }else{
                    binding.styleBar.visibility=View.GONE
                    layoutParams.bottomMargin=resources.getDimensionPixelSize(R.dimen.margin_20dp)
                    binding.editNoteFab.layoutParams=layoutParams
                }
            }
        }catch (e:Throwable){
            Log.d("Tag", e.stackTraceToString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!
        audioEditBtn = binding.addEditAudioBtn
        audioEditBtn.setOnClickListener {
            subscribeToAudiFileSharedFlow()
            view.findNavController().navigate(R.id.action_editNoteFragment_to_audioRecordFragment)
        }

        binding.editNoteTitle.setText(currentNote.noteTitle)
        //binding.editNoteDesc.setText(currentNote.noteDesc)
        binding.etNoteContent.setText(currentNote.noteDesc)
        audioEditAdapter = AudioAdapter(currentNote.audioFiles)
        audioEditRecyclerView=binding.audioEditRecyclerView
        audioEditRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        audioEditRecyclerView.adapter = audioEditAdapter

//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("AUDIOFILE")?.observe(viewLifecycleOwner) { audioFile ->
//            val audioFileObj= Gson().fromJson(audioFile,AudioFile::class.java)
//            addAudioFileToList(audioFileObj)
//            Log.d("path","path received ")
//        }
        //subscribeToAudiFileSharedFlow()


        binding.editNoteFab.setOnClickListener {
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            //val noteDesc = binding.editNoteDesc.text.toString().trim()
            val noteDesc=binding.etNoteContent.getMD().trim()
            val audioEditFiles=currentNote.audioFiles
            val date = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().time)

            if (noteTitle.isNotEmpty()) {
                val note = Note(currentNote.id, noteTitle, noteDesc,date, audioEditFiles )
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun subscribeToAudiFileSharedFlow(){
        var job : Job?= null
        job =CoroutineScope(Dispatchers.Main).launch {
            audioFileSharedFlow.collect {
                addAudioFileToList(it)
                //audioEditAdapter.notifyItemInserted(currentNote.audioFiles.size -1)
                audioEditAdapter.notifyDataSetChanged()
                Log.d("Shared flow", "AudioFile $it")
                job?.cancel()
            }
        }
    }

    private fun addAudioFileToList(audioFile: AudioFile) {
        currentNote.audioFiles.add(audioFile)
        audioEditAdapter.notifyDataSetChanged()
    }
    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note!")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete"){_,_ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment,false)
            }
            setNegativeButton("Cancel",null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.edit_menu,menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu ->{
                deleteNote()
                true
            }
            R.id.editSetAlarmMenu ->{
              //set alarm
                true
            }else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding =null

    }

}