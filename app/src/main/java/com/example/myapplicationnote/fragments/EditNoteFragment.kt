package com.example.myapplicationnote.fragments

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.marginBottom
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationnote.AlarmReceiver
import com.example.myapplicationnote.MainActivity
import com.example.myapplicationnote.R
import com.example.myapplicationnote.adapter.AudioAdapter
import com.example.myapplicationnote.adapter.ImageAdapter
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
import java.io.FileOutputStream
import java.io.IOException
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
    private lateinit var showEditNoteAlarm: TextView
    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager
    private lateinit var imageEditAdapter: ImageAdapter
    private lateinit var imageEditRecyclerView: RecyclerView
    private lateinit var editImageButton: ImageButton
    private var permissions = arrayOf(android.Manifest.permission.CAMERA)
    private val PERMISSION_REQUEST_CODE = 3
    private val REQUEST_IMAGE_CAPTURE = 4
    private val MY_PERMISSIONS_REQUEST_CAMERA = 5
    private val REQUEST_PICK_IMAGE = 6
    private val MAX_IMAGES = 10

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
        showEditNoteAlarm=binding.editNoteShowAlarm
        audioEditBtn = binding.addEditAudioBtn
        audioEditBtn.setOnClickListener {
            subscribeToAudiFileSharedFlow()
            view.findNavController().navigate(R.id.action_editNoteFragment_to_audioRecordFragment)
        }

        binding.editNoteTitle.setText(currentNote.noteTitle)
        //binding.editNoteDesc.setText(currentNote.noteDesc)
        binding.etNoteContent.setText(currentNote.noteDesc)
        audioEditAdapter = AudioAdapter(currentNote.audioFiles)
        imageEditAdapter = ImageAdapter(requireContext(),currentNote.imagePaths)
        showEditNoteAlarm.text = currentNote.alarm
        if (currentNote.alarm==""){
            binding.editNoteShowAlarm.visibility=View.GONE
        }else{
            binding.editNoteShowAlarm.visibility=View.VISIBLE
        }
        Log.d("edit note alarm","${showEditNoteAlarm.text}${currentNote.alarm}")



        audioEditRecyclerView=binding.audioEditRecyclerView
        audioEditRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        audioEditRecyclerView.adapter = audioEditAdapter

        imageEditRecyclerView = binding.editNoteImageRecyclerView
        imageEditRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        //imageEditAdapter=ImageAdapter(requireContext(),currentNote.imagePaths)
        imageEditRecyclerView.adapter = imageEditAdapter

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
            val editedAlarm=binding.editNoteShowAlarm.text.toString()
            val audioEditFiles=currentNote.audioFiles
            val editImageList=currentNote.imagePaths
            val date = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Calendar.getInstance().time)

            if (noteTitle.isNotEmpty()) {
                val note = Note(currentNote.id, noteTitle, noteDesc,date, editedAlarm,editImageList,audioEditFiles )
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
            }
        }
        editImageButton = binding.editNoteAddImage
        editImageButton.setOnClickListener {
            AlertDialog.Builder(activity).apply {
                setTitle("Add Image")
                setMessage("Choose options")
                setPositiveButton("Camera") { _, _ ->
                    checkCameraPermission()
                }
                setNegativeButton("Gallery") { _, _ ->
                    openGallery()
                }
            }.create().show()
        }
    }

    private fun openGallery() {
        val galleryOpenIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryOpenIntent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryOpenIntent.type = "image/*"
        if (galleryOpenIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(galleryOpenIntent, REQUEST_PICK_IMAGE)
        } else {
            Toast.makeText(requireContext(), "No gallery app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissions,
                PERMISSION_REQUEST_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            addImageToList(imageBitmap)
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    selectedImageUri
                )
                addImageToList(imageBitmap)
            }
        }
    }
    private fun addImageToList(imageBitmap: Bitmap) {
        if (currentNote.imagePaths.size < MAX_IMAGES) {
            val imagePath = saveImageToInternalStorage(imageBitmap)
            Log.d("editimagefun",imagePath)
            currentNote.imagePaths.add(imagePath)
            Log.d("editimagelistfun", currentNote.imagePaths.toString())
            //imageAdapter.setImagePath(addNoteImageList)
            imageEditAdapter.notifyItemInserted(currentNote.imagePaths.size - 1)
        } else {
            Toast.makeText(requireContext(), "Maximum limit reached", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveImageToInternalStorage(imageBitmap: Bitmap): String {
        val filename = "${System.currentTimeMillis()}.jpg"
        val directory = requireContext().filesDir
        val file = File(directory, filename)
        try {
            FileOutputStream(file).use { fos ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("savefile on edit",file.absolutePath)
        return file.absolutePath
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
    private fun showDateTimePicker() {
        val calendar =Calendar.getInstance()
        val datePickerDialog= DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view: DatePicker, year:Int, month:Int, dayOfMonth:Int ->
                calendar.set(Calendar.YEAR,year)
                calendar.set(Calendar.MONTH,month)
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay:Int, minute:Int ->
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                        calendar.set(Calendar.MINUTE,minute)
                        val dateFormat=SimpleDateFormat("yy-MM-dd hh:mm a",Locale.getDefault())
                        val formattedDateTime=dateFormat.format(calendar.time)
                        showEditNoteAlarm=binding.editNoteShowAlarm
                        showEditNoteAlarm.text="$formattedDateTime"
                        showEditNoteAlarm.visibility=View.VISIBLE
                        setReminder(calendar.timeInMillis)

                    },calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            },calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()+10000
        datePickerDialog.show()
    }
    private fun setReminder(timeInMillis: Long) {
        alarmManager=requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i= Intent(requireContext(), AlarmReceiver::class.java)
        pendingIntent= PendingIntent.getBroadcast(requireContext(),0,i, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent)
        Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show()
    }
    private fun cancelAlarm(){
        alarmManager=requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i= Intent(requireContext(), AlarmReceiver::class.java)
        pendingIntent= PendingIntent.getBroadcast(requireContext(),0,i, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        showEditNoteAlarm.text=""
        showEditNoteAlarm.visibility=View.GONE
        Toast.makeText(context, "Alarm deleted", Toast.LENGTH_SHORT).show()
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
                AlertDialog.Builder(activity).apply {
                    setTitle("Set/Cancel Alarm")
                    setMessage("Choose from options")
                    setPositiveButton("Set Alarm"){_,_ ->
                        showDateTimePicker()
                    }
                    setNegativeButton("Back",null)
                    setNeutralButton("Delete Alarm"){_,_ ->
                        cancelAlarm()
                    }
                }.create().show()
                true
            }else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding =null

    }


}