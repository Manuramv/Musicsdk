package com.thales.musicapp.musicsdk

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.thales.musicapp.MainActivity
import com.thales.musicapp.R
import com.thales.musicapp.musicsdk.receivers.ControlActionsListener
import com.thales.musicapp.musicsdk.utils.*

class MusicService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    val TAG = MusicService::class.java.canonicalName
    // Binder given to clients
    private val iBinder: IBinder = LocalBinder()
    var PLAY_INITIALLY = false
    lateinit var musicFilesListner: MusicFilesListner

    companion object{
        private var mPlayer: MediaPlayer? = null
        fun getIsPlaying() = mPlayer?.isPlaying == true
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"muscis service started, inside oncreate")
        initMusicPlayer()


        //here we can start playing t
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("tag","inside onStartCommand::"+ intent.action)

        val action = intent.action


        when(action) {
            PLAYSONG -> playSong()
            STOPSONG -> stopSong()
            PAUSESONG -> pauseSong()
            GETSONGS -> getAllSongs()
        }

        setupNotification()
        return START_NOT_STICKY

    }

    private fun getAllSongs() {

        val musicFiles = RetreiveMusicFiles.getAllAudios(this)
        if(!musicFiles.isNullOrEmpty())
            ListnerConstant.musicFilesListner.songLoaded(musicFiles!!)
        else
            ListnerConstant.musicFilesListner.onError(Error(getString(R.string.song_loaded_error)))

    }


    fun initMusicPlayer(){
        if (mPlayer != null) {
            return
        }
        try {
            val filename = "android.resource://" + this.packageName + "/raw/numsong"
            mPlayer = MediaPlayer.create(this, Uri.parse(filename))
            mPlayer?.start()
        } catch (ignored: Exception) {
        }
    }


    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        //if(PLAY_INITIALLY)
        mp?.start()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "song event received::onError:"+what)
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        //mp?.start()
    }


    //play song
    private fun playSong(){
        Log.d(TAG, "song event received::play")
        PLAY_INITIALLY = true
        initMusicPlayer()
        mPlayer?.start()
    }
    private fun stopSong(){
        Log.d(TAG, "song event received::stop")
        PLAY_INITIALLY = false
        destroyPlayer()
    }


    private fun pauseSong(){
        Log.d(TAG, "song event received::stop")
        PLAY_INITIALLY = false
        mPlayer?.pause()


    }

    @SuppressLint("NewApi")
    private fun setupNotification() {
        val title = "music title" //later read the  name from the music
        val artist = "Manu" //later read the  name from the music
        /*val artist = mCurrSong?.artist ?: ""
        val playPauseIcon = if (getIsPlaying()) R.drawable.ic_pause_vector else R.drawable.ic_play_vector*/

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

    private fun getContentIntent(): PendingIntent {
        val contentIntent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, contentIntent, 0)
    }

    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, ControlActionsListener::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
    }

    private fun destroyPlayer() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }



}