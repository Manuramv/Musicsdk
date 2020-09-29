package com.thales.musicapp.musicsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startForegroundService
import com.thales.musicapp.musicsdk.utils.GETSONGS
import com.thales.musicapp.musicsdk.utils.ListnerConstant
import com.thales.musicapp.musicsdk.utils.MusicFilesListner
import com.thales.musicapp.musicsdk.utils.PLAYSONG

object MusicProvider {
    val TAG = MusicProvider::class.java.canonicalName

    fun startMusicPlayer(context: Context){
        Log.d(TAG, "called startMusicPlayer")
        Intent(context, MusicService::class.java).apply {
            context.startService(this)
        }
    }


    fun playSong(context: Context,action:String){
        Intent(context, MusicService::class.java).apply {
            this.action = action
            context.startService(this)
        }

        /*Intent(this, MusicService::class.java).apply {
            this.action = action
            try {
                *//*if (isOreoPlus()) {
                    startForegroundService(this)
                } else {
                    startService(this)
                }*//*
            } catch (ignored: Exception) {
            }
        }*/

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