package com.example.myapplicationnote.fragments

import android.content.res.Resources.Theme
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplicationnote.R
import com.example.myapplicationnote.databinding.FragmentAudioPlayerBinding
import com.example.myapplicationnote.model.Note
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class AudioPlayerFragment : Fragment(R.layout.fragment_audio_player) {
    private var audioPlayerBinding: FragmentAudioPlayerBinding? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: ImageButton
    private lateinit var btnback: ImageButton
    private lateinit var btnforward: ImageButton
    private lateinit var speed: Chip
    private lateinit var seekBar: SeekBar
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay=1000L
    private var jumpvalue=1000
    private var playBackSpeed=1.0f
    private lateinit var tvFilename: TextView
    private lateinit var tvTrackProgress: TextView
    private lateinit var tvTrackDuration: TextView
    private val binding get() = audioPlayerBinding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        audioPlayerBinding=FragmentAudioPlayerBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val fileName =
//        val filePath =
//        tvFilename=binding.tvAudioFilename
//        tvTrackDuration=binding.tvTrackDuration
//        tvTrackProgress=binding.tvTrackProgress
//        btnback=binding.btnBack
//        btnforward=binding.btnForward
//        btnPlay=binding.btnPlay
//        speed=binding.chip
//        seekBar=binding.seekbar
//
//       // tvFilename.text=fileName
//        mediaPlayer= MediaPlayer()
//        mediaPlayer.apply {
//            try {
//               // setDataSource(filePath)
//                prepare()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Error: Unable to play audio", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//        tvTrackDuration.text=dateFormat(mediaPlayer.duration)
//        handler= Handler(Looper.getMainLooper())
//        runnable = Runnable {
//            seekBar.progress=mediaPlayer.currentPosition
//            tvTrackProgress.text=dateFormat(mediaPlayer.currentPosition)
//            handler.postDelayed(runnable,delay)
//        }
//        btnPlay.setOnClickListener{
//            playPausePlayer()
//        }
//        seekBar.max =mediaPlayer.duration
//        mediaPlayer.setOnCompletionListener {
//            btnPlay.background=ResourcesCompat.getDrawable(resources,R.drawable.round_play_arrow_24,
//                requireContext().theme
//            )
//            seekBar.progress=mediaPlayer.duration
//            handler.removeCallbacks(runnable)
//        }
//        btnforward.setOnClickListener {
//            mediaPlayer.seekTo(mediaPlayer.currentPosition + jumpvalue)
//            seekBar.progress += jumpvalue
//        }
//        btnback.setOnClickListener {
//            mediaPlayer.seekTo(mediaPlayer.currentPosition - jumpvalue)
//            seekBar.progress -= jumpvalue
//        }
//        speed.setOnClickListener {
//            if (playBackSpeed !=2.0f){
//                playBackSpeed+=0.5f
//            }else{
//                playBackSpeed=0.5f
//            }
//            mediaPlayer.playbackParams= PlaybackParams().setSpeed(playBackSpeed)
//            speed.text="x $playBackSpeed"
//        }
//        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser){
//                    mediaPlayer.seekTo(progress)
//                }
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//        })
//


    }
    private fun dateFormat(duration: Int):String{
        var d=duration/1000
        var s=d%60
        var m=(d/60%60)
        var h=((d-m*60)/360).toInt()
        val f: NumberFormat = DecimalFormat("00")
        var str="$m:${f.format(s)}"
        if (h>0) str="$h:$str"
        return str
    }
    private fun playPausePlayer() {
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
            btnPlay.background=
                ResourcesCompat.getDrawable(resources,R.drawable.round_pause_24,requireContext().theme)
            handler.postDelayed(runnable,delay)
        }else{
            mediaPlayer.pause()
            btnPlay.background= ResourcesCompat.getDrawable(resources,R.drawable.round_play_arrow_24,requireContext().theme)
            handler.removeCallbacks(runnable)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        audioPlayerBinding = null
    }


}


