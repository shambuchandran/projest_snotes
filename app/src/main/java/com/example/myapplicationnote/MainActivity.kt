package com.example.myapplicationnote

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationnote.database.NoteDatabase
import com.example.myapplicationnote.repository.NoteRepository
import com.example.myapplicationnote.viewmodel.NoteViewModel
import com.example.myapplicationnote.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {
    public lateinit var noteViewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        NoteDatabase.deleteDatabase(this.applicationContext)
        setupViewModel()

    }
    private fun setupViewModel(){
        val noteRepository =NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory=NoteViewModelFactory(application,noteRepository)
        noteViewModel=ViewModelProvider(this,viewModelProviderFactory)[NoteViewModel::class.java]
    }
}