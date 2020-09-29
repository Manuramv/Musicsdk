package com.thales.musicapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.thales.musicapp.musicsdk.MusicProvider
import com.thales.musicapp.musicsdk.utils.GETSONGS
import com.thales.musicapp.musicsdk.utils.PLAYSONG
import com.thales.musicapp.musicsdk.utils.STOPSONG
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()

        startMusic.setOnClickListener({
            MusicProvider.playSong(this,PLAYSONG)
        })

        stopMusic.setOnClickListener({
            MusicProvider.stopSong(this,STOPSONG)
        })
        getSongs.setOnClickListener({
            MusicProvider.stopSong(this, GETSONGS)
        })





    }

    private fun setupPermissions() {
        val permission = checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    MusicProvider.startMusicPlayer(this)
                }
            }
        }
    }
}