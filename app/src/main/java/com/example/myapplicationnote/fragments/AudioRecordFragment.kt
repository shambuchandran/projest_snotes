package com.example.myapplicationnote.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.navigation.fragment.findNavController
import com.example.myapplicationnote.R
import com.example.myapplicationnote.Timer
import com.example.myapplicationnote.databinding.FragmentAudioRecordBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale

const val REQUEST_CODE = 1

class AudioRecordFragment : Fragment(R.layout.fragment_audio_record), Timer.OnTimeChangeListener {
    private var audioRecordBinding: FragmentAudioRecordBinding? = null
    private val binding get() = audioRecordBinding!!
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    private lateinit var recordBtn: ImageButton
    private lateinit var listBtn: ImageButton
    private lateinit var doneBtn: ImageButton
    private lateinit var deleteBtn: ImageButton
    private lateinit var showTimer: TextView
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false
    private lateinit var timer: Timer
    private lateinit var vibrator: Vibrator
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBg: View
    private lateinit var filenameInput: TextInputEditText
    private lateinit var cancelButton: Button
    private lateinit var okButton: Button
    private var filePath = ""
    private var fileName = ""
    private var audioDuration = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        audioRecordBinding = FragmentAudioRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        permissionGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE)
        }
        filenameInput = binding.root.findViewById(R.id.fileNameInput)
        bottomSheetBg = binding.bottomSheetBg
        bottomSheet = binding.root.findViewById(R.id.bottom_Sheet_Layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        timer = Timer(this)
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        recordBtn = binding.btnRecord
        recordBtn.setOnClickListener {
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        listBtn = binding.btnList
        listBtn.setOnClickListener {
            Toast.makeText(requireContext(), "list", Toast.LENGTH_SHORT).show()

        }
        doneBtn = binding.btnDone
        doneBtn.setOnClickListener {
            stopRecording()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBg.visibility = View.VISIBLE
            bottomSheetBg.isClickable = false
            bottomSheetBehavior.isHideable = false
            filenameInput.setText(filename)

        }
        cancelButton = binding.root.findViewById(R.id.btnCancel)
        cancelButton.setOnClickListener {
            File("$dirPath$filename.mp3").delete()
            dismiss()

        }
        okButton = binding.root.findViewById(R.id.btnOk)
        okButton.setOnClickListener {
            save()
            dismiss()
        }
        bottomSheetBg.setOnClickListener {
            File("$dirPath$filename.mp3").delete()
            dismiss()
        }
        deleteBtn = binding.btnDelete
        deleteBtn.setOnClickListener {
            stopRecording()
            File("$dirPath$filename.mp3").delete()
            Toast.makeText(requireContext(), "Record deleted", Toast.LENGTH_SHORT).show()
        }
        deleteBtn.isClickable = false


    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun dismiss() {
        bottomSheetBg.visibility = View.GONE
        hideKeyboard(filenameInput)
        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100)
    }

    private fun save() {
        val newFilename = filenameInput.text.toString()
        if (newFilename != filename) {
            val newFile = File("$dirPath$newFilename.mp3")
            File("$dirPath$filename.mp3").renameTo(newFile)
            Toast.makeText(requireContext(), "Recording saved", Toast.LENGTH_SHORT).show()
        }
        fileName = "$newFilename.mp3"
        filePath = "$dirPath$filename.mp3"
        val action = AudioRecordFragmentDirections.actionAudioRecordFragmentToAddNoteFragment(
            filePath,
            fileName,
            audioDuration
        )
        findNavController().navigate(action)

    }

    private fun pauseRecording() {
        recorder.pause()
        isPaused = true
        recordBtn.setImageResource(R.drawable.ic_record)
        timer.pause()

    }

    private fun resumeRecording() {
        recorder.resume()
        isPaused = false
        recordBtn.setImageResource(R.drawable.round_pause_24)
        timer.start()
    }

    private fun stopRecording() {
        timer.stop()
        recorder.apply {
            stop()
            release()
        }
        isPaused = false
        isRecording = false
        listBtn.visibility = View.VISIBLE
        doneBtn.visibility = View.GONE
        deleteBtn.isClickable = false
        deleteBtn.setImageResource(R.drawable.round_clear_disabled_24)
        recordBtn.setImageResource(R.drawable.ic_record)
        showTimer.text = "00.00.00"

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE)
            return
        }
        recorder = MediaRecorder()
        dirPath = "${requireContext().externalCacheDir?.absolutePath}/"
        val simpleDateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val date = simpleDateFormat.format(Date())
        filename = "Rec_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")
            try {
                prepare()
            } catch (_: IOException) {
            }
            start()
        }
        recordBtn.setImageResource(R.drawable.round_pause_24)
        isRecording = true
        isPaused = false
        timer.start()
        deleteBtn.isClickable = true
        deleteBtn.setImageResource(R.drawable.round_clear_24)
        listBtn.visibility = View.GONE
        doneBtn.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioRecordBinding = null
    }

    override fun onTimerChange(duration: String) {
        showTimer = binding.tvTimer
        showTimer.text = duration
        audioDuration = duration.dropLast(3)
    }
}