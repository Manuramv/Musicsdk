package com.thales.musicapp.musicsdk.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startForegroundService
import com.thales.musicapp.musicsdk.MusicService

class ControlActionsListener : BroadcastReceiver() {

   /* override fun onReceive(context: Context, intent: Intent) {
        Log.d("tag","inside ControlActionsListener onrecieve::"+ intent.action)
        val action = intent.action
        context.sendIntent(action)



    }*/

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        sendIntent(context,action)
    }

    fun sendIntent(context: Context, action: String?) {
        Intent(context, MusicService::class.java).apply {
            this.action = action
            try {
                startForegroundService(context,this)
            } catch (ignored: Exception) {
            }
        }
    }

}
