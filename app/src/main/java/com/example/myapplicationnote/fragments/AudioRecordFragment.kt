package com.example.myapplicationnote.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.example.myapplicationnote.R
import com.example.myapplicationnote.Timer
import com.example.myapplicationnote.databinding.FragmentAudioRecordBinding
import java.io.IOException
import java.util.Date
import java.util.Locale

const val REQUEST_CODE =1
class AudioRecordFragment : Fragment(R.layout.fragment_audio_record),Timer.OnTimeChangeListener {
    private var audioRecordBinding: FragmentAudioRecordBinding? = null
    private val binding get() = audioRecordBinding!!
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    private lateinit var recordBtn:ImageButton
    private lateinit var recorder: MediaRecorder
    private var dirPath =""
    private var filename =""
    private var isRecording = false
    private var isPaused =false
    private lateinit var timer: Timer
    private lateinit var vibrator: Vibrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        audioRecordBinding= FragmentAudioRecordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionGranted =ActivityCompat.checkSelfPermission(requireContext(),permissions[0])== PackageManager.PERMISSION_GRANTED
        if (!permissionGranted){
            ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CODE)
        }
        timer= Timer(this)
        vibrator= requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        recordBtn=binding.btnRecord
        recordBtn.setOnClickListener {
            when{
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun pauseRecording() {
       recorder.pause()
        isPaused =true
        recordBtn.setImageResource(R.drawable.ic_record)
        timer.pause()

    }

    private fun resumeRecording() {
        recorder.resume()
        isPaused=false
        recordBtn.setImageResource(R.drawable.round_pause_24)
        timer.start()
    }
    private fun stopRecording() {
        timer.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            permissionGranted=grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun startRecording(){
        if (!permissionGranted){
            ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CODE)
            return
        }
        recorder = MediaRecorder()
        dirPath ="${requireContext().externalCacheDir?.absolutePath}/"
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
            }catch (_: IOException){}
            start()
        }
        recordBtn.setImageResource(R.drawable.round_pause_24)
        isRecording =true
        isPaused =false
        timer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioRecordBinding=null
    }

    override fun onTimerChange(duration: String) {
        binding.tvTimer.text =duration

    }
}