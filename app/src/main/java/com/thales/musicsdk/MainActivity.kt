package com.thales.musicsdk

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.thales.musicsdk.musicsdk.MusicProvider
import com.thales.musicsdk.musicsdk.`interface`.SongClickListner
import com.thales.musicsdk.musicsdk.adapters.SongAdapter
import com.thales.musicsdk.musicsdk.models.Song
import com.thales.musicsdk.musicsdk.utils.MusicFilesListner
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SongClickListner {
    private val TAG = "MainActivity"
    private val RECORD_REQUEST_CODE = 101
    lateinit var songClickListner: SongClickListner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        songClickListner = this
        setupPermissions()

        startMusic.setOnClickListener({
            MusicProvider.playSong(this)
        })
        pauseMusic.setOnClickListener({
            MusicProvider.pauseSong(this)
        })

        stopMusic.setOnClickListener({
            MusicProvider.stopSong(this)
        })
        getSongs.setOnClickListener({
            MusicProvider.getSongs(this, object :MusicFilesListner{
                override fun songLoaded(songs: List<Song>) {
                    Log.d(TAG,"song loaded::"+songs)
                    setUpAdapter(songs)

                }

                override fun onError(error: Throwable) {
                    Log.d(TAG,"song loaded onError::"+error)
                }

            })
        })

    }

    fun setUpAdapter(songList: List<Song>){
        val adapter = SongAdapter(songClickListner)
        rvMusicList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        rvMusicList.adapter = adapter
        adapter.setItems(songList as ArrayList<Song>)
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

    override fun onSongSelected(index: Int) {
        Log.i(TAG, "on Song selected::"+ index)
        MusicProvider.selectedSongFromList(this,index)
    }

}