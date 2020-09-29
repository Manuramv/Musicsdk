package com.thales.musicsdk.musicsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.thales.musicsdk.musicsdk.MusicService.Companion.setContentIntentActivity
import com.thales.musicsdk.musicsdk.utils.*
import com.thales.musicsdk.musicsdk.utils.ListnerConstant

class MusicProvider private constructor(context: Context, contentIntentActivity: Activity) {
    val TAG = MusicProvider::class.java.canonicalName

    companion object {
            @Volatile
            var INSTANCE: MusicProvider? = null

            @Synchronized
            fun getInstance(context: Context, contentIntentActivity: Activity): MusicProvider
                    = INSTANCE ?: MusicProvider(context,contentIntentActivity).also {
                INSTANCE = it
                setContentIntentActivity(activity = contentIntentActivity)
            }
    }

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

    fun pauseSong(context: Context){
        Intent(context, MusicService::class.java).apply {
            this.action = PAUSESONG
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

    fun stopSong(context: Context) {
        Intent(context, MusicService::class.java).apply {
            this.action = STOPSONG
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