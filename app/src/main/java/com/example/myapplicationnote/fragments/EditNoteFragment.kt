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
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.marginBottom
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplicationnote.MainActivity
import com.example.myapplicationnote.R
import com.example.myapplicationnote.databinding.FragmentEditNoteBinding
import com.example.myapplicationnote.model.Note
import com.example.myapplicationnote.viewmodel.NoteViewModel
import java.io.File


class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

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

        binding.editNoteTitle.setText(currentNote.noteTitle)
        //binding.editNoteDesc.setText(currentNote.noteDesc)
        binding.etNoteContent.setText(currentNote.noteDesc)
        binding.editNoteFab.setOnClickListener {
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            //val noteDesc = binding.editNoteDesc.text.toString().trim()
            val noteDesc=binding.etNoteContent.getMD().trim()

            if (noteTitle.isNotEmpty()) {
                val note = Note(currentNote.id, noteTitle, noteDesc)
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
            }
        }
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
            }else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding =null
    }

}