package com.thales.musicapp.musicsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService
import com.thales.musicapp.musicsdk.models.Song
import com.thales.musicapp.musicsdk.utils.*
import com.thales.musicapp.musicsdk.utils.ListnerConstant

object MusicProvider {
    val TAG = MusicProvider::class.java.canonicalName

    fun startMusicPlayer(context: Context){
        Log.d(TAG, "called startMusicPlayer")
        Intent(context, MusicService::class.java).apply {
            context.startService(this)
        }
    }


    fun playSong(context: Context){
        Intent(context, MusicService::class.java).apply {
            this.action = PLAYSONG
            context.startService(this)
        }
    }
    fun selectedSongFromList(context: Context,index: Int){
        Intent(context, MusicService::class.java).apply {
            this.action = PLAYSELECTEDSONGFROMLIST
            this.putExtra(SELECTEDSONGINDEX, index)
            context.startService(this)
        }
    }

    fun stopSong(context: Context,action:String) {
        Intent(context, MusicService::class.java).apply {
            this.action = action
            context.startService(this)
        }

    }

    fun getSongs(context: Context,musicFilesListner: MusicFilesListner) {
        Intent(context, MusicService::class.java).apply {
            this.action = GETSONGS
            context.startService(this)
            ListnerConstant.musicFilesListner = musicFilesListner
        }

    }


    }