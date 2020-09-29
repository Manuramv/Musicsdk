package com.thales.musicsdk.musicsdk

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.thales.musicsdk.R
import com.thales.musicsdk.musicsdk.models.Song
import com.thales.musicsdk.musicsdk.receivers.ControlActionsListener
import com.thales.musicsdk.musicsdk.utils.*
import com.thales.musicsdk.musicsdk.utils.FileUtils.getFileName
import java.io.File

internal class MusicService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    val TAG = MusicService::class.java.canonicalName
    // Binder given to clients
    private val iBinder: IBinder = LocalBinder()
    var  musicFiles :List<Song>?=null
    var mCurrentSong: Song?=null
    var pos:Int = 0


    companion object{
        private var mPlayer: MediaPlayer? = null
        private lateinit var contentIntentActivity: Activity

        /**
         * getting the song playing stats to display the music play time
         */
        fun getIsPlaying() = mPlayer?.isPlaying == true
        /**
         * Method to set the setContentIntentActivity for the notification
         * @param activity - setContentIntentActivity, Activity to be opened when clicking on the notification
         */
        fun setContentIntentActivity(activity: Activity)  {
            contentIntentActivity = activity
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"muscis service started, inside oncreate")
        initMusicPlayer()
    }

    /**
     * When music app request to start service/component this method gets invoked
     * @param intent - Intent of the specific operations to do.
     * in our case play ,pause, stop music and get songs.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("tag","inside onStartCommand::"+ intent.action)
        val action = intent.action
        when(action) {
            PLAYSONG -> playSong()
            STOPSONG -> stopSong()
            PAUSESONG -> pauseSong()
            GETSONGS -> getAllSongs()
            PLAYSELECTEDSONGFROMLIST -> playSelectedSongFromList(intent)
        }
        setupNotification()
        return START_NOT_STICKY

    }
    /**
     * Method to get all the songs from device
     * It will return the success callback to the end user if it's found any songs else failure callback
     */
    private fun getAllSongs() {
        musicFiles = FileUtils.getAllAudios(this)
        if(!musicFiles.isNullOrEmpty())
            ListnerConstant.musicFilesListner.songLoaded(musicFiles!!)
        else
            ListnerConstant.musicFilesListner.onError(Error(getString(R.string.song_loaded_error)))
    }
    /**
     * Method to play the selected song from the list
     * @param intent Pass the index of the song as intent
     */
    private fun playSelectedSongFromList(intent: Intent) {
        if(!musicFiles.isNullOrEmpty()){
            //val filename = "android.resource://" + this.packageName + "/raw/numsong"
            pos = intent.getIntExtra(SELECTEDSONGINDEX, 0)
            Log.d(TAG,"seleced index::"+pos)
            mPlayer?.reset()
            if(musicFiles?.get(pos)!=null)
                mCurrentSong = musicFiles?.get(pos)!!
            val songPath = Uri.fromFile(File(mCurrentSong?.filePath))
            mPlayer = MediaPlayer.create(this,songPath)
            mPlayer?.start()
        }
    }


    /**
     * Initalizing the music player and playing the user selected song
     */
    fun initMusicPlayer(){
        if (mPlayer != null) {
            return
        }
        try {
            if(musicFiles?.get(pos)!=null)
                mCurrentSong = musicFiles?.get(pos)!!
            val songPath = Uri.fromFile(File(mCurrentSong?.filePath))
            mPlayer = MediaPlayer.create(this, songPath)
            mPlayer?.start()
        } catch (ignored: Exception) {
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "song event received::onError:"+what)
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
    }


    /**
     * Method to set the setContentIntentActivity for the notification
     * @param activity - setContentIntentActivity, Activity to be opened when clicking on the notification
     */
    private fun playSong(){
        Log.d(TAG, "song event received::play")
        initMusicPlayer()
        mPlayer?.start()
    }
    /**
     * Method to stop the song and destroy the player
     */
    private fun stopSong(){
        Log.d(TAG, "song event received::stop")
        destroyPlayer()
    }

    /**
     * Method to pause the song
     */
    private fun pauseSong(){
        Log.d(TAG, "song event received::stop")
        mPlayer?.pause()


    }

    /**
     * Method to set up the notification and starting the foreground notification. since we are targetting for OS version greater than 8 not handling the lower version cases.
     * Adding the action icons and pending intent.
     *
     */
    @SuppressLint("NewApi")
    private fun setupNotification() {
        val title = "Thales Music Player" //later read the  name from the music
        val artist = getFileName(mCurrentSong?.name?:"") //later read the  name from the music
        /*val artist = mCurrSong?.artist ?: ""*/

        var notifWhen = 0L
        var showWhen = false
        var usesChronometer = false
        var ongoing = false
        if (getIsPlaying()) {
            notifWhen = System.currentTimeMillis() - (mPlayer?.currentPosition ?: 0)
            showWhen = true
            usesChronometer = true
            ongoing = true
        }


            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "notifname"
            val importance = NotificationManager.IMPORTANCE_LOW
            NotificationChannel(NOTIFICATION_CHANNEL, name, importance).apply {
                enableLights(false)
                enableVibration(false)
                notificationManager.createNotificationChannel(this)
            }

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_headset)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setWhen(notifWhen)
            .setShowWhen(showWhen)
            .setUsesChronometer(usesChronometer)
            .setContentIntent(getContentIntent())
            .setOngoing(ongoing)
            .setChannelId(NOTIFICATION_CHANNEL)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2))
            .addAction(R.drawable.ic_play_circle, getString(R.string.play), getIntent(PLAYSONG))
            .addAction(R.drawable.ic__pause_circle, getString(R.string.pause), getIntent(PAUSESONG))
            .addAction(R.drawable.ic_stop_24, getString(R.string.stop), getIntent(STOPSONG))



        startForeground(NOTIFICATION_ID, notification.build())

        // delay foreground state updating a bit, so the notification can be swiped away properly after initial display
        Handler(Looper.getMainLooper()).postDelayed({
            if (!getIsPlaying()) {
                stopForeground(false)
            }
        }, 200L)

    }

    private fun  getContentIntent(): PendingIntent {
        val contentIntent = Intent(this, contentIntentActivity::class.java)
        return PendingIntent.getActivity(this, 0, contentIntent, 0)
    }

    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, ControlActionsListener::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
    }

    /**
     * Destroy the music player and release the memory
     */
    private fun destroyPlayer() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }



}