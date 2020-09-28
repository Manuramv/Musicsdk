package com.thales.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thales.musicapp.musicsdk.MusicProvider
import com.thales.musicapp.musicsdk.utils.PLAYSONG
import com.thales.musicapp.musicsdk.utils.STOPSONG
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MusicProvider.startMusicPlayer(this)

        startMusic.setOnClickListener({
            MusicProvider.playSong(this,PLAYSONG)
        })

        stopMusic.setOnClickListener({
            MusicProvider.stopSong(this,STOPSONG)
        })


    }
}