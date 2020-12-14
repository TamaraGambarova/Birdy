package com.example.birdyapp.features.searching_by_name.view

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.birdyapp.R
import com.example.birdyapp.databinding.ActivityRecordAudioBinding
import com.example.birdyapp.util.ToastManager
import io.grpc.Channel
import kotlinx.android.synthetic.main.activity_record_audio.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class AudioRecordActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()

    private lateinit var channel: Channel
    private val toastManager: ToastManager by instance()

    private var fileName: String = ""

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    var mStartPlaying = true
    var mStartRecording = true

    var countDownTimer: CountDownTimer? = null
    var second = -1
    var minute: Int = 0
    var hour: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val binding: ActivityRecordAudioBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_record_audio)
        binding.lifecycleOwner = this
        binding.activity = this

        initButtons()
        initToolbar()
    }

    private fun initButtons() {
        record.setOnClickListener {
            onRecord(mStartRecording)

            mStartRecording = !mStartRecording
        }
        play.setOnClickListener {
            onPlay(mStartPlaying)

            mStartPlaying = !mStartPlaying
        }
        send.setOnClickListener {
            val audioBytes = if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                getAudioBytesArray()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }
    }

    private fun initToolbar() {
        toolbar.title_text_view.text = getString(R.string.record_audio)
    }

    private fun onRecord(start: Boolean) = if (start) {
        record.setImageResource(R.drawable.ic_stop)
        record_label.text = getString(R.string.stop)
        showTimer()
        startRecording()
    } else {
        record.setImageResource(R.drawable.ic_record)
        record_label.text = getString(R.string.record)
        countDownTimer?.cancel()
        time.text = getString(R.string.start_time)
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        play.setImageResource(R.drawable.ic_pause)
        play_label.text = getString(R.string.pause)
        startPlaying()
    } else {
        play.setImageResource(R.drawable.ic_play)
        play_label.text = getString(R.string.play)
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }

            start()
        }
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun showTimer() {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                second++
                time.text = recorderTime()
            }

            override fun onFinish() {}
        }
        (countDownTimer as CountDownTimer).start()
    }

    private fun recorderTime(): String {
        if (second == 60) {
            minute++
            second = 0
        }
        if (minute == 60) {
            hour++
            minute = 0
        }
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAudioBytesArray(): ByteArray {
        return Files.readAllBytes(Paths.get(fileName))
    }
}