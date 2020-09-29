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

    /**
     * The HelloWorld program implements an application that
     * simply displays "Hello World!" to the standard output.
     *
     * @author  Zara Ali
     * @version 1.0
     * @since   2014-03-31
     */
    companion object {
            @Volatile
            var INSTANCE: MusicProvider? = null
        /**
         * creating singleton class and also to avoid sending different contentIntentActivity each time from user.
         * @param contentIntentActivity - This activity will open when user tap on the notification
         *
         */
            @Synchronized
            fun getInstance(context: Context, contentIntentActivity: Activity): MusicProvider
                    = INSTANCE ?: MusicProvider(context,contentIntentActivity).also {
                INSTANCE = it
                setContentIntentActivity(activity = contentIntentActivity)
            }
    }

    /**
     * Starting the music player
     * @param context - Activity context
     *
     */
    fun startMusicPlayer(context: Context){
        Log.d(TAG, "called startMusicPlayer")
        Intent(context, MusicService::class.java).apply {
            context.startService(this)
        }
    }


    /**
     * playing the song (only if there is any song is paused
     * @param context - Activity context
     *
     */
    fun playSong(context: Context){
        Intent(context, MusicService::class.java).apply {
            this.action = PLAYSONG
            context.startService(this)
        }
    }
    /**
     * pausing the song
     * @param context - Activity context
     *
     */
    fun pauseSong(context: Context){
        Intent(context, MusicService::class.java).apply {
            this.action = PAUSESONG
            context.startService(this)
        }
    }

    /**
     * this method allow users to select the song which one user wants to play
     * @param context - Activity context
     * @param index - Index of the song wants to play
     *
     */
    fun selectedSongFromList(context: Context,index: Int){
        Intent(context, MusicService::class.java).apply {
            this.action = PLAYSELECTEDSONGFROMLIST
            this.putExtra(SELECTEDSONGINDEX, index)
            context.startService(this)
        }
    }

    /**
     * stop the song - stop the exiting song and release the media player
     * @param context - Activity context
     *
     */
    fun stopSong(context: Context) {
        Intent(context, MusicService::class.java).apply {
            this.action = STOPSONG
            context.startService(this)
        }

    }
    /**
     * Method to return all the music files from the device
     * @param context - Activity context
     * @param musicFilesListner - Succes or failure listner .. If the SDK is able to get the files it will return success calback to user.
     * User can show the list of songs or error msg based on the callback
     *
     */
    fun getSongs(context: Context,musicFilesListner: MusicFilesListner) {
        Intent(context, MusicService::class.java).apply {
            this.action = GETSONGS
            context.startService(this)
            ListnerConstant.musicFilesListner = musicFilesListner
        }

    }

    }